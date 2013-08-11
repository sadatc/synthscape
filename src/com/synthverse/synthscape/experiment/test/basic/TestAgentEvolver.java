package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class TestAgentEvolver extends Evolver implements Constants {

    protected TestAgentEvolver(Simulation simulation) {
	super(simulation);
    }

    public Agent createFactoryAgent(Simulation simulation, Species species, int generation,
	    int agentId, int maxSteps, int x, int y) {
	return new TestAgent(simulation, species, generation, agentId, maxSteps, x, y);

    }

    @Override
    public Agent getSeedAgent(Species species, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Agent getEvolvedAgent(Species species, int generationId, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub

    }

}
