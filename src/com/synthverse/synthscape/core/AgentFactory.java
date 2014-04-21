package com.synthverse.synthscape.core;

import java.util.ArrayDeque;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

public abstract class AgentFactory implements Constants {

    private static Logger logger = Logger.getLogger(AgentFactory.class.getName());

    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    protected Simulation simulation;

    ArrayDeque<Agent> availableAgents = new ArrayDeque<Agent>();

    protected void reclaimAgent(Agent agent) {
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

	Agent result = availableAgents.pollLast();
	if (result == null) {
	    result = createNewFactoryAgent(species);
	    // TODO: why was this null before? I've set it to species, will this
	    // break anything?
	}
	result.setSpecies(species);
	return result;
    }

    public abstract Agent createNewFactoryAgent(Species species);

    public Simulation getSimulation() {
	return this.simulation;
    }

}
