package com.synthverse.evolver.core;

import java.util.List;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;

public abstract class Evolver {

    protected Simulation simulation;

    @SuppressWarnings("unused")
    private Evolver() {
	throw new AssertionError("Evolver constructor is restricted");
    }

    protected Evolver(Simulation simulation) {
	this.simulation = simulation;
    }

    public abstract Agent getAgent(Species species, int x, int y);
    
    public abstract void init();

    public abstract void provideFeedback(List<Agent> agents, Stats simStats);

}
