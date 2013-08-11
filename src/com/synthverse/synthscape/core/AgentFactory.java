package com.synthverse.synthscape.core;

public abstract class AgentFactory {

    protected Simulation simulation;

    @SuppressWarnings("unused")
    private AgentFactory() {
	throw new AssertionError("AgentFactory constructor is restricted");
    }

    protected AgentFactory(Simulation simulation) {
	this.simulation = simulation;

    }

    public abstract Agent getNewFactoryAgent(Species species);

}
