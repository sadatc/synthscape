package com.synthverse.evolver.islands;

import java.util.ArrayList;
import java.util.List;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class PopulationIslandEvolver extends Evolver implements Constants {

    public PopulationIslandEvolver(Simulation simulation) {
	super(simulation);

    }

    private List<PopulationIsland> islands = new ArrayList<PopulationIsland>();

    public List<Agent> getRepresentativeAgents() {
	List<Agent> representatives = null;

	return representatives;
    }

    public void initPopulationIslands() {

	for (Species species : simulation.getSpeciesComposition()) {
	    PopulationIsland island = new PopulationIsland(
		    simulation.getAgentFactory(), species,
		    simulation.getNumberOfAgentsPerSpecies());
	    islands.add(island);
	}
    }

    @Override
    public void init() {
	D.p("initing population island evolver...");
	initPopulationIslands();
    }

    @Override
    public Agent getSeedAgent(Species species, int x, int y) {
	Agent agent = simulation.getAgentFactory().getNewFactoryAgent(species);
	agent.setGeneration(SEED_GENERATION_NUMBER);

	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	agent.setX(x);
	agent.setY(y);

	return agent;
    }

    @Override
    public Agent getEvolvedAgent(Agent parentAgent, int x, int y) {
	parentAgent.setGeneration(parentAgent.getGeneration() + 1);

	parentAgent.setX(x);
	parentAgent.setY(y);

	// perhaps do other things to get it evolved.
	parentAgent.reset();

	return parentAgent;
    }

}
