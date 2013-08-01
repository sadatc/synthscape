package com.synthverse.synthscape.core;

public interface AgentFactory {

	public Agent create();

	public Agent create(Simulation simulation, int generation, int agentId,
			int x, int y);
}
