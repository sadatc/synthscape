package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Event;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;
import com.synthverse.util.LogUtils;

/**
 * Here is how it works: 1. It creates an evolver for each species 2. when a
 * request comes in, it keeps track of the generation
 * 
 * 
 * @author sadat
 * 
 */
public class ArchipelagoEvolver extends Evolver implements Constants {
    private static Logger logger = Logger.getLogger(ArchipelagoEvolver.class.getName());
    private static double maxFitness = Double.MIN_VALUE;

    private int generation;

    HashMap<Species, PopulationIslandEvolver> speciesIslandMap = new HashMap<Species, PopulationIslandEvolver>();

    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    public ArchipelagoEvolver(Simulation simulation, AgentFactory agentFactory) {
	super(simulation, agentFactory);
	this.generation = 0;
    }

    public void initPopulationIslands() throws Exception {
	for (Species species : simulation.getSpeciesComposition()) {
	    PopulationIslandEvolver island = new PopulationIslandEvolver(simulation, agentFactory, species,
		    simulation.getClonesPerSpecies());
	    speciesIslandMap.put(species, island);
	}
    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	PopulationIslandEvolver islandEvolver = speciesIslandMap.get(species);

	Agent result = islandEvolver.getAgent(species, x, y);
	return result;
    }

    @Override
    public void init() {
	try {
	    initPopulationIslands();
	} catch (Exception e) {
	    System.err.println("Exception initializing islands:" + e.getMessage());
	    e.printStackTrace();
	}

    }

    @Override
    public void provideFeedback(List<Agent> agents, Stats simStats) {
	// FITNESS EVALUATION:
	// In this model, for each generation, the number of simulations run
	// corresponds
	// to the size of the gene pool. During each simulation, an individual
	// gene
	// from the pool is taken, cloned (into a team), and fitness evaluated
	// collectively

	// logger.info("evaluating generation:"+this.generation+"-"+
	// simStats.toString());
	double collectiveFitness = computeFitness(simStats, agents);

	// here all agents are forced to have the same fitness
	// all agents here belong to a single genotypical parent
	for (Agent agent : agents) {
	    agent.setFitness(collectiveFitness);
	    agent.setProvidedFeedback(true);
	    Agent cloneParentAgent = agent.getGenotypicalParent();
	    // logger.info("agent parent="+cloneParentAgent.getAgentId());
	    if (cloneParentAgent != null) {
		cloneParentAgent.setFitness(collectiveFitness);
	    }
	}

    }

    private double computeFitness(Stats simStats, List<Agent> agents) {
	double result = 0.0;

	// collecting a resource gets the highest point
	// simStats.printValues();

	for (Event event : simStats.getEvents()) {

	    result += getEventWeight(event) * simStats.getValue(event);

	}
	if (result > maxFitness) {
	    logger.info("*** Record Fitness=" + result + " at Generation: " + generation + " ***");

	    maxFitness = result;
	}

	/*
	 * if(result>0.0) { logger.info("trap"); }
	 */

	return result;
    }

    private double getEventWeight(Event event) {

	switch (event) {
	/*
	 * case DETECTED_RAW_RESOURCE: return 0.10; case EXTRACTED_RESOURCE:
	 * return 0.25;
	 */
	/*
	 * case DETECTED_EXTRACTED_RESOURCE: return 8.0; case
	 * DETECTED_PROCESSED_RESOURCE: return 6.0; case PROCESSED_RESOURCE:
	 * return 12.0; case LOADED_RESOURCE: return 6.0; case
	 * UNLOADED_RESOURCE: return 5.0; case MOVE_TO_CLOSEST_COLLECTION_SITE:
	 * return 1.0; case MOVE_TO_PRIMARY_COLLECTION_SITE: return 1.0;
	 */
	case COLLECTED_RESOURCE:
	    // logger.info("encountered collection!!!!!");
	    return 1.0;

	}
	return 0;

    }

    @Override
    public void evolve() {
	// logger.info("ArchipelagoEvolver: evolve()");
	for (Evolver evolver : speciesIslandMap.values()) {
	    evolver.evolve();
	}
	generation++;

	// D.pause();
	// System.exit(1);

    }

    @Override
    public int getGeneration() {
	return generation;
    }
}
