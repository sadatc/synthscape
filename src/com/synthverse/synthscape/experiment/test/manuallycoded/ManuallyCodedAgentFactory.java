package com.synthverse.synthscape.experiment.test.manuallycoded;

import com.synthverse.evolutionengine.model.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ManuallyCodedAgentFactory implements AgentFactory, Constants {

    private static ManuallyCodedAgentFactory instance = new ManuallyCodedAgentFactory();

    private ManuallyCodedAgentFactory() {
    }

    public static ManuallyCodedAgentFactory getInstance() {
	return instance;
    }

    public Agent createFactoryAgent(Species species) {
	return new ManuallyCodedAgent(species);

    }

    @Override
    public Agent createFactoryAgent(Simulation simulation, Species species, int generationNumber, int agentId,
	    int maxSteps, int startX, int startY) {
	return new ManuallyCodedAgent(simulation, species, generationNumber, agentId, maxSteps,
		startX, startY);
    }

    @Override
    public Agent getSeedAgent(Evolver evolver,
	    Species species) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Agent getEvolvedAgent(Evolver evolver,
	    Species species) {
	// TODO Auto-generated method stub
	return null;
    }

}
