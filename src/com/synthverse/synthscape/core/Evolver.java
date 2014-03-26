package com.synthverse.synthscape.core;

import java.util.List;

public abstract class Evolver {

    protected Simulation simulation;

    @SuppressWarnings("unused")
    private Evolver() {
	throw new AssertionError("Evolver constructor is restricted");
    }

    protected Evolver(Simulation simulation) {
	this.simulation = simulation;
    }

    public abstract void evolve();

    public abstract Agent getAgent(Species species, int x, int y);

    public abstract void init();

    public abstract void provideFeedback(List<Agent> agents, Stats simStats);

    public abstract int getGeneration();

}
