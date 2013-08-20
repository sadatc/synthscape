package com.synthverse.synthscape.core;

import java.util.ArrayDeque;

public abstract class AgentFactory implements Constants {

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
	if(result == null) {
	    result = createNewFactoryAgent(null);
	} else {
	    D.p("providing reclaimed agent#"+result.getId());
	}
	result.setSpecies(species);
	return result;
    }

    public abstract Agent createNewFactoryAgent(Species species);

}
