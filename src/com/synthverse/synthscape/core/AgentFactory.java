package com.synthverse.synthscape.core;

public interface AgentFactory {

    public Agent create();

    public Agent create(Simulation simulation, int generationNumber, int agentId, int maxSteps,
	    int startX, int startY);
}
