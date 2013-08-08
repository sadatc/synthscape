package com.synthverse.synthscape.experiment.dissertation.archipelago;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;

public class ArchipelagoAgentFactory implements AgentFactory, Constants {

    private static ArchipelagoAgentFactory instance = new ArchipelagoAgentFactory();

    private ArchipelagoAgentFactory() {
    }

    public static ArchipelagoAgentFactory getInstance() {
	return instance;
    }

    public Agent create() {
	return new ArchipelagoAgent();

    }

    @Override
    public Agent create(Simulation simulation, int generationNumber, int agentId, int maxSteps,
	    int startX, int startY) {
	return new ArchipelagoAgent(simulation, generationNumber, agentId, maxSteps, startX,
		startY);
    }

}
