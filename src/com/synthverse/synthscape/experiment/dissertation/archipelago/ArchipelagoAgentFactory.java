package com.synthverse.synthscape.experiment.dissertation.archipelago;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ArchipelagoAgentFactory implements AgentFactory, Constants {

    private static ArchipelagoAgentFactory instance = new ArchipelagoAgentFactory();

    private ArchipelagoAgentFactory() {
    }

    public static ArchipelagoAgentFactory getInstance() {
	return instance;
    }

    public Agent createFactoryAgent() {
	return new ArchipelagoAgent();

    }

    @Override
    public Agent createFactoryAgent(Simulation simulation, Species species, int generationNumber, int agentId, int maxSteps,
	    int startX, int startY) {
	return new ArchipelagoAgent(simulation, species, generationNumber, agentId, maxSteps, startX,
		startY);
    }

}
