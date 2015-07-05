package com.synthverse;

import java.util.logging.Logger;

import com.synthverse.synthscape.core.EvolutionaryModel;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.evolutionarymodel.alife.ALifeEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.alife.ALifeEvolutionSimulationFancyUI;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandSimulationFancyUI;
import com.synthverse.util.LogUtils;

public class Main {
	public static Settings settings = Settings.getInstance();
	private static Logger logger = Logger.getLogger(Main.class.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public static void main(String[] args) {
		try {
			settings.originalArgs = args;
			settings.processCommandLineInput(args);

			if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ISLAND_MODEL) {
				// island model

				if (settings.SHOW_GRAPHICS) {
					// the console simulator will be instantiated in a thread
					// within
					PopulationIslandSimulationFancyUI.main(args);
				} else {
					// this simply starts the thread that runs the console
					// simulator
					Thread t = new Thread(new PopulationIslandSimulation.PopulationIslandSimulationThread(),
							"PopulationIslandSimulationThread");
					t.start();
				}
			} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
				// embodied model
				if (settings.SHOW_GRAPHICS) {

					throw new RuntimeException("Graphics mode for Embodied model has not been implemented");

				} else {
					EmbodiedEvolutionSimulation.main(args);
				}

			} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ALIFE_MODEL) {
				// alife model

				if (settings.SHOW_GRAPHICS) {
					ALifeEvolutionSimulationFancyUI.main(args);

				} else {
					ALifeEvolutionSimulation.main(args);

				}

			} else {
				logger.severe("UNKNOWN MODEL REQUESTED!");
			}

		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}
}
