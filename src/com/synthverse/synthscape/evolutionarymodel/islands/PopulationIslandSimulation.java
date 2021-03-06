package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.stacks.Program;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Broadcast;
import com.synthverse.synthscape.core.EventStats;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Team;
import com.synthverse.synthscape.core.Trail;
import com.synthverse.synthscape.core.Unicast;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedAgent;
import com.synthverse.util.GridUtils;
import com.synthverse.util.LogUtils;
import com.synthverse.util.StringUtils;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

@SuppressWarnings("serial")
public class PopulationIslandSimulation extends Simulation {

	/**
	 * Thread just runs the Population Island Simulator
	 * 
	 * @author sadat
	 *
	 */
	public static class CoreSimThread implements Runnable {
		String[] args;

		public CoreSimThread() {
			this.args = Main.settings.originalArgs;
		}

		@Override
		public void run() {
			PopulationIslandSimulation.main(args);
		}
	}

	private Team team = new Team();

	int generation = 0;
	long reportTime = System.currentTimeMillis();
	long deltaTime = 0;
	long allocatedMemory = 0;

	public static Settings settings = Settings.getInstance();

	private static SummaryStatistics populationFitnessStats = new SummaryStatistics();

	EnumMap<Species, EventStats> speciesEventStatsMap = new EnumMap<Species, EventStats>(Species.class);

