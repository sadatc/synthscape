package com.synthverse.evolver.islands;

import java.util.HashMap;
import java.util.List;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ArchipelagoEvolver extends Evolver implements Constants {

    HashMap<Species, PopulationIslandEvolver> speciesIslandMap = new HashMap<Species, PopulationIslandEvolver>();

    public ArchipelagoEvolver(Simulation simulation) {
	super(simulation);

    }

    public List<Agent> getRepresentativeAgents() {
	List<Agent> representatives = null;

	return representatives;
    }

    public void initPopulationIslands() {

	for (Species species : simulation.getSpeciesComposition()) {
	    PopulationIslandEvolver island = new PopulationIslandEvolver(
		    simulation, species,
		    simulation.getNumberOfAgentsPerSpecies());
	    speciesIslandMap.put(species, island);
	}
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
	return speciesIslandMap.get(parentAgent.getSpecies()).getEvolvedAgent(
		parentAgent, x, y);
    }

}
