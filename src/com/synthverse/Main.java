package com.synthverse;

import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.experiment.dissertation.islands.PopulationIslandSimulation;

public class Main {
    public static Settings settings = Settings.getInstance();

    public static void main(String[] args) {
	settings.processCommandLineInput(args);
	PopulationIslandSimulation.main(args);

    }
}
