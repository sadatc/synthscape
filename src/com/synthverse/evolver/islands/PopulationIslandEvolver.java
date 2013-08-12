package com.synthverse.evolver.islands;

import com.synthverse.evolver.core.CentralizedEvolutionEngine;
import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

/**
 * A population island maintains a gene pool that produces agents of a specific
 * species
 * 
 * @author sadat
 * 
 */
public class PopulationIslandEvolver extends Evolver implements Constants {

    private int populationSize;
    private Species species;
    private CentralizedEvolutionEngine engine;

    public PopulationIslandEvolver(Simulation simulation, Species species,
	    int populationSize) {
	super(simulation);
	this.species = species;
	this.populationSize = populationSize;

	this.engine = new CentralizedEvolutionEngine(
		simulation.getAgentFactory(), species, populationSize);
    }

    @Override
    public Agent getSeedAgent(Species species, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Agent getEvolvedAgent(Agent ancestorAgent, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void init() {

    }

}
