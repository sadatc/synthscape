package com.synthverse.synthscape.core;

import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.synthverse.evolver.core.CentralizedEvolutionEngine;
import com.synthverse.util.LogUtils;

public abstract class AgentFactory implements Constants {

    private static Logger logger = Logger.getLogger(AgentFactory.class.getName());

    static {
	LogUtils.applyDefaultSettings(logger, Level.ALL);
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
	    result = createNewFactoryAgent(null);
	    //D.p("getNewFactoryAgent() returning brand new agent with id:"+result.getAgentId());
	} else {
	    //D.p("getNewFactoryAgent() returning recycled agent with id:"+result.getAgentId());
	}
	result.setSpecies(species);
	return result;
    }

    public abstract Agent createNewFactoryAgent(Species species);

    public Simulation getSimulation() {
	return this.simulation;
    }

}
