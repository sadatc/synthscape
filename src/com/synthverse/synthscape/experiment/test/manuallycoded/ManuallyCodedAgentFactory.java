package com.synthverse.synthscape.experiment.test.manuallycoded;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ManuallyCodedAgentFactory extends AgentFactory implements
	Constants {

    protected ManuallyCodedAgentFactory(Simulation simulation) {
	super(simulation);

    }

    @Override
    public Agent getNewFactoryAgent(Species species) {
	return new ManuallyCodedAgent(simulation, species);
    }

}
