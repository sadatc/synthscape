package com.synthverse;

import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.experiment.dissertation.islands.PopulationIslandSimulation;

public class Main {
    static Settings settings = Settings.getInstance();

    public static void main(String[] args) {
	settings.processCommandLineInput(args);

	PopulationIslandSimulation.main(args);
	

    }
}
