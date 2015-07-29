package com.synthverse;

import java.util.logging.Logger;

import org.jfugue.player.Player;
import org.jfugue.rhythm.Rhythm;
import org.jfugue.theory.ChordProgression;

import com.synthverse.synthscape.core.EvolutionaryModel;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.gui.FancySimulationUI;
import com.synthverse.synthscape.evolutionarymodel.alife.ALifeEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandSimulation;
import com.synthverse.util.LogUtils;
import com.synthverse.util.SoundUtils;

public class Main {
	public static Settings settings = Settings.getInstance();
	private static Logger logger = Logger.getLogger(Main.class.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public static void main(String[] args) {
		try {

			SoundUtils.playTest();

			settings.originalArgs = args;
			settings.processCommandLineInput(args);

			Thread coreEvolutionThread = null;

			if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ISLAND_MODEL) {
				// island model
				coreEvolutionThread = new Thread(new PopulationIslandSimulation.CoreSimThread(), "IslandCoreSimThread");

			} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
				// embodied model
				coreEvolutionThread = new Thread(new EmbodiedEvolutionSimulation.CoreSimThread(),
						"EmbodiedCoreSimThread");

			} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ALIFE_MODEL) {
				// alife model
				coreEvolutionThread = new Thread(new ALifeEvolutionSimulation.CoreSimThread(), "ALifeCoreSimThread");
			} else {
				logger.severe("UNKNOWN MODEL REQUESTED!");
				System.exit(1);
			}

			if (coreEvolutionThread != null) {
				if (settings.SHOW_GRAPHICS) {
					// this starts two threads: one for graphics and another for
					// the core evolution
					FancySimulationUI.main(coreEvolutionThread, args);
				} else {
					coreEvolutionThread.start();
				}
			}

		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}
}
