package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;

public class TestAgentFactory implements AgentFactory, Constants {

	private static TestAgentFactory factory = new TestAgentFactory();

	public static TestAgentFactory getInstance() {
		return factory;
	}

	@Override
	public Agent create() {
		Agent agent = new TestAgent();
		return agent;
	}

	@Override
	public Agent create(Simulation simulation, long generation, long agentId,
			int x, int y) {
		TestAgent agent = new TestAgent(simulation, SEED_GENERATION_NUMBER,
				agentId, 200, x, y);
		return agent;
	}

}
