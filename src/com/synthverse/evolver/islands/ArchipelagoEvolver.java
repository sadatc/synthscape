package com.synthverse.evolver.islands;

import java.util.HashMap;
import java.util.List;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;

/**
 * Here is how it works: 1. It creates an evolver for each species 2. when a
 * request comes in, it keeps track of the generation
 * 
 * 
 * @author sadat
 * 
 */
public class ArchipelagoEvolver extends Evolver implements Constants {

    HashMap<Species, PopulationIslandEvolver> speciesIslandMap = new HashMap<Species, PopulationIslandEvolver>();

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
	for(Agent agent: agents) {
	    PopulationIslandEvolver islandEvolver = speciesIslandMap.get(agent.getSpecies());
	    islandEvolver.provideFeedback(agent, simStats);
	}
	
    }

}
