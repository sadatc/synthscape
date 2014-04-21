package com.synthverse;

import java.util.logging.Logger;

import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.EvolutionaryModel;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandSimulation;

public class Main {
    public static Settings settings = Settings.getInstance();
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
	try {
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
	} catch (Exception e) {
	    logger.severe(e.getMessage());
	}

    }
}
