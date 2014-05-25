package com.synthverse.synthscape.test.manuallycoded;

import java.util.EnumMap;
import java.util.List;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.EventStats;
import com.synthverse.util.LogUtils;

public class ManuallyCodedEvolver extends Evolver implements Constants {
    
    private int generation;

    private static Logger logger = Logger.getLogger(ManuallyCodedEvolver.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    EnumMap<Species, Integer> speciesAgentRequestCounter = new EnumMap<Species, Integer>(Species.class);
    EnumMap<Species, Integer> speciesAgentGeneration = new EnumMap<Species, Integer>(Species.class);

    protected ManuallyCodedEvolver(Simulation simulation, AgentFactory agentFactory) {
	super(simulation, agentFactory);
	generation = 0;
    }

    @Override
    public void init() {
	for (Species species : simulation.getSpeciesComposition()) {
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
	if (agentsRequested <= simulation.getClonesPerSpecies()) {
	    speciesAgentRequestCounter.put(species, agentsRequested);
	} else {
	    // if it did, we are on a new generation...
	    generationCounter++;
	    speciesAgentGeneration.put(species, generationCounter);
	    speciesAgentRequestCounter.put(species, 1);
	    logger.info("starting new generation: " + generationCounter);
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
    public void provideFeedback(List<Agent> agents, EventStats simStats) {
	

    }

    @Override
    public int evolve() {
	
	generation++;
	return generation;
    }

    @Override
    public int getGeneration() {
	
	return generation;
    }

}
