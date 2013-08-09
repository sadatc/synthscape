package com.synthverse.synthscape.core;

public interface AgentFactory {

    public Agent createFactoryAgent();

    public Agent createFactoryAgent(Simulation simulation, Species species,
	    int generationNumber, int agentId, int maxSteps, int startX,
	    int startY);
}
