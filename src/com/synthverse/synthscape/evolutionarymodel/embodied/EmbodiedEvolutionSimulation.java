package com.synthverse.synthscape.evolutionarymodel.embodied;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.stacks.Program;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Broadcast;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.EventStats;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.ExperimentReporter;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Team;
import com.synthverse.synthscape.core.Trail;
import com.synthverse.synthscape.core.Unicast;
import com.synthverse.synthscape.evolutionarymodel.islands.IslanderAgent;
import com.synthverse.synthscape.evolutionarymodel.islands.IslanderAgentFactory;
import com.synthverse.util.GridUtils;
import com.synthverse.util.LogUtils;
import com.synthverse.util.StringUtils;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

@SuppressWarnings("serial")
public class EmbodiedEvolutionSimulation extends Simulation {

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
			EmbodiedEvolutionSimulation.main(args);
		}
	}

	private Team team = new Team();

	/**
	 * EventStats for the entire generation from the combined Pools
	 */
	EventStats generationEventStats = new EventStats();

	public static Settings settings = Settings.getInstance();

	private static Logger logger = Logger.getLogger(EmbodiedEvolutionSimulation.class.getName());

	private static SummaryStatistics populationFitnessStats = new SummaryStatistics();

	EnumMap<Species, EventStats> speciesEventStatsMap = new EnumMap<Species, EventStats>(Species.class);

	int generation = 0;

	long reportTime = System.currentTimeMillis();
	long deltaTime = 0;
	long allocatedMemory = 0;

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public EmbodiedEvolutionSimulation(long seed) throws Exception {
		super(seed);
		initSpeciesEventStats();
	}

	public static void main(String[] arg) {
		String[] manualArgs = StringUtils.parseArguments("-repeat " + settings.REPEAT + " -seed " + settings.SEED);
		doLoop(EmbodiedEvolutionSimulation.class, manualArgs);
		/*
		logger.info("Diagnosis: total # of agents created: " + Agent.get_optimazationTotalAgentsCounters());
		logger.info("Diagnosis: total # of islander agents created: "
				+ IslanderAgent.get_optimizationIslanderAgentCounter());
		logger.info("Diagnosis: total # of embodied agents created: "
				+ EmbodiedAgent.get_optimizationEmbodiedAgentCounter());
		*/
		//System.exit(0);
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

	private void initTeam() {
		int teamId = team.getTeamId();

		teamId++;
		team.init();
		team.setTeamId(teamId);
		team.setExpectedSize(speciesComposition.size() * clonesPerSpecies);

	}

	protected void init() throws Exception {
		this.showGraphics = settings.__showGraphics;

		// we can compute the server name and batch ID right away
		settings.experimentNumber++;
		try {
			serverName = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			serverName = "LOCAL";
		}
		batchId = Long.toHexString(System.currentTimeMillis());

		// InstructionTranslator.logStatus();

		setGenePoolSize(configGenePoolSize());
		setReportEvents(configIsReportEvents());
		setReportPerformance(configIsReportPerformance());

		setEventFileName(configEventFileName());

		// now set these up based on the concrete simulation

		setExperimentName(configExperimentName());
		setProblemComplexity(configProblemComplexity());

		setMaxStepsPerAgent(configMaxStepsPerAgent());

		// environmental stuff
		setGridWidth(configGridWidth());
		setGridHeight(configGridHeight());
		setNumberOfCollectionSites(configNumberOfCollectionSites());
		setObstacleDensity(configObstacleDensity());
		setResourceDensity(configResourceDensity());

		// interactions
		setInteractionMechanisms(configInteractionMechanisms());

		// species compositions
		setClonesPerSpecies(configClonesPerSpecies());
		setSpeciesComposition(configSpeciesComposition());

		// steps and simulations...
		setStepsPerSimulation(configStepsPerSimulation());
		setSimulationsPerExperiment(configSimulationsPerExperiment());

		// agent factory and activeEvolver...
		setAgentFactory(configAgentFactory());
		setEmbodiedAgentFactory(configEmbodiedAgentFactory());

		// set these variables
		double gridArea = gridWidth * gridHeight;
		numberOfObstacles = (int) (gridArea * obstacleDensity);
		numberOfResources = (int) (gridArea * resourceDensity);
		resourceCaptureGoal = (int) ((double) numberOfResources * settings.PERC_RESOURCE_CAPTURE_GOAL);

		createDataStructures();

		simulationCounter = 0;
		simsRunForThisGeneration = 0;

		numberOfCollectedResources = 0;

		experimentReporter = new ExperimentReporter(this, DEFAULT_FLUSH_ALWAYS_FLAG);

		isToroidalWorld = TOROIDAL_FLAG;

	}

	protected void initCachedAgents() {

		int cacheSize = Main.settings.DE_MAX_POPULATION
				- ((speciesComposition.size() - 1) * Main.settings.DE_INITIAL_CLONES);

		List<EmbodiedAgent> agentList = new ArrayList<EmbodiedAgent>();
		for (Species species : speciesComposition) {

			for (int i = 0; i < cacheSize; i++) {
				EmbodiedAgent embodiedAgent = (EmbodiedAgent) agentFactory.getNewFactoryAgent(species);
				agentList.add(embodiedAgent);

			}
			for (EmbodiedAgent agent : agentList) {
				agentFactory.reclaimAgent(agent);
			}

		}
		logger.info("done");
	}

	@Override
	protected void initAgents() {

		HashMap<Species, Integer> speciesCloneCreateMap = new HashMap<Species, Integer>();

		// populate with agents
		initTeam();

		MersenneTwisterFast randomPrime = this.random;
		agents.clear();

		int previousX = 0;
		int previousY = 0;

		int numSpecies = speciesComposition.size();
		int totalPop = 0;
		int speciesCount = 0;

		for (Species species : speciesComposition) {
			// by default the number of clones is what has been set by default
			int numClones = clonesPerSpecies;
			speciesCount++;

			if (Main.settings.MANUAL_EVENNESS) {
				// if we are running with manual evenness settings, then we can
				// have
				// two possible cases: random evennness or fixed evenness
				// depending on either of these two cases, we pick the
				// appropriate number
				// of clones
				if (Main.settings.ME_RANDOM_POP_RATIO) {
					// case 1: random ratio
					if (speciesCount != numSpecies) {
						numClones = random.nextInt(Main.settings.ME_MAX_POPULATION - totalPop - numSpecies) + 1;
					} else {
						numClones = Main.settings.ME_MAX_POPULATION - totalPop;
					}
					totalPop += numClones;
				} else {
					// case 2: fixed ratio
					String ratioString = Main.settings.ME_POP_RATIO;
					String[] speciesProportions = ratioString.split(":");
					try {
						int detectorRatio = Integer.parseInt(speciesProportions[0]);
						int extractorRatio = Integer.parseInt(speciesProportions[1]);
						int transporterRatio = Integer.parseInt(speciesProportions[2]);
						int processorRatio = 0;
						int total = detectorRatio + extractorRatio + transporterRatio;

						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
							processorRatio = Integer.parseInt(speciesProportions[3]);
							total += processorRatio;
						}

						if (species == Species.DETECTOR) {
							numClones = (int) (((double) detectorRatio / total) * Main.settings.ME_MAX_POPULATION);
						} else if (species == Species.EXTRACTOR) {
							numClones = (int) (((double) extractorRatio / total) * Main.settings.ME_MAX_POPULATION);
						} else if (species == Species.TRANSPORTER) {
							numClones = (int) (((double) transporterRatio / total) * Main.settings.ME_MAX_POPULATION);
						} else if (species == Species.PROCESSOR) {
							numClones = (int) (((double) processorRatio / total) * Main.settings.ME_MAX_POPULATION);
						}

					} catch (Exception e) {
						logger.severe("unable to parse rato string:" + ratioString + "  "+e.getMessage()+" ... exitiing!");
						System.exit(1);
					}

				}

				if (species == Species.DETECTOR) {
					Main.settings.__numDetectors = numClones;
				} else if (species == Species.EXTRACTOR) {
					Main.settings.__numExtractors = numClones;
				} else if (species == Species.TRANSPORTER) {
					Main.settings.__numTransporters = numClones;
				} else if (species == Species.PROCESSOR) {
					Main.settings.__numProcessors = numClones;
				}

			}
			speciesCloneCreateMap.put(species, numClones);

		}

		for (Species species : speciesComposition) {

			for (int i = 0; i < speciesCloneCreateMap.get(species); i++) {

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

				EmbodiedAgent embodiedAgent = (EmbodiedAgent) agentFactory.getNewFactoryAgent(species);
				embodiedAgent.setLocation(randomX, randomY);
				agentGrid.setObjectLocation(embodiedAgent, new Int2D(randomX, randomY));

				embodiedAgent.synchronizeLocationToActiveAgent();

				team.addMember(embodiedAgent);
				embodiedAgent.setTeam(team);

				agents.add(embodiedAgent);

				if (!embodiedAgent.isScheduled()) {
					schedule.scheduleRepeating(embodiedAgent);

					embodiedAgent.setScheduled(true);
				}

			}

		}

	}

	protected void addNewAgentBySpecies(Species species, boolean isProgenitor) {

		if (agents.size() < Main.settings.DE_MAX_POPULATION) {

			MersenneTwisterFast randomPrime = this.random;

			int randomX = randomPrime.nextInt(gridWidth);
			int randomY = randomPrime.nextInt(gridHeight);

			while (GridUtils.gridHasAnObjectAt(collisionGrid, randomX, randomY)) {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);
			}
			GridUtils.set(collisionGrid, randomX, randomY, true);

			EmbodiedAgent embodiedAgent = (EmbodiedAgent) agentFactory.getNewFactoryAgent(species);
			// embodiedAgent.setGeneration(generation + 1);
			embodiedAgent.activeEvolver.generation = generation;

			embodiedAgent.isProgenitor = isProgenitor;
			embodiedAgent.setLocation(randomX, randomY);
			agentGrid.setObjectLocation(embodiedAgent, new Int2D(randomX, randomY));

			embodiedAgent.synchronizeLocationToActiveAgent();

			team.addMember(embodiedAgent);
			embodiedAgent.setTeam(team);

			agents.add(embodiedAgent);

			if (!embodiedAgent.isScheduled()) {
				schedule.scheduleRepeating(embodiedAgent);

				embodiedAgent.setScheduled(true);
			}
		}

	}

	protected void destroyAgent(Agent agent) {
		agentGrid.remove(agent);
		agents.remove(agent);
		// logger.info("<==Removed agent of species: " + agent.getSpecies());

	}

	protected void initNextAgents() {
		// we already have embodied agents, we just get load the agents with the
		// next genes from their respective gene pools

		MersenneTwisterFast randomPrime = this.random;

		for (Agent agent : agents) {

			EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;

			int randomX = randomPrime.nextInt(gridWidth);
			int randomY = randomPrime.nextInt(gridHeight);

			while (GridUtils.gridHasAnObjectAt(collisionGrid, randomX, randomY)) {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);
			}

			GridUtils.set(collisionGrid, randomX, randomY, true);

			embodiedAgent.setNextActiveAgent(randomX, randomY);

			embodiedAgent.setLocation(randomX, randomY);
			agentGrid.setObjectLocation(embodiedAgent, new Int2D(randomX, randomY));

			embodiedAgent.synchronizeLocationToActiveAgent();
			embodiedAgent.setStepCounter(0);

			if (!embodiedAgent.isScheduled()) {
				schedule.scheduleRepeating(embodiedAgent);
				embodiedAgent.setScheduled(true);
			}

		}
	}

	/**
	 * Evolve all the embodied agents and compute/aggregate statistics. Finally
	 * report the statistics
	 */
	protected void evolveEmbodiedAgents() {
		// we might as well free some memory now
		allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
		/*
		 * if (allocatedMemory >
		 * Main.settings.ALLOC_MEMORY_TO_TRIGGER_GC_CLEANUP) { System.gc(); long
		 * allocatedMemoryAfter = (Runtime.getRuntime().totalMemory() -
		 * Runtime.getRuntime().freeMemory()) / (1024 * 1024); long memorySaved
		 * = allocatedMemory - allocatedMemoryAfter; if (memorySaved > 0) {
		 * logger.info("****** FREED :" + memorySaved + " MB "); } }
		 */
		// D.p("=====> starting evolution for generation:" + generation);

		populationFitnessStats.clear();

		// evolve each agent and keep account of their event and fitness
		// statistics...
		generation++;

		if (Main.settings.DYNAMIC_EVENNESS) {
			Main.settings.__numDetectors = 0;
			Main.settings.__numExtractors = 0;
			Main.settings.__numTransporters = 0;
			Main.settings.__numProcessors = 0;
		}

		for (Agent agent : agents) {
			EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;

			if (Main.settings.DYNAMIC_EVENNESS) {
				if (embodiedAgent.activeSpecies == Species.DETECTOR) {
					Main.settings.__numDetectors++;
				} else if (embodiedAgent.activeSpecies == Species.EXTRACTOR) {
					Main.settings.__numExtractors++;
				} else if (embodiedAgent.activeSpecies == Species.TRANSPORTER) {
					Main.settings.__numTransporters++;
				} else if (embodiedAgent.activeSpecies == Species.PROCESSOR) {
					Main.settings.__numProcessors++;
				}
			}

			// accumulate event counts for agents...
			embodiedAgent.poolGenerationEventStats.aggregateStatsTo(embodiedAgent.poolHistoricalEventStats);

			// evolve the agents...
			if (embodiedAgent.evolve() != generation) {
				logger.severe("invalid evolution algorithm implementation");
				System.exit(1);
			}

			for (double fitnessValue : embodiedAgent.fitnessStats.getValues()) {
				populationFitnessStats.addValue(fitnessValue);
			}

			// this part computes the genetic distance between the current and
			// previous alpha

			double alphaGeneticDistance = Program.comparePrograms(
					embodiedAgent.activeEvolver.evolutionEngine.alphaProgram,
					embodiedAgent.activeEvolver.evolutionEngine.previousAlphaProgram);

			if (alphaGeneticDistance != Double.NaN) {
				embodiedAgent.computedAlphaDistance = alphaGeneticDistance;
			}

			experimentReporter.reportAlphaProgram(generation, embodiedAgent.getAgentId(), embodiedAgent.getSpecies(),
					embodiedAgent.activeEvolver.evolutionEngine.alphaProgram);

		}

		// resourceCaptureStats.printStats();

		long currentTime = System.currentTimeMillis();
		deltaTime = currentTime - reportTime;
		allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

		experimentReporter.reportPerformanceEmbodiedModel(generation, intervalStats, generationEventStats,
				speciesEventStatsMap, agents, captureStats, populationFitnessStats, simsRunForThisGeneration,
				resourceCaptureStats, this.simulationCounter, deltaTime, allocatedMemory);

		printGenerationalStats();
		reportTime = currentTime;

		// clear pool generation event stats for next generation...
		for (Agent agent : agents) {
			EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;
			embodiedAgent.poolGenerationEventStats.clear();
		}
		generationEventStats.clear();
		captureStats.clear();
		intervalStats.clear();
		clearSpeciesEventStats();
		resourceCaptureStats.clearAll();

	}

	@Override
	protected void doEndOfSimulationTasks() {

		// each agent now needs to provide local feedback
		// each agent has complete record of everything that happened
		// in poolGenerationEventStats

		for (Agent agent : agents) {
			EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;
			embodiedAgent.activeAgent.eventStats.aggregateStatsTo(embodiedAgent.poolGenerationEventStats);
			embodiedAgent.activeAgent.eventStats.aggregateStatsTo(generationEventStats);

			EventStats speciesEventStats = speciesEventStatsMap.get(embodiedAgent.getSpecies());
			embodiedAgent.activeAgent.eventStats.aggregateStatsTo(speciesEventStats);

			embodiedAgent.evaluateLocalFitness();
			// now reclaim the internal agents...

			embodiedAgent.reclaimActiveAgent();

		}

		collectResouceCaptureStats();

		captureStats.addValue(this.numberOfCollectedResources);

		if (this.numberOfCollectedResources > this.maxResourcesEverCollected) {
			this.maxResourcesEverCollected = this.numberOfCollectedResources;
		}

	}

	@Override
	protected void startNextSimulation() {

		intervalStats.resetLastSteps();
		resetEnvironment();

		if (settings.generationCounter != 0 && (settings.generationCounter % settings.BENCHMARK_GENERATION) == 0) {
			// every BENCHMARK_GENERATION use benchmark environment...
			initEnvironmentWithBenchmark();
		} else {
			initEnvironment();
		}

		initNextAgents();
		unlockGenerationalVisualizer();

	}

	private void createAgentsFromBirthQueue() {
		// birthQueue is a list of all the agent creation requests that were
		// made by all the current agents
		// we will be careful how we add new agents...
		// rule 1:
		// to control population explosion, we only allow one new agent per
		// species -- otherwise a particular species will excessively dominate
		// rule 2: if fewer spots are available than requests made, there will
		// be
		// a toss -- this will give all requesters an equal chance to grow

		// first figure out all possible species to add
		HashSet<Species> requestedSpecies = new HashSet<Species>();
		HashSet<Agent> requesterAgent = new HashSet<Agent>();
		for (Agent agent : birthQueue) {
			if (!requestedSpecies.contains(agent.getSpecies())) {
				requestedSpecies.add(agent.getSpecies());
				requesterAgent.add(agent);
			}
		}
		// requesterAgent now contains one agent from each species
		// that has requested to create new agents

		// figure out spots left

		int openSpots = Main.settings.DE_MAX_POPULATION - agents.size();

		if (requestedSpecies.size() <= openSpots) {
			// enough spots available -- no need to worry about random
			// selection
			for (Agent agent : requesterAgent) {
				addNewAgentByCreatorAgent(agent, false);
			}
		} else {
			// fewer spots are available, we need to be picky
			// shuffle
			Bag speciesBag = new Bag(requesterAgent.toArray());
			speciesBag.shuffle(random);
			for (int i = 0; i < openSpots; i++) {
				addNewAgentByCreatorAgent((Agent) speciesBag.get(i), false);
			}
		}

	}

	private void addNewAgentByCreatorAgent(Agent parentAgent, boolean isProgenitor) {
		if (agents.size() < Main.settings.DE_MAX_POPULATION) {

			Species species = parentAgent.getSpecies();

			MersenneTwisterFast randomPrime = this.random;

			int randomX = randomPrime.nextInt(gridWidth);
			int randomY = randomPrime.nextInt(gridHeight);

			while (GridUtils.gridHasAnObjectAt(collisionGrid, randomX, randomY)) {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);
			}
			GridUtils.set(collisionGrid, randomX, randomY, true);

			EmbodiedAgent childAgent = (EmbodiedAgent) agentFactory.getNewFactoryAgent(species);
			// embodiedAgent.setGeneration(generation + 1);
			childAgent.activeEvolver.generation = generation;

			childAgent.isProgenitor = isProgenitor;
			childAgent.setLocation(randomX, randomY);
			agentGrid.setObjectLocation(childAgent, new Int2D(randomX, randomY));

			childAgent.synchronizeLocationToActiveAgent();

			team.addMember(childAgent);
			childAgent.setTeam(team);

			// now we also transfer the genepool of the parent to the child
			copyGenePool((EmbodiedAgent) parentAgent, childAgent);
			// logger.info("=>Added new agent of species: " + species);

			agents.add(childAgent);

			if (!childAgent.isScheduled()) {
				schedule.scheduleRepeating(childAgent);

				childAgent.setScheduled(true);
			}
		}

	}

	private void copyGenePool(EmbodiedAgent parentAgent, EmbodiedAgent childAgent) {

		int parentPoolSize = parentAgent.activeEvolver.activeBuffer.size();
		int childPoolSize = childAgent.activeEvolver.activeBuffer.size();

		if (parentPoolSize != childPoolSize) {
			logger.info("buffer sizes should never be different -- investigate");
			System.exit(1);
		}

		// make a deep copy of parents's pool into child's
		for (int i = 0; i < parentPoolSize; i++) {
			Program childsOldProgram = childAgent.activeEvolver.activeBuffer.get(i).getProgram();
			// D.p("childs old program was="+childsOldProgram.getFingerPrint());

			childAgent.activeEvolver.activeBuffer.get(i).setProgram(null);
			childsOldProgram = null;

			Program parentGeneProgramCloned = new Program(parentAgent.activeEvolver.activeBuffer.get(i).getProgram());
			// D.p("parents program
			// is="+parentGeneProgramCloned.getFingerPrint());
			childAgent.activeEvolver.activeBuffer.get(i).setProgram(parentGeneProgramCloned);
			childsOldProgram = childAgent.activeEvolver.activeBuffer.get(i).getProgram();
			// D.p("childs new program is
			// now="+childsOldProgram.getFingerPrint());

		}

	}

	private void destroyAgentsFromDeathQueue() {
		// the rules of death are similar to the birth rules...
		// if many agents from many different species wants to die
		// we only pick one agent per species to be killed
		// this way we have a smoother death rate...
		// we also shuffle the death queue
		Bag agentBag = new Bag(deathQueue.toArray());
		agentBag.shuffle(random);

		HashSet<Species> speciesKilled = new HashSet<Species>();

		for (int i = 0; i < agentBag.numObjs; i++) {
			Agent agent = (Agent) agentBag.get(i);
			if (!speciesKilled.contains(agent.getSpecies())) {
				destroyAgent(agent);
				speciesKilled.add(agent.getSpecies());
				// ((EmbodiedAgent)agent).reclaimActiveAgent();
				this.agentFactory.reclaimAgent(agent);
			}
		}
	}

	@Override
	protected boolean evaluateSimulationTerminateCondition() {

		boolean result = (this.numberOfCollectedResources >= this.resourceCaptureGoal)
				|| (this.simStepCounter > stepsPerSimulation);

		return result;
	}

	@Override
	protected void startSimulation() {

		String seedString = Integer.toString(settings.SEED, 16);

		logger.info("EXPERIMENT STARTS ["+seedString+"]: expected maxium simulations =" + simulationsPerExperiment
				+ " stepsPerSimulation=" + stepsPerSimulation);

		initEnvironment();
		saveEnvironmentForBenchmark();

		if (Main.settings.DYNAMIC_EVENNESS) {
			initCachedAgents();
		}
		initAgents();

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

					// logger.info("---- end of simulation: collected=" +
					// numberOfCollectedResources);

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
							evolveEmbodiedAgents();
							destroyAgentsFromDeathQueue();
							createAgentsFromBirthQueue();
							simsRunForThisGeneration = 0;
						}

						startNextSimulation();

					} else {
						// end of experiment...
						if (collectedAllResources()) {
							logger.info("!!!ALL RESOURCES COLLECTED!!!");
						}

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
		return null;
	}

	@Override
	public AgentFactory configAgentFactory() {
		return new EmbodiedAgentFactory(this);
	}

	@Override
	public int configGenePoolSize() {
		return settings.GENE_POOL_SIZE;
	}

	@Override
	public AgentFactory configEmbodiedAgentFactory() {
		return new IslanderAgentFactory(this);
	}

	@Override
	public void printGenerationalStats() {

		if (Main.settings.DYNAMIC_EVENNESS || Main.settings.MANUAL_EVENNESS) {

			logger.info("======> gen: " + generation + "; sims: " + this.simulationCounter + "; fitness: "
					+ populationFitnessStats.getMean() + "; best_capture: " + captureStats.getMax() + "; "
					+ Main.settings.__numDetectors + ":" + Main.settings.__numExtractors + ":"
					+ Main.settings.__numTransporters + ":" + Main.settings.__numProcessors + " ["
					+ settings.statusCache + "] time_delta_ms=" + deltaTime + " allocMB=" + allocatedMemory);

		} else {

			logger.info("gen: " + generation + "; sims: " + this.simulationCounter + "; fitness: "
					+ populationFitnessStats.getMean() + "; best_capture: " + captureStats.getMax() + " ["
					+ settings.statusCache + "] time_delta_ms=" + deltaTime + " allocMB=" + allocatedMemory);
		}
		logger.info("emodiedAgent.count=" + EmbodiedAgent._optimizationEmbodiedAgentCounter + " ; islanderAgent.count="
				+ IslanderAgent._optimizationIslanderAgentCounter);

	}

}
