package com.synthverse.synthscape.experiment.archipleago;

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
	return new ManuallyCodedAgent();

    }

    @Override
    public Agent create(Simulation simulation, int generationNumber, int agentId, int maxSteps,
	    int startX, int startY) {
	return new ManuallyCodedAgent(simulation, generationNumber, agentId, maxSteps, startX,
		startY);
    }

}
