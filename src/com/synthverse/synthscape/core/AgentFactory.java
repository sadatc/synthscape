package com.synthverse.synthscape.core;

public interface AgentFactory {
	
	
	public Agent create();

	public Agent create(Simulation simulation, long generation, long agentId,
			int x, int y);
}
