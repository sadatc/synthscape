package com.synthverse.synthscape.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public abstract class AgentFactory implements Constants {

	private static Logger logger = Logger.getLogger(AgentFactory.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	protected Simulation simulation;

	public HashMap<Species, ArrayDeque<Agent>> speciesAvailableAgentsMap = new HashMap<Species, ArrayDeque<Agent>>();

	public void reclaimAgent(Agent agent) {
		Species species = agent.getSpecies();
		ArrayDeque<Agent> availableAgents = speciesAvailableAgentsMap.get(species);
		if (availableAgents == null) {
			logger.info("availableAgents sizes should never be different -- investigate");
			System.exit(1);
		}
		availableAgents.add(agent);

	}

	@SuppressWarnings("unused")
	private AgentFactory() {
		throw new AssertionError("AgentFactory constructor is restricted");
	}

	protected AgentFactory(Simulation simulation) {
		this.simulation = simulation;
	}

	public Agent getNewFactoryAgent(Species species) {
		Agent result = null;

		ArrayDeque<Agent> availableAgents = speciesAvailableAgentsMap.get(species);
		if (availableAgents == null) {
			availableAgents = new ArrayDeque<Agent>();
			speciesAvailableAgentsMap.put(species, availableAgents);
		} else {
			result = availableAgents.pollLast();
		}

		// if(result instanceof EmbodiedAgent && result!=null) {
		// D.p("about to return a recycled embodied agent -- must make sure its
		// all reset internally");
		// }

		if (result == null) {
			result = createNewFactoryAgent(species);
		}

		return result;
	}

	public abstract Agent createNewFactoryAgent(Species species);

	public Simulation getSimulation() {
		return this.simulation;
	}

}
