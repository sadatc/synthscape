package com.synthverse.evolver.islands;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ArchipelagoEvolver extends Evolver implements Constants {

    protected ArchipelagoEvolver(Simulation simulation) {
	super(simulation);
	// TODO Auto-generated constructor stub
    }
    /*

    HashMap<Species, PopulationIslandEvolver> speciesIslandMap = new HashMap<Species, PopulationIslandEvolver>();
    List<PopulationIslandEvolver> islands = new ArrayList<PopulationIslandEvolver>();

    public ArchipelagoEvolver(Simulation simulation) {
	super(simulation);

    }

    public List<Agent> getRepresentativeAgents() {
	List<Agent> representatives = null;

	return representatives;
    }

    public void initPopulationIslands() {

	for (Species species : simulation.getSpeciesComposition()) {
	    PopulationIslandEvolver island = new PopulationIslandEvolver(simulation, species,
		    simulation.getNumberOfAgentsPerSpecies());
	    speciesIslandMap.put(species, island);
	    islands.add(island);
	}
	D.p("number of islands = " + islands.size());
    }

    @Override
    public void init() {
	D.p("initing population island evolver...");
	initPopulationIslands();
    }

    @Override
    public Agent getSeedAgent(Species species, int x, int y) {

	return speciesIslandMap.get(species).getSeedAgent(species, x, y);
    }

    @Override
    public Agent getEvolvedAgent(Agent parentAgent, int x, int y) {
	return speciesIslandMap.get(parentAgent.getSpecies()).getEvolvedAgent(parentAgent, x, y);
    }

    @Override
    public void generateNextGeneration() {

	// let's compute the global fitness
	// and share it across all agents...
	double fitness = computeGlobalFitness();
	for (Agent agent : simulation.getAgents()) {
	    agent.setFitness(fitness);
	}

	for (PopulationIslandEvolver island : islands) {
	    island.generateNextGeneration();
	}

    }

    private double computeGlobalFitness() {
	double result = 0.0;
	D.p("]]]computing global fitness");

	for (Event event : simulation.simStats.getEvents()) {
	    result += simulation.simStats.getValue(event);
	}

	return result;
    }

    @Override
    public Agent getAgent(Species species, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }
    */

    @Override
    public Agent getAgent(Species species, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void init() {
	// TODO Auto-generated method stub
	
    }

}
