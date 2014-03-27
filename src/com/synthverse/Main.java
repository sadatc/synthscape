package com.synthverse;

import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.EvolutionaryModel;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandSimulation;

public class Main {
    public static Settings settings = Settings.getInstance();

    public static void main(String[] args) {
	settings.processCommandLineInput(args);

	if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ISLAND_MODEL) {
	    // island model
	    PopulationIslandSimulation.main(args);
	} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
	    // embodied model
	    EmbodiedEvolutionSimulation.main(args);
	    D.p(settings.EVOLUTIONARY_MODEL+" is not ready yet"); 
	} else {
	    // alife model
	    D.p(settings.EVOLUTIONARY_MODEL+" is not ready yet");
	}

    }
}
