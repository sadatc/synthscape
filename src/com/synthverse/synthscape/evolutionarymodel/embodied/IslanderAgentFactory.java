package com.synthverse.synthscape.evolutionarymodel.embodied;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.evolutionarymodel.islands.IslanderAgent;

public class IslanderAgentFactory extends AgentFactory implements Constants {
    

    protected IslanderAgentFactory(Simulation simulation) {
	super(simulation);
    }

    @Override
    public Agent createNewFactoryAgent(Species species) {
	IslanderAgent agent = new IslanderAgent(simulation, species);
	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	agent.setInteractionMechanisms(simulation.getInteractionMechanisms());
	
	
	return agent;
    }

}
