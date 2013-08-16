package com.synthverse.synthscape.experiment.test.manuallycoded;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ManuallyCodedEvolver extends Evolver implements Constants {

    protected ManuallyCodedEvolver(Simulation simulation) {
	super(simulation);
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub

    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	Agent agent = simulation.getAgentFactory().getNewFactoryAgent(species);
	agent.setGeneration(SEED_GENERATION_NUMBER);

	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	agent.setX(x);
	agent.setY(y);

	return agent;

    }

}
