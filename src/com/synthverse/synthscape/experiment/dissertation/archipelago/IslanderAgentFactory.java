package com.synthverse.synthscape.experiment.dissertation.archipelago;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;

public class IslanderAgentFactory implements AgentFactory, Constants {

	private static IslanderAgentFactory instance = new IslanderAgentFactory();

	private IslanderAgentFactory() {
	}

	public static IslanderAgentFactory getInstance() {
		return instance;
	}

	public Agent create() {
		Agent agent = new IslanderAgent();
		return agent;
	}

	public Agent create(Simulation simulation, long generation, long agentId,
			int x, int y) {
		IslanderAgent agent = new IslanderAgent(simulation, SEED_GENERATION_NUMBER,
				agentId, 20000, x, y);
		return agent;
	}

}
