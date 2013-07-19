package com.synthverse.synthscape.experiment.test.manuallycoded;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;

public class ManuallyCodedAgentFactory implements AgentFactory, Constants {

	private static ManuallyCodedAgentFactory instance = new ManuallyCodedAgentFactory();

	private ManuallyCodedAgentFactory() {
	}

	public static ManuallyCodedAgentFactory getInstance() {
		return instance;
	}

	public Agent create() {
		Agent agent = new ManuallyCodedAgent();
		return agent;
	}

	public Agent create(Simulation simulation, long generation, long agentId,
			int x, int y) {
		ManuallyCodedAgent agent = new ManuallyCodedAgent(simulation, SEED_GENERATION_NUMBER,
				agentId, 200, x, y);
		return agent;
	}

}
