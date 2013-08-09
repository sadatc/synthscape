package com.synthverse.synthscape.core;

import com.synthverse.evolutionengine.model.core.Evolver;

public interface AgentFactory {

    public Agent createFactoryAgent(Species species);

    public Agent createFactoryAgent(Simulation simulation, Species species,
	    int generationNumber, int agentId, int maxSteps, int startX,
	    int startY);

    public Agent getSeedAgent(Evolver evolver,
	    Species species);

    public Agent getEvolvedAgent(Evolver evolver,
	    Species species);
}
