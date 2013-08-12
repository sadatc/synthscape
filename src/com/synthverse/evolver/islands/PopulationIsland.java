package com.synthverse.evolver.islands;

import com.synthverse.evolver.core.CentralizedEvolutionEngine;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Species;

/**
 * A population island maintains a gene pool that produces agents of a specific
 * species
 * 
 * @author sadat
 * 
 */
public class PopulationIsland {

    private int populationSize;
    private Species species;
    private CentralizedEvolutionEngine engine;
    private AgentFactory agentFactory;

    public PopulationIsland(AgentFactory agentFactory, Species species,
	    int populationSize) {
	this.species = species;
	this.populationSize = populationSize;
	this.agentFactory = agentFactory;
	this.engine = new CentralizedEvolutionEngine(agentFactory, species,
		populationSize);
    }

}
