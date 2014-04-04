package com.synthverse.synthscape.evolutionarymodel.embodied;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class EmbodiedAgentFactory extends AgentFactory implements Constants {

    protected EmbodiedAgentFactory(Simulation simulation) {
	super(simulation);
    }

    public Agent createNewFactoryAgent(Species species, int poolSize) {
	EmbodiedAgent agent = new EmbodiedAgent(simulation, species, poolSize);
	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	agent.setInteractionMechanisms(simulation.getInteractionMechanisms());
	return agent;
    }

    @Override
    public Agent createNewFactoryAgent(Species species) {
	return createNewFactoryAgent(species, Main.settings.DEFAULT_EMBODIED_AGENT_POOL_SIZE);
    }

}
