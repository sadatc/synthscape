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
    
    protected static Logger logger = Logger.getLogger(PopulationIslandEvolver.class.getName());

    static {
	LogUtils.applyDefaultSettings(logger, Level.ALL);
    }
    
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

    public PopulationIslandEvolver(Simulation simulation, Species species, int clonesPerSpecies) throws Exception {
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

	if (requestCounter < totalPopulation) {
	    Agent archetype = activeBuffer.get(genePoolIndex);
	    // now clone this and give it.

	    D.p("archeType="+archetype.getProgram().getSignature());
	    
	    returnAgent = simulation.getAgentFactory().getNewFactoryAgent(species);
	    D.p("returnAgent1="+returnAgent.getProgram().getSignature());
	    returnAgent.cloneGenotypeFrom(archetype);
	    D.p("returnAgent2="+returnAgent.getProgram().getSignature());
	    returnAgent.getVirtualMachine().resetAll();
	    D.p("returnAgent3="+returnAgent.getProgram().getSignature());

	    returnAgent.reset();
	    returnAgent.setGeneration(generation);
	    returnAgent.setMaxSteps(simulation.getMaxStepsPerAgent());
	    returnAgent.setX(x);
	    returnAgent.setY(y);
	    D.p("returning agent: requestCounter:"+requestCounter+" cloneCounter:"+cloneCounter+" genePoolIndex:"+genePoolIndex+" generation:"+generation+" Agent Signature:"+returnAgent.getProgram().getSignature());

	    if (cloneCounter > clonesPerSpecies) {
		genePoolIndex++;
		cloneCounter = 0;
	    } else {
		cloneCounter++;
	    }

	} else {
	    // we have reached next generation
	    // reset all counters and re-run...	    
	    generation++;
	    D.p("$$$ starting new generation: "+generation);
	    requestCounter = 0;
	    genePoolIndex = 0;
	    cloneCounter = 0;
	    evolutionEngine.generateNextGeneration(simulation.random);
	    activeBuffer = evolutionEngine.getActiveBuffer();
	    // recursive call!
	    returnAgent = getAgent(species, x, y);
	    D.p("returning agent: requestCounter:"+requestCounter+" cloneCounter:"+cloneCounter+" genePoolIndex:"+genePoolIndex+" generation:"+generation+" Agent Signature:"+returnAgent.getProgram().getSignature());
	    
	}
	

	
	requestCounter++;
	
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

   

}
