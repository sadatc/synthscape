package com.synthverse.synthscape.experiment.test.manuallycoded;

import java.util.EnumMap;
import java.util.List;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;

public class ManuallyCodedEvolver extends Evolver implements Constants {
    
    

    EnumMap<Species, Integer> speciesAgentRequestCounter = new EnumMap<Species, Integer>(Species.class);
    EnumMap<Species, Integer> speciesAgentGeneration = new EnumMap<Species, Integer>(Species.class);

    protected ManuallyCodedEvolver(Simulation simulation) {
	super(simulation);
    }

    @Override
    public void init() {
	for(Species species: simulation.getSpeciesComposition()) {
	    speciesAgentRequestCounter.put(species, 0);
	    speciesAgentGeneration.put(species, SEED_GENERATION_NUMBER);
	}

    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	
	int generationCounter = speciesAgentGeneration.get(species);
	int agentsRequested = speciesAgentRequestCounter.get(species);
	
	agentsRequested++;
	// check if this is exceeding the limit
	if(agentsRequested <= simulation.getClonesPerSpecies()) {
	    speciesAgentRequestCounter.put(species, agentsRequested);
	} else {
	    // if it did, we are on a new generation...
	    generationCounter++;
	    speciesAgentGeneration.put(species, generationCounter);
	    speciesAgentRequestCounter.put(species, 1);
	    D.p("starting new generation: "+generationCounter);
	}
	
	Agent agent = simulation.getAgentFactory().getNewFactoryAgent(species);
	agent.reset();
	agent.setGeneration(generationCounter);

	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	agent.setX(x);
	agent.setY(y);

	return agent;

    }

    @Override
    public void provideFeedback(List<Agent> agents, Stats simStats) {
	// TODO Auto-generated method stub
	
    }

}
