package com.synthverse.synthscape.test.basic;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class TestAgentFactory extends AgentFactory {

    protected TestAgentFactory(Simulation simulation) {
	super(simulation);
    }

    @Override
    public Agent createNewFactoryAgent(Species species) {
	return new TestAgent(simulation, species);
    }

}
