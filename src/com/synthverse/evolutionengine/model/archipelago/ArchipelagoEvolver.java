package com.synthverse.evolutionengine.model.archipelago;

import java.util.ArrayList;
import java.util.List;

import com.synthverse.evolutionengine.model.core.Evolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Experiment;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ArchipelagoEvolver implements Evolver {

    private Simulation simulation;

    private List<PopulationIsland> islands = new ArrayList<PopulationIsland>();

    private ArchipelagoEvolver() {
	throw new AssertionError("ArchipelagoEvolver constructor is restricted");
    }

    public ArchipelagoEvolver(Simulation simulation) {
	this.simulation = simulation;
    }

    public List<Agent> getRepresentativeAgents() {
	List<Agent> representatives = null;

	return representatives;
    }

    public void initPopulationIslands() {
	Experiment experiment = simulation.getExperiment();
	for (Species species : experiment.getSpeciesComposition()) {
	    PopulationIsland island = new PopulationIsland(
		    simulation.getAgentFactory(), species,
		    experiment.getNumberOfAgentsPerSpecies());
	    islands.add(island);
	}
    }

    @Override
    public Agent getSeedAgent() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Agent getEvolvedAgent() {
	// TODO Auto-generated method stub
	return null;
    }

}
