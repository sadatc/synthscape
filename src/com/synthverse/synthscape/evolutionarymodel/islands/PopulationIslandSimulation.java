package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import sim.util.Int2D;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Team;
import com.synthverse.util.LogUtils;
import com.synthverse.util.StringUtils;

import ec.util.MersenneTwisterFast;

@SuppressWarnings("serial")
public class PopulationIslandSimulation extends Simulation {

    private Team team = new Team();

    public static Settings settings = Settings.getInstance();

    private static Logger logger = Logger.getLogger(PopulationIslandSimulation.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    public PopulationIslandSimulation(long seed) throws Exception {
	super(seed);

    }

    public static void main(String[] arg) {
	String[] manualArgs = StringUtils.parseArguments("-repeat " + settings.REPEAT + " -seed 2");
	doLoop(PopulationIslandSimulation.class, manualArgs);
	logger.info("Diagnosis: total # of agents created: " + Agent.get_optimizationAgentCounter());
	System.exit(0);
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

}
