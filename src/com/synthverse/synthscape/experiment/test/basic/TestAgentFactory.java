package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;

public class TestAgentFactory implements AgentFactory, Constants {

    private static TestAgentFactory instance = new TestAgentFactory();

    private TestAgentFactory() {
    }

    public static TestAgentFactory getInstance() {
	return instance;
    }

    @Override
    public Agent create() {
	return new TestAgent();
    }

    @Override
    public Agent create(Simulation simulation, int generation, int agentId, int maxSteps, int x,
	    int y) {
	return new TestAgent(simulation, generation, agentId, maxSteps, x, y);

    }

}
