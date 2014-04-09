package com.synthverse.synthscape.test.basic;

import java.util.List;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Stats;

public class TestAgentEvolver extends Evolver implements Constants {
    
    int generation;

    protected TestAgentEvolver(Simulation simulation, AgentFactory agentFactory) {
	super(simulation, agentFactory);
	generation = 0;
    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	Agent agent = simulation.getAgentFactory().getNewFactoryAgent(species);
	agent.reset();
	agent.setGeneration(SEED_GENERATION_NUMBER);

	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	agent.setX(x);
	agent.setY(y);
	return agent;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub

    }
    
    @Override
    public void provideFeedback(List<Agent> agents, Stats simStats) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void evolve() {
	// TODO Auto-generated method stub
	generation++;
	
    }

    @Override
    public int getGeneration() {
	// TODO Auto-generated method stub
	return generation;
    }

}
