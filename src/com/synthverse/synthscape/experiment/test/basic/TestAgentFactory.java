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

    public Agent create() {
	Agent agent = new TestAgent();
	return agent;
    }

    public Agent create(Simulation simulation, int generation, int agentId,
	    int x, int y) {
	TestAgent agent = new TestAgent(simulation, SEED_GENERATION_NUMBER,
		agentId, 200, x, y);
	return agent;
    }

}
