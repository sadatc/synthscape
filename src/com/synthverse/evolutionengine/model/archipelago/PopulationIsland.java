package com.synthverse.evolutionengine.model.archipelago;

import com.synthverse.evolutionengine.model.core.EvolverCore;
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
    private EvolverCore population;
    private AgentFactory agentFactory;

    public PopulationIsland(AgentFactory agentFactory, Species species,
	    int populationSize) {
	this.species = species;
	this.populationSize = populationSize;
	this.agentFactory = agentFactory;
	this.population = new EvolverCore(agentFactory, species,
		populationSize);
    }

}
