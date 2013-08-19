package com.synthverse.evolver.islands;

import java.util.List;

import com.synthverse.evolver.core.CentralizedEvolutionEngine;
import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

/**
 * A population island maintains agentStats gene pool that produces agents of agentStats specific
 * species
 * 
 * @author sadat
 * 
 */
public class PopulationIslandEvolver extends Evolver implements Constants {

    protected PopulationIslandEvolver(Simulation simulation) {
	super(simulation);
	// TODO Auto-generated constructor stub
    }
    /*

    private int populationSize;
    private Species species;
    private CentralizedEvolutionEngine engine;
   
    private static int seedAgentRequestCounter = 0;
    private static int evolvedAgentRequestCounter = 0;
    
    private List<Agent> activeBuffer = null;

    public PopulationIslandEvolver(Simulation simulation, Species species,
	    int populationSize) {
	super(simulation);
	this.species = species;
	this.populationSize = populationSize;

	this.engine = new CentralizedEvolutionEngine(
		simulation.getAgentFactory(), species, populationSize);
	 activeBuffer = engine.getActiveBuffer();
    }

    @Override
    public Agent getSeedAgent(Species species, int x, int y) {
	Agent result = null;
	if(seedAgentRequestCounter<activeBuffer.size()) {
	    
	    result = activeBuffer.get(seedAgentRequestCounter);
	    
	    result.setX(x);
	    result.setY(y);
	    result.setMaxSteps(simulation.getMaxStepsPerAgent());
	    
	    seedAgentRequestCounter++;
	} else {
	    D.p("WARNING! requested too many seed agents...");
	    System.exit(-1);
	}
	
	return result;
	
    }

    @Override
    public Agent getEvolvedAgent(Agent ancestorAgent, int x, int y) {
	Agent result = ancestorAgent;
	if(evolvedAgentRequestCounter<activeBuffer.size()) {
	    
	    if(activeBuffer.contains(result)) {
	    
        	    result.setX(x);
        	    result.setY(y);
        	    result.setMaxSteps(simulation.getMaxStepsPerAgent());
        	    
        	    evolvedAgentRequestCounter++;
	    } else {
		D.p("Warning! requested agent doesn't exist");
		System.exit(-1);
	    }
	} else {
	    D.p("WARNING! requested too many evolved agents...");
	    System.exit(-1);
	}
	
	return result;
    }

    @Override
    public void init() {

    }

    @Override
    public void generateNextGeneration() {
	engine.generateNextGeneration(simulation.random);
	
    }
    */

    @Override
    public Agent getAgent(Species species, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub
	
    }

}