	private static Logger logger = Logger.getLogger(PopulationIslandSimulation.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public PopulationIslandSimulation(long seed) throws Exception {
		super(seed);
		initSpeciesEventStats();

	}

	public final void clearSpeciesEventStats() {
		for (EventStats es : speciesEventStatsMap.values()) {
			es.clear();
		}
	}

	public final void initSpeciesEventStats() {
		for (Species species : speciesComposition) {
			EventStats es = new EventStats();
			speciesEventStatsMap.put(species, es);
		}
	}

	public static void main(String[] arg) {
		String[] manualArgs = StringUtils.parseArguments("-repeat " + settings.REPEAT + " -seed " + settings.SEED);
		doLoop(PopulationIslandSimulation.class, manualArgs);
		/*
		logger.info("Diagnosis: total # of agents created: " + Agent.get_optimazationTotalAgentsCounters());
		logger.info("Diagnosis: total # of islander agents created: "
				+ IslanderAgent.get_optimizationIslanderAgentCounter());
		logger.info("Diagnosis: total # of embodied agents created: "
				+ EmbodiedAgent.get_optimizationEmbodiedAgentCounter());

		*/
		//System.exit(0);
	}

	private void initTeam() {
		int teamId = team.getTeamId();

		teamId++;
		team.init();
		team.setTeamId(teamId);
		team.setExpectedSize(speciesComposition.size() * clonesPerSpecies);

	}

	@Override
	protected void initAgents() {
		// populate with agents
		initTeam();

		MersenneTwisterFast randomPrime = this.random;
		agents.clear();

		int previousX = 0;
		int previousY = 0;

		for (Species species : speciesComposition) {

			for (int i = 0; i < clonesPerSpecies; i++) {

				int randomX = randomPrime.nextInt(gridWidth);
				int randomY = randomPrime.nextInt(gridHeight);

				if (!settings.CLUSTERED) {
					while (GridUtils.gridHasAnObjectAt(collisionGrid, randomX, randomY)) {
						randomX = randomPrime.nextInt(gridWidth);
						randomY = randomPrime.nextInt(gridHeight);
					}
				}

				if (settings.CLUSTERED) {
					Int2D clusterLocation = getClusterLocation(i, previousX, previousY);
					randomX = clusterLocation.x;
					randomY = clusterLocation.y;

				}

				GridUtils.set(collisionGrid, randomX, randomY, true);

				previousX = randomX;
				previousY = randomY;

				Agent agent = evolver.getAgent(species, randomX, randomY);

				agent.setProvidedFeedback(false);
				team.addMember(agent);
				agent.setTeam(team);

				agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));

				agents.add(agent);

				// add agents to the scheduler

				if (!agent.isScheduled()) {
					schedule.scheduleRepeating(agent);

					agent.setScheduled(true);
				}

			}

		}

	}

	@Override
	protected void doEndOfSimulationTasks() {
		for (Agent agent : agents) {
			agent.eventStats.aggregateStatsTo(simEventStats);
			agent.eventStats.aggregateStatsTo(experimentEventStats);

			EventStats speciesEventStats = speciesEventStatsMap.get(agent.getSpecies());
			agent.eventStats.aggregateStatsTo(speciesEventStats);
		}
		// simEventStats.printEventTypeStats();

		reclaimAgents();
		this.evolver.provideFeedback(agents, simEventStats);

		captureStats.addValue(this.numberOfCollectedResources);

		collectResouceCaptureStats();

		if (this.numberOfCollectedResources > this.maxResourcesEverCollected) {
			this.maxResourcesEverCollected = this.numberOfCollectedResources;
		}

	};

	@Override
	protected void startSimulation() {

		String seedString = Integer.toString(settings.SEED, 16);

		logger.info("EXPERIMENT STARTS ["+seedString+"]: expected maxium simulations =" + simulationsPerExperiment
				+ " stepsPerSimulation=" + stepsPerSimulation);

		initEnvironment();

		initAgents();

		saveEnvironmentForBenchmark();

		logger.info("---- starting simulation (" + simulationCounter + ") with: world=" + (gridHeight * gridWidth)
				+ " obstacles=" + numberOfObstacles + " sites=" + numberOfCollectionSites + " resources="
				+ numberOfResources + " agents=" + agents.size());

		setStartDate();

		// this synchronizes the seed value for repeated experiments
		settings.SEED = (int) this.seed();
		experimentReporter.initReporter();

		reportTime = System.currentTimeMillis();

		attachVisualizationGrids();

		// before we start stepping, let's synchronize with visualizer, if any
		unlockGenerationalVisualizer();

		// this is run at the end of each step
		Main.settings.__simulationStarted = true;
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
			public void step(SimState state) {

				simStepCounter++;

				if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
					fadeTrails();
				}

				if (Main.settings.PEER_REWARDS) {
					fadeRewardGrids();
				}

				if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
					ageBroadcasts();
				}

				doEndOfStepTasks();

				// check if simulation should continue...

				if (evaluateSimulationTerminateCondition()) {

					doEndOfSimulationTasks();

					simStepCounter = 0;
					simulationCounter++;
					simsRunForThisGeneration++;

					if (!collectedAllResources() && simulationCounter < simulationsPerExperiment) {

						if (simulationCounter % settings.GENE_POOL_SIZE == 0) {
							// we clear up the replication queue prior to start
							// evolving
							// the evolutionary process will create new entries
							// in replication
							// queue
							birthQueue.clear();
							deathQueue.clear();
							evolvePopulationIslands();
							simsRunForThisGeneration = 0;
						}

						startNextSimulation();

					} else {
						// end of experiment...
						if (collectedAllResources()) {
							logger.info("!!!ALL RESOURCES COLLECTED!!!");
						}
						// evolvePopulationIslands(); // this does one last line
						// of recording...
						setEndDate();
						experimentReporter.cleanupReporter();
						finish();
						logger.info("<=====  EXPERIMENT ENDS\n");
						Trail.resetSendReceiveCounters();
						Broadcast.resetSendReceiveCounters();
						Unicast.resetSendReceiveCounters();
						// settings.resetRandomSeedIfNeeded(state);
					}
				}
			}

		}, 1);

	}

	protected void evolvePopulationIslands() {

		populationFitnessStats.clear();

		ArchipelagoEvolver archipelagoEvolver = (ArchipelagoEvolver) evolver;

		generation++;

		for (PopulationIslandEvolver islandEvolver : archipelagoEvolver.speciesIslandMap.values()) {
			if (islandEvolver.evolve() != generation) {
				logger.severe("invalid evolution algorithm implementation");
				System.exit(1);

			}

			for (double fitnessValue : islandEvolver.evolutionEngine.fitnessStats.getValues()) {
				populationFitnessStats.addValue(fitnessValue);
			}

			// this part computes the genetic distance between the current and
			// previous alpha

			double alphaGeneticDistance = Program.comparePrograms(islandEvolver.evolutionEngine.alphaProgram,
					islandEvolver.evolutionEngine.previousAlphaProgram);

			if (alphaGeneticDistance != Double.NaN) {
				islandEvolver.computedAlphaDistance = alphaGeneticDistance;

			}

			experimentReporter.reportAlphaProgram(generation, islandEvolver.species.getId(), islandEvolver.species,
					islandEvolver.evolutionEngine.alphaProgram);

		}

		// resourceCaptureStats.printStats();

		allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
		long currentTime = System.currentTimeMillis();
		deltaTime = currentTime - reportTime;

		experimentReporter.reportPerformanceIslandModel(generation, intervalStats, simEventStats, speciesEventStatsMap,
				captureStats, populationFitnessStats, simsRunForThisGeneration, resourceCaptureStats,
				archipelagoEvolver.speciesIslandMap, this.simulationCounter, deltaTime, allocatedMemory);

		printGenerationalStats();
		reportTime = currentTime;

		clearSpeciesEventStats();
		captureStats.clear();
		populationFitnessStats.clear();

		intervalStats.clear();
		resourceCaptureStats.clearAll();
	}

	@Override
	public int configGridWidth() {
		return settings.WORLD_WIDTH;
	}

	@Override
	public int configGridHeight() {
		return settings.WORLD_HEIGHT;
	}

	@Override
	public double configObstacleDensity() {
		return settings.OBSTACLE_DENSITY;
	}

	@Override
	public double configResourceDensity() {
		return settings.RESOURCE_DENSITY;
	}

	@Override
	public EnumSet<InteractionMechanism> configInteractionMechanisms() {
		EnumSet<InteractionMechanism> mechanisms = EnumSet.noneOf(InteractionMechanism.class);

		if (settings.MODEL_INTERACTIONS.contains("none")) {
			logger.info("Agents will not use any interaction instructions");
		} else {

			if (settings.MODEL_INTERACTIONS.contains("trail")) {
				mechanisms.add(InteractionMechanism.TRAIL);
				logger.info("Agents will use any TRAIL interactions...");
			}

			if (settings.MODEL_INTERACTIONS.contains("broadcast")) {
				mechanisms.add(InteractionMechanism.BROADCAST);
				logger.info("Agents will use any BROADCAST interactions...");
			}

			if (settings.MODEL_INTERACTIONS.contains("unicast_n")) {
				mechanisms.add(InteractionMechanism.UNICAST_CLOSEST_AGENT);
				logger.info("Agents will use any UNICAST_CLOSEST_AGENT interactions...");
			}

			if (settings.MODEL_INTERACTIONS.contains("unicast_g")) {
				mechanisms.add(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
				logger.info("Agents will use any UNICAST_CLIQUE_MEMBER interactions...");
			}
		}
		return mechanisms;
	}

	@Override
	public ProblemComplexity configProblemComplexity() {
		return settings.PROBLEM_COMPLEXITY;
	}

	@Override
	public int configClonesPerSpecies() {
		return settings.CLONES_PER_SPECIES;
	}

	@Override
	public int configNumberOfCollectionSites() {
		return settings.NUMBER_OF_COLLECTION_SITES;
	}

	@Override
	public int configMaxStepsPerAgent() {
		return settings.MAX_STEPS_PER_AGENT;
	}

	@Override
	public boolean configIsReportEvents() {
		return REPORT_EVENTS;
	}

	@Override
	public boolean configIsReportPerformance() {
		return REPORT_PERFORMANCE;
	}

	@Override
	public String configExperimentName() {
		return "POPULATION_ISLAND_MANUAL";
	}

	@Override
	public int configSimulationsPerExperiment() {
		return settings.SIMS_PER_EXPERIMENT;
	}

	@Override
	public int configStepsPerSimulation() {
		return settings.MAX_STEPS_PER_AGENT;
	}

	@Override
	public String configEventFileName() {
		return settings.EVENT_DATA_FILE;
	}

	@Override
	public Evolver configEvolver() {
		return new ArchipelagoEvolver(this, getAgentFactory());
	}

	@Override
	public AgentFactory configAgentFactory() {
		return new IslanderAgentFactory(this);
	}

	@Override
	public int configGenePoolSize() {
		return settings.GENE_POOL_SIZE;
	}

	@Override
	public AgentFactory configEmbodiedAgentFactory() {
		// there is no internal agent factory
		return null;
	}

	@Override
	public void printGenerationalStats() {
		logger.info("gen: " + generation + "; sims: " + this.simulationCounter + "; fitness: "
				+ populationFitnessStats.getMean() + "; best_capture: " + captureStats.getMax() + " time_delta_ms="
				+ deltaTime + " allocMB=" + allocatedMemory);

	}

}
