package com.synthverse.evolver.islands;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Event;
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

    HashMap<Species, PopulationIslandEvolver> speciesIslandMap = new HashMap<Species, PopulationIslandEvolver>();

    static {
	LogUtils.applyDefaultSettings(logger, Level.ALL);
    }

    public ArchipelagoEvolver(Simulation simulation) {
	super(simulation);
    }

    public void initPopulationIslands() {
	for (Species species : simulation.getSpeciesComposition()) {
	    PopulationIslandEvolver island = new PopulationIslandEvolver(simulation, species,
		    simulation.getClonesPerSpecies());
	    speciesIslandMap.put(species, island);
	}
    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	PopulationIslandEvolver islandEvolver = speciesIslandMap.get(species);
	return islandEvolver.getAgent(species, x, y);
    }

    @Override
    public void init() {
	initPopulationIslands();

    }

    @Override
    public void provideFeedback(List<Agent> agents, Stats simStats) {

	double fitness = computeFitness(simStats);

	for (Agent agent : agents) {
	    agent.setFitness(fitness);
	    if (agent.getGenotypicalParent() != null) {
		agent.getGenotypicalParent().setFitness(fitness);
	    }
	}

    }

    private double computeFitness(Stats simStats) {
	double result = 0.0;

	// collecting a resource gets the highest point
	//simStats.printValues();

	for (Event event : simStats.getEvents()) {

	    result += getEventWeight(event) * simStats.getValue(event);

	}
	if (result > maxFitness) {
	    logger.info("Fitness=" + result);
	    maxFitness = result;
	}

	return result;
    }

    private double getEventWeight(Event event) {

	switch (event) {
	case DETECTED_RAW_RESOURCE:
	    return 10.0;
	case DETECTED_EXTRACTED_RESOURCE:
	    return 8.0;
	case DETECTED_PROCESSED_RESOURCE:
	    return 6.0;
	case EXTRACTED_RESOURCE:
	    return 11.0;
	case PROCESSED_RESOURCE:
	    return 12.0;
	case LOADED_RESOURCE: 
	    return 6.0;
	case UNLOADED_RESOURCE: 
	    return 5.0;
	case MOVE_TO_CLOSEST_COLLECTION_SITE:
	    return 1.0;
	case MOVE_TO_PRIMARY_COLLECTION_SITE:
	    return 1.0;
	case COLLECTED_RESOURCE:
	    return 20.0;

	}
	return 0;

    }

}
