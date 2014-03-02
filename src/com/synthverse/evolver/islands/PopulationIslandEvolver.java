package com.synthverse.evolver.islands;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.synthverse.evolver.core.CentralizedEvolutionEngine;
import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;
import com.synthverse.util.LogUtils;

/**
 * A population island maintains agentStats gene pool that produces agents of
 * agentStats specific species
 * 
 * @author sadat
 * 
 */
public class PopulationIslandEvolver extends Evolver implements Constants {

    private int generation;

    protected static Logger logger = Logger.getLogger(PopulationIslandEvolver.class.getName());

    static {
	LogUtils.applyDefaultSettings(logger, Level.ALL);
    }

    private Species species;
    private int clonesPerSpecies;
    private int totalPopulation;
    private int genePoolSize;
    private int genePoolIndex;

    private CentralizedEvolutionEngine evolutionEngine;
    private int requestCounter = 1;
    private int cloneCounter = 1;
    List<Agent> activeBuffer;

    public PopulationIslandEvolver(Simulation simulation, Species species, int clonesPerSpecies) throws Exception {
	super(simulation);
	this.species = species;

	this.generation = 0;

	evolutionEngine = new CentralizedEvolutionEngine(simulation.getAgentFactory(), species);
	this.clonesPerSpecies = clonesPerSpecies;
	genePoolSize = evolutionEngine.getGenePoolSize();
	totalPopulation = clonesPerSpecies * genePoolSize;
	activeBuffer = evolutionEngine.getActiveBuffer();
	this.requestCounter = 1;
	this.genePoolIndex = 0;
    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	Agent returnAgent = null;

	if (requestCounter > totalPopulation) {
	    requestCounter = 1;
	    genePoolIndex = 0;
	    cloneCounter = 1;

	}

	if (cloneCounter > clonesPerSpecies) {
	    cloneCounter = 1;
	    genePoolIndex++;
	}

	// get next agent from the gene-pool and clone it
	activeBuffer = evolutionEngine.getActiveBuffer();
	Agent archetype = activeBuffer.get(genePoolIndex);
	returnAgent = simulation.getAgentFactory().getNewFactoryAgent(species);
	returnAgent.cloneGenotypeFrom(archetype);
	returnAgent.getVirtualMachine().resetAll();
	returnAgent.reset();
	returnAgent.setGeneration(generation);
	returnAgent.setMaxSteps(simulation.getMaxStepsPerAgent());
	returnAgent.setX(x);
	returnAgent.setY(y);
	requestCounter++;
	cloneCounter++;

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

    @Override
    public void evolve() {
	//logger.info("PopulationIslandEvolver(" + species + "): evolve()");
	// time to generate next generation

	//logger.info("***************************************");
	//logger.info("starting new generation: " + (generation + 1));
	//logger.info("***************************************");
	
	
	for(Agent agent: activeBuffer) {
	    logger.info("inspecting "+agent.getAgentId()+" fitness="+agent.getFitness());
	}
	
	
	
	evolutionEngine.generateNextGeneration(simulation.random);
	activeBuffer = evolutionEngine.getActiveBuffer();
	generation++;
    }

    @Override
    public int getGeneration() {

	return generation;
    }

}
