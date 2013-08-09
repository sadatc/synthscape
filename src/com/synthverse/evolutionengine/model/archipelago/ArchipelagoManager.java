package com.synthverse.evolutionengine.model.archipelago;

import java.util.ArrayList;
import java.util.List;

import com.synthverse.synthscape.core.Experiment;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class ArchipelagoManager {

    private Simulation simulation;

    private List<PopulationIsland> islands = new ArrayList<PopulationIsland>();

    private ArchipelagoManager() {
	throw new AssertionError("ArchipelagoManager constructor is restricted");
    }

    public ArchipelagoManager(Simulation simulation) {
	this.simulation = simulation;
	setupPopulationIslands();
    }

    private void setupPopulationIslands() {
	Experiment experiment = simulation.getExperiment();
	for (Species species : experiment.getSpeciesComposition()) {
	    PopulationIsland island = new PopulationIsland(
		    simulation.getAgentFactory(), species,
		    experiment.getNumberOfAgentsPerSpecies());
	    islands.add(island);
	}

    }

}
