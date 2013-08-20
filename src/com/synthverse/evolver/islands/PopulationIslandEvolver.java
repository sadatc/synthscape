package com.synthverse.evolver.islands;

import java.util.List;

import com.synthverse.evolver.core.CentralizedEvolutionEngine;
import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;

/**
 * A population island maintains agentStats gene pool that produces agents of
 * agentStats specific species
 * 
 * @author sadat
 * 
 */
public class PopulationIslandEvolver extends Evolver implements Constants {

    private Species species;
    private int clonesPerSpecies;
    private int totalPopulation;
    private int genePoolSize;
    private int genePoolIndex;
    private int generation;
    private CentralizedEvolutionEngine evolutionEngine;
    private int requestCounter = 0;
    private int cloneCounter = 0;
    List<Agent> activeBuffer;

    public PopulationIslandEvolver(Simulation simulation, Species species, int clonesPerSpecies) {
	super(simulation);
	this.species = species;

	this.generation = 0;

	evolutionEngine = new CentralizedEvolutionEngine(simulation.getAgentFactory(), species);
	this.clonesPerSpecies = clonesPerSpecies;
	genePoolSize = evolutionEngine.getGenePoolSize();
	totalPopulation = clonesPerSpecies * genePoolSize;
	activeBuffer = evolutionEngine.getActiveBuffer();
	this.requestCounter = 0;
	this.genePoolIndex = 0;
    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	Agent returnAgent = null;

	requestCounter++;

	if (requestCounter < totalPopulation) {
	    cloneCounter++;

	    Agent archetype = activeBuffer.get(genePoolIndex);
	    // now clone this and give it.

	    returnAgent = simulation.getAgentFactory().getNewFactoryAgent(species);
	    returnAgent.cloneGenotypeFrom(archetype);

	    returnAgent.reset();
	    returnAgent.setGeneration(generation);
	    returnAgent.setMaxSteps(simulation.getMaxStepsPerAgent());
	    returnAgent.setX(x);
	    returnAgent.setY(y);

	    if (cloneCounter >= clonesPerSpecies) {
		genePoolIndex++;
		cloneCounter = 0;
	    }

	} else {
	    // we have reached next generation
	    // reset all counters and re-run...
	    generation++;
	    requestCounter = 0;
	    genePoolIndex = 0;
	    cloneCounter = 0;
	    evolutionEngine.generateNextGeneration(simulation.random);
	    activeBuffer = evolutionEngine.getActiveBuffer();
	    // recursive call!
	    returnAgent = getAgent(species, x, y);
	}

	return returnAgent;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub

    }
    
    @Override
    public void provideFeedback(List<Agent> agents, Stats simStats) {
	// do nothing, we don't need it.
	
    }

    public void provideFeedback(Agent agent, Stats simStats) {
	
	
    }

}
