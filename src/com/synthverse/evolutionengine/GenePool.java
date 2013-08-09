package com.synthverse.evolutionengine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.util.LogUtils;

/**
 * The GenePool is the entry point to the evolution engine. Each instance is
 * responsible for evolving a particular "species". At any given point, a new
 * agent might be requested and an evaluated agent can be returned.
 * 
 * Internally, the genepool maintains a state:
 * 
 * Seeding phase: it produces random agents that needs to be evaluated Evolution
 * phase: it has enough evaluated agents to carry on producing evolved agents.
 * 
 * The genepool essentially creates and manages an evolution engine
 * 
 * 
 * 1. during seeding phase, only produces random agent when requested 2. only
 * once populationSize amount of agents have been reclaimed, evolution phase
 * begins 3. during evolution phase: request for new agents produces an agent
 * from next gen
 * 
 * 
 * 
 * 
 * @author sadat
 * 
 */
public class GenePool {

    AgentCache agentCache = null;
    AgentFactory agentFactory = null;
    int populationSize = 0;
    int numSeedAgentsRequested = 0;
    int numEvolvedAgentsRequested = 0;
    boolean isSeedingPhase = true;

    private static Logger log = Logger.getLogger(GenePool.class.getName());

    static {
	LogUtils.applyDefaultSettings(log, Level.ALL);
    }

    @SuppressWarnings("unused")
    private GenePool() {
	throw new AssertionError("GenePool constructor is restricted");
    }

    public GenePool(AgentFactory agentFactory, int populationSize) {
	if (populationSize > 0) {
	    this.agentFactory = agentFactory;
	    this.populationSize = populationSize;
	    isSeedingPhase = true;
	    numSeedAgentsRequested = 0;
	    numEvolvedAgentsRequested = 0;
	    // create twice the number of agents
	    agentCache = new AgentCache(agentFactory, populationSize * 2);
	} else {
	    throw new AssertionError("invalid condition: populationSize < 0");
	}
    }

    /**
     * This returns an agent. Depending on the internal state of the genepool,
     * either a fresh new agent with completely random genotype is generated, OR
     * an evolved agent is generated.
     */
    public Agent getNewAgent() {
	Agent agent = null;

	if (isSeedingPhase) {
	    if (numSeedAgentsRequested <= populationSize) {
		agent = agentCache.borrowAgent();
		numSeedAgentsRequested++;
		agent.randomizeGenotype();
	    } else {
		throw new AssertionError(
			"already generated maximum allowed seed agents");
	    }

	} else {
	    // evolution phase
	    if (numEvolvedAgentsRequested <= populationSize) {
		agent = agentCache.borrowAgent();
		numSeedAgentsRequested++;
		agent.randomizeGenotype();
	    } else {
		throw new AssertionError(
			"already generated maximum allowed seed agents");
	    }

	}

	return agent;
    }

    /**
     * An agent that has completed its lifecycle and has had it's fitness
     * evaluated is submitted to be reclaimed by the gene pool
     * 
     * @param agent
     */
    public void reclaimUsedAgent(Agent agent) {
	log.info("Used agent submitted fore reclaiming...");
    }

    /**
     * Resets to the seeding phase.
     */
    public void reset() {

    }

}
