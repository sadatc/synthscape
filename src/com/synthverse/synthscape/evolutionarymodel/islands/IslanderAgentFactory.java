package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.util.LogUtils;

public class IslanderAgentFactory extends AgentFactory implements Constants {

	protected static Logger logger = Logger
			.getLogger(IslanderAgentFactory.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public IslanderAgentFactory(Simulation simulation) {
		super(simulation);
	}

	@Override
	public Agent createNewFactoryAgent(Species species) {
		// logger.info("IslanderAgentFactory.createNewFactoryAgent()");
		IslanderAgent agent = new IslanderAgent(simulation, species);
		agent.setMaxSteps(simulation.getMaxStepsPerAgent());
		agent.setInteractionMechanisms(simulation.getInteractionMechanisms());
		return agent;
	}

}
