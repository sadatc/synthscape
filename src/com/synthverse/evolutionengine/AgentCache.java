package com.synthverse.evolutionengine;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;

/**
 * This provides a cache of agents to minimize object creation/destruction. A
 * cache of agents is maintained and agents are borrowed and then returned back
 * to the cache
 * 
 * @author sadat
 * 
 */
public class AgentCache {
    private static Logger log = Logger.getLogger(AgentCache.class.getName());

    private ArrayList<Agent> activeEntities = null;
    private ConcurrentLinkedQueue<Agent> availableEntities = null;

    private int numActive = 0;
    private int size;

    static {
	log.setLevel(Level.INFO);
    }

    @SuppressWarnings("unused")
    private AgentCache() {
	throw new AssertionError("AgentCache constructor is restricted");
    }

    public AgentCache(AgentFactory agentFactory, int size) {

	if (size > 0) {
	    this.size = size;
	    activeEntities = new ArrayList<Agent>(size);
	    availableEntities = new ConcurrentLinkedQueue<Agent>();

	    for (int i = 0; i < size; i++) {
		availableEntities.add(agentFactory.createFactoryAgent());
	    }
	    numActive = 0;

	} else {
	    throw new AssertionError(
		    "Trying to initialize agent cache with size = " + size);
	}
    }

    public int getSize() {
	return size;
    }

    public int getNumActive() {
	return this.numActive;
    }

    public Agent borrowAgent() {
	log.fine(">getAgentFromCache");
	Agent entity = null;
	if (numActive < size) {
	    entity = availableEntities.remove();
	    activeEntities.add(entity);
	    numActive++;
	} else {
	    log.severe("ran out of entities");
	    System.exit(1);
	}
	return entity;
    }

    public void returnAgent(Agent entity) {
	log.fine("<returnAgent");
	if (numActive > 0) {
	    if (activeEntities.contains(entity)) {
		activeEntities.remove(entity);
		numActive--;
		availableEntities.add(entity);
	    } else {

		if (!availableEntities.contains(entity)) {
		    log.severe("Requested to return unknown entity back to the cache");
		    System.exit(1);
		}

	    }
	}
    }
}
