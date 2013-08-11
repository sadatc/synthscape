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

    @Override
    public Agent getSeedAgent(Species species) {
	Agent agent = simulation.getAgentFactory().getNewFactoryAgent(species);

	return agent;
    }

    @Override
    public Agent getEvolvedAgent(Agent parentAgent, int x, int y) {
	parentAgent.setGeneration(parentAgent.getGeneration() + 1);

	parentAgent.setX(x);
	parentAgent.setY(y);
	
	// perhaps do other things to get it evolved.

	return parentAgent;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub

    }

}
