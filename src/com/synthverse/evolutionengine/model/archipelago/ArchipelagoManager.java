package com.synthverse.evolutionengine.model.archipelago;

import java.util.ArrayList;
import java.util.List;

import com.synthverse.synthscape.core.Experiment;

public class ArchipelagoManager {

    private Experiment experiment;

    private List<PopulationIsland> islands = new ArrayList<PopulationIsland>();

    private ArchipelagoManager() {
	throw new AssertionError("ArchipelagoManager constructor is restricted");
    }

    public ArchipelagoManager(Experiment experiment) {
	setExperiment(experiment);

    }

    private void setExperiment(Experiment experiment) {
	this.experiment = experiment;
    }

}
