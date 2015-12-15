package com.synthverse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.EvolutionaryModel;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.gui.FancySimulationUI;
import com.synthverse.synthscape.evolutionarymodel.alife.ALifeEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandSimulation;
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

			int runBlocks = settings.processCommandLineInput(args);

			if (runBlocks > 0) {
				String worldWidths[] = { "15", "25" };
				String resourceDensities[] = { "0.05", "0.15" };
				String difficulties[] = { "", "-env_diff" };

				for (String width : worldWidths) {
					for (String rdensity : resourceDensities) {
						for (String difficulty : difficulties) {

							List<String> argList = new ArrayList<String>();
							Collections.addAll(argList, args);

							int blockPosition = 0;
							// remove the param "blocks" and the next param
							// value after that
							for (int i = 0; i < argList.size(); i++) {
								if (argList.get(i).equals("-blocks")) {
									blockPosition = i;
									break;
								}
							}
							// blocks param
							argList.remove(blockPosition);
							// param value
							argList.remove(blockPosition);

							argList.add("-width");
							argList.add(width);
							argList.add("-height");
							argList.add(width);
							argList.add("-rdensity");
							argList.add(rdensity);

							argList.add("-repeat");
							argList.add(Integer.toString(runBlocks));

							if (!difficulty.equals("")) {
								argList.add(difficulty);
							}

							// now issue the commands
							String[] modifiedArgs = new String[argList.size()];
							modifiedArgs = argList.toArray(modifiedArgs);

							settings.originalArgs = modifiedArgs;
							settings.processCommandLineInput(modifiedArgs);
							startSimulation(modifiedArgs);
							D.p("Came Here!!!!");

						}
					}
				}

			} else {

				Thread theThread = startSimulationThread();
				theThread.join();
			}

		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void startSimulation(String[] args) {

		if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ISLAND_MODEL) {
			// island model
			PopulationIslandSimulation.main(args);

		} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
			// embodied model
			EmbodiedEvolutionSimulation.main(args);

		} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ALIFE_MODEL) {
			// alife model
			ALifeEvolutionSimulation.main(args);
		} else {
			logger.severe("UNKNOWN MODEL REQUESTED!");
			System.exit(1);
		}

	}

	public static Thread startSimulationThread() throws InterruptedException {

		Thread coreEvolutionThread = null;

		if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ISLAND_MODEL) {
			// island model
			coreEvolutionThread = new Thread(new PopulationIslandSimulation.CoreSimThread(), "IslandCoreSimThread");

		} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
			// embodied model
			coreEvolutionThread = new Thread(new EmbodiedEvolutionSimulation.CoreSimThread(), "EmbodiedCoreSimThread");

		} else if (settings.EVOLUTIONARY_MODEL == EvolutionaryModel.ALIFE_MODEL) {
			// alife model
			coreEvolutionThread = new Thread(new ALifeEvolutionSimulation.CoreSimThread(), "ALifeCoreSimThread");
		} else {
			logger.severe("UNKNOWN MODEL REQUESTED!");
			System.exit(1);
		}

		if (coreEvolutionThread != null) {
			if (settings.SHOW_GRAPHICS) {
				// this starts two threads: one for graphics and another
				// for
				// the core evolution
				FancySimulationUI.main(coreEvolutionThread, settings.originalArgs);
			} else {
				coreEvolutionThread.start();
			}
		}
		return coreEvolutionThread;

	}
}
