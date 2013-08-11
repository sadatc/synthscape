package com.synthverse.evolver.core;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public abstract class Evolver {

    protected Simulation simulation;

    @SuppressWarnings("unused")
    private Evolver() {
	throw new AssertionError("Evolver constructor is restricted");
    }

    protected Evolver(Simulation simulation) {
	this.simulation = simulation;
    }

    public abstract Agent getSeedAgent(Species species, int x, int y);

    public abstract Agent getEvolvedAgent(Species species, int generationId, int x, int y);

    public abstract void init();

}
