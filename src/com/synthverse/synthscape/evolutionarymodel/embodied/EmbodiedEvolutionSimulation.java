package com.synthverse.synthscape.evolutionarymodel.embodied;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

import com.synthverse.Main;
import com.synthverse.stacks.InstructionTranslator;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.ExperimentReporter;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Team;
import com.synthverse.synthscape.evolutionarymodel.islands.IslanderAgent;
import com.synthverse.synthscape.evolutionarymodel.islands.IslanderAgentFactory;
import com.synthverse.util.LogUtils;
import com.synthverse.util.StringUtils;

import ec.util.MersenneTwisterFast;

@SuppressWarnings("serial")
public class EmbodiedEvolutionSimulation extends Simulation {

    private Team team = new Team();

    public static Settings settings = Settings.getInstance();

    private static Logger logger = Logger.getLogger(EmbodiedEvolutionSimulation.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    public EmbodiedEvolutionSimulation(long seed) throws Exception {
	super(seed);
    }

    public static void main(String[] arg) {
	String[] manualArgs = StringUtils.parseArguments("-repeat " + settings.REPEAT + " -seed 2");
	doLoop(EmbodiedEvolutionSimulation.class, manualArgs);
	logger.info("Diagnosis: total # of agents created: " + Agent.get_optimazationTotalAgentsCounters());
	logger.info("Diagnosis: total # of islander agents created: "
		+ IslanderAgent.get_optimizationIslanderAgentCounter());
	logger.info("Diagnosis: total # of embodied agents created: "
		+ EmbodiedAgent.get_optimizationEmbodiedAgentCounter());
	System.exit(0);
    }

    private void initTeam() {
	int teamId = team.getTeamId();

	teamId++;
	team.init();
	team.setTeamId(teamId);
	team.setExpectedSize(speciesComposition.size() * clonesPerSpecies);

    }

    protected void init() throws Exception {
	// we can compute the server name and batch ID right away
	try {
	    serverName = java.net.InetAddress.getLocalHost().getHostName();
	} catch (Exception e) {
	    serverName = "LOCAL";
	}
	batchId = Long.toHexString(System.currentTimeMillis());

	InstructionTranslator.logStatus();

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

	// agent factory and evolver...
	setAgentFactory(configAgentFactory());
	setEmbodiedAgentFactory(configEmbodiedAgentFactory());

	// set these variables
	double gridArea = gridWidth * gridHeight;
	numberOfObstacles = (int) (gridArea * obstacleDensity);
	numberOfResources = (int) (gridArea * resourceDensity);
	resourceCaptureGoal = (int) ((double) numberOfResources * settings.RESOURCE_CAPTURE_GOAL);

	createDataStructures();

	simulationCounter = 0;

	numberOfCollectedResources = 0;

	experimentReporter = new ExperimentReporter(this, DEFAULT_FLUSH_ALWAYS_FLAG);

	isToroidalWorld = TOROIDAL_FLAG;
	trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;
    }

    @Override
    protected void initAgents() {
	// populate with agents
	initTeam();

	MersenneTwisterFast randomPrime = this.random;
	if (!settings.RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM) {
	    randomPrime = controlledRandom;
	    controlledRandom.setSeed(1);
	}
	agents.clear();

	for (Species species : speciesComposition) {

	    for (int i = 0; i < clonesPerSpecies; i++) {

		int randomX = randomPrime.nextInt(gridWidth);
		int randomY = randomPrime.nextInt(gridHeight);

		while (initCollisionGrid.field[randomX][randomY] == PRESENT) {
		    randomX = randomPrime.nextInt(gridWidth);
		    randomY = randomPrime.nextInt(gridHeight);
		}
		initCollisionGrid.field[randomX][randomY] = PRESENT;

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

    protected void initNextAgents() {
	// we already have embodied agents, we just get load the agents with the
	// next genes from their respective gene pools

	MersenneTwisterFast randomPrime = this.random;
	if (!settings.RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM) {
	    randomPrime = controlledRandom;
	    controlledRandom.setSeed(1);
	}

	for (Agent agent : agents) {

	    EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;

	    int randomX = randomPrime.nextInt(gridWidth);
	    int randomY = randomPrime.nextInt(gridHeight);

	    while (initCollisionGrid.field[randomX][randomY] == PRESENT) {
		randomX = randomPrime.nextInt(gridWidth);
		randomY = randomPrime.nextInt(gridHeight);
	    }
	    initCollisionGrid.field[randomX][randomY] = PRESENT;

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

    protected void evolveEmbodiedAgents() {
	logger.info("evolving embodied agents... number of simulations run:" + this.simulationCounter);

	for (Agent agent : agents) {
	    EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;
	    embodiedAgent.evolve();
	}

    }

    @Override
    public void reportPerformance(int generationCounter, DescriptiveStatistics fitnessStats) {
	experimentReporter.reportPerformanceEmbodiedModel(generationCounter, simStats, poolStats, fitnessStats);
    }

    @Override
    protected void doEndOfStepTasks() {
	// accumulate all agent counts to a step count
	for (Agent agent : agents) {
	    EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;
	    embodiedAgent.aggregateStepStats();
	    embodiedAgent.activeAgent.agentStats.clear();

	}
    }

    @Override
    protected void doEndOfSimulationTasks() {
	// each agent now needs to provide local feedback
	// each agent has complete record of everything that happened 
	// in embodiedAgentSimStats
	// TODO: call embodiedAgent.providefeedback -- this will do a local
	// evaluation based on agent's capabilities

	
	for (Agent agent : agents) {
	    EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;
	    embodiedAgent.evaluateLocalFitness();
	    // now reclaim the internal agents...

	    embodiedAgent.reclaimActiveAgent();

	}
	// System.exit(1);

	if (this.numberOfCollectedResources > this.maxResourcesEverCollected) {
	    this.maxResourcesEverCollected = this.numberOfCollectedResources;
	}

    }

    @Override
    protected void startNextSimulation() {

	// logger.info("startNextSimulation()");
	simStats.aggregateStatsTo(poolStats);
	simStats.clear();
	resetEnvironment();
	initEnvironment();
	initNextAgents();

    }

    @Override
    protected boolean evaluateSimulationTerminateCondition() {

	boolean result = (this.numberOfCollectedResources >= this.resourceCaptureGoal)
		|| (this.simStepCounter > stepsPerSimulation);

	return result;
    }

    @Override
    protected void fadeTrails() {
	trailGrid.lowerBound(0.0);
	trailGrid.multiply(trailEvaporationConstant);

    }

    @Override
    protected void startSimulation() {

	logger.info("EXPERIMENT STARTS: expected maxium simulations =" + simulationsPerExperiment
		+ " stepsPerSimulation=" + stepsPerSimulation);

	initEnvironment();
	initAgents();

	logger.info("---- starting simulation (" + simulationCounter + ") with: world=" + (gridHeight * gridWidth)
		+ " obstacles=" + numberOfObstacles + " sites=" + numberOfCollectionSites + " resources="
		+ numberOfResources + " agents=" + agents.size());

	setStartDate();
	experimentReporter.initReporter();

	// this is run at the end of each step
	schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
	    public void step(SimState state) {

		simStepCounter++;

		fadeTrails();
		ageBroadcasts();
		doEndOfStepTasks();

		// check if simulation should continue...
		if (evaluateSimulationTerminateCondition()) {

		    doEndOfSimulationTasks();

		    // logger.info("---- end of simulation: collected=" +
		    // numberOfCollectedResources);

		    simStepCounter = 0;
		    simulationCounter++;

		    if (!collectedAllResources() && simulationCounter < simulationsPerExperiment) {

			if (simulationCounter % settings.GENE_POOL_SIZE == 0) {
			    evolveEmbodiedAgents();
			}

			/*
			 * logger.info("---- starting simulation (" +
			 * simulationCounter + ") with: world=" + (gridHeight *
			 * gridWidth) + " obstacles=" + numberOfObstacles +
			 * " sites=" + numberOfCollectionSites + " resources=" +
			 * numberOfResources + " agents=" + agents.size());
			 */

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
    public Set<Species> configSpeciesComposition() {
	Set<Species> speciesSet = new HashSet<Species>();

	if (settings.MODEL_SPECIES.contains("detector")) {
	    speciesSet.add(Species.DETECTOR);
	    logger.info("adding DETECTORs...");
	}

	if (settings.MODEL_SPECIES.contains("extractor")) {
	    speciesSet.add(Species.EXTRACTOR);
	    logger.info("adding EXTRACTORs...");
	}

	if (settings.MODEL_SPECIES.contains("transporter")) {
	    speciesSet.add(Species.TRANSPORTER);
	    logger.info("adding TRANSPORTERs...");
	}

	if (settings.MODEL_SPECIES.contains("hetero")) {
	    speciesSet.add(Species.HETERO);
	    logger.info("adding HETEROs...");
	}

	return speciesSet;
    }

    @Override
    public Set<InteractionMechanism> configInteractionMechanisms() {
	Set<InteractionMechanism> mechanisms = new HashSet<InteractionMechanism>();

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

}
