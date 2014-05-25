package com.synthverse.synthscape.core;

import java.util.List;

public abstract class Evolver {

    protected Simulation simulation;
    
    protected AgentFactory agentFactory;
    
    
    @SuppressWarnings("unused")
    private Evolver() {
	throw new AssertionError("Evolver constructor is restricted");
    }

    public Evolver(Simulation simulation, AgentFactory agentFactory) {
	this.simulation = simulation;
	this.agentFactory = agentFactory;
    }

    public abstract int evolve();

    public abstract Agent getAgent(Species species, int x, int y);

    public abstract void init();

    public abstract void provideFeedback(List<Agent> agents, EventStats simStats);

    public abstract int getGeneration();

}
