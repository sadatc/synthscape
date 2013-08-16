package com.synthverse.synthscape.core;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public abstract class AgentFactory implements Constants {

    protected Simulation simulation;

    ArrayDeque<Agent> availableAgents = new ArrayDeque<Agent>(AGENT_FACTORY_CACHE_SIZE);
    Set<Agent> activeAgents = new HashSet<Agent>(AGENT_FACTORY_CACHE_SIZE);

    private void initCache() {
	for (int i = 0; i < AGENT_FACTORY_CACHE_SIZE; i++) {
	    Agent agent = createNewFactoryAgent(null);
	    agent.setRetirable(false);
	    availableAgents.add(agent);
	}
	D.p("AgentCache: size =  "+availableAgents.size());
    }

    @SuppressWarnings("unused")
    private AgentFactory() {
	throw new AssertionError("AgentFactory constructor is restricted");
    }

    protected AgentFactory(Simulation simulation) {
	this.simulation = simulation;
	initCache();

    }

    public Agent getNewFactoryAgent(Species species) {
	Agent result = availableAgents.pollLast();

	if (availableAgents == null) {
	    // no luck...
	    // reclaim all that can be reclaimed
	    for (Agent agent : activeAgents) {
		int reclaimCount = 0;
		if (agent.isRetirable()) {
		    activeAgents.remove(agent);
		    availableAgents.add(agent);
		    reclaimCount++;
		}
		D.p("AgentCache: reclaimed number of agents ="+reclaimCount);
	    }
	    // try one more time...
	    result = availableAgents.pollLast();

	    // if still no luck, increase the cache
	    if (result == null) {
		initCache();
		result = availableAgents.pollLast();
		if (result == null) {
		    D.p("Fatal problem: investigate");
		    System.exit(0);
		}
	    }

	}
	result.setSpecies(species);
	activeAgents.add(result);
	return result;
    }

    public abstract Agent createNewFactoryAgent(Species species);

}
