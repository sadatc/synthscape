package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class TestAgentFactory implements AgentFactory, Constants {

    private static TestAgentFactory instance = new TestAgentFactory();

    private TestAgentFactory() {
    }

    public static TestAgentFactory getInstance() {
	return instance;
    }

    @Override
    public Agent createFactoryAgent(Species species) {
	return new TestAgent(species);
    }

    @Override
    public Agent createFactoryAgent(Simulation simulation, Species species,
	    int generation, int agentId, int maxSteps, int x, int y) {
	return new TestAgent(simulation, species, generation, agentId,
		maxSteps, x, y);

    }

}
