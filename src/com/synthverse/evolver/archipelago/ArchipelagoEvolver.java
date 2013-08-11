package com.synthverse.evolver.archipelago;

import java.util.ArrayList;
import java.util.List;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ArchipelagoEvolver extends Evolver {

    protected ArchipelagoEvolver(Simulation simulation) {
	super(simulation);

    }

    private List<PopulationIsland> islands = new ArrayList<PopulationIsland>();

    public List<Agent> getRepresentativeAgents() {
	List<Agent> representatives = null;

	return representatives;
    }

    public void initPopulationIslands() {

	for (Species species : simulation.getSpeciesComposition()) {
	    PopulationIsland island = new PopulationIsland(simulation.getAgentFactory(), species,
		    simulation.getNumberOfAgentsPerSpecies());
	    islands.add(island);
	}
    }

    @Override
    public void init() {
	initPopulationIslands();

    }

    @Override
    public Agent getSeedAgent(Species species, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Agent getEvolvedAgent(Species species, int generationId, int x, int y) {
	// TODO Auto-generated method stub
	return null;
    }

}
