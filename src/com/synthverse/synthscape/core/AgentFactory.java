package com.synthverse.synthscape.core;

import java.util.ArrayDeque;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedAgent;
import com.synthverse.util.LogUtils;

public abstract class AgentFactory implements Constants {

	private static Logger logger = Logger.getLogger(AgentFactory.class
			.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	protected Simulation simulation;

	public ArrayDeque<Agent> availableAgents = new ArrayDeque<Agent>();

	public void reclaimAgent(Agent agent) {
		availableAgents.add(agent);
		
	//	agent = null;
	}

	@SuppressWarnings("unused")
	private AgentFactory() {
		throw new AssertionError("AgentFactory constructor is restricted");
	}

	protected AgentFactory(Simulation simulation) {
		this.simulation = simulation;

	}

	public Agent getNewFactoryAgent(Species species) {

		Agent result = availableAgents.pollLast();
		
		//if(result instanceof EmbodiedAgent && result!=null) {
		//	D.p("came here!");
		//}
		
		if (result == null) {
			result = createNewFactoryAgent(species);
		} 
		
		result.setSpecies(species);
		return result;
	}

	public abstract Agent createNewFactoryAgent(Species species);

	public Simulation getSimulation() {
		return this.simulation;
	}

}
