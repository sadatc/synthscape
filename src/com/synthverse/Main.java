package com.synthverse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.synthverse.synthscape.core.Constants;
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

	@SuppressWarnings("unused")
	public static HashMap<String, String> readExperimentMapFromFile() {
		HashMap<String, String> map = new HashMap<String, String>();
		if (map != null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(Constants.EXPERIMENT_MAP_FILE));
				String line;
				int num = 0;
				while ((line = reader.readLine()) != null) {
					if (!line.trim().equals("") && !line.trim().startsWith("#")) {
						String keyValuePair[] = line.split(":");
						if (keyValuePair.length == 2) {
							// D.p("read key:"+keyValuePair[0].trim());
							map.put(keyValuePair[0].trim(), keyValuePair[1].trim());
							num++;
						}
					}
				}
				D.p("read " + num + " experiment labels from : " + Constants.EXPERIMENT_MAP_FILE);
				reader.close();

			} catch (Exception e) {
				D.p("WARNING! " + Constants.EXPERIMENT_MAP_FILE + " was not found; expecting all simulation params...");
			}

		}

		return map;
	}

	@SuppressWarnings("static-access")
	public static String[] transformArgsForNamedExperiment(String[] args) {
		String[] result = args;

		HashMap<String, String> experimentMap = readExperimentMapFromFile();

		int i = 0;
		String experimentArgs = null;

		for (String arg : args) {
			if (arg.toLowerCase().equals("-experiment")) {
				String experimentName = args[i + 1];
				if (experimentMap.containsKey(experimentName)) {
					experimentArgs = experimentMap.get(experimentName);
				} else {
					logger.severe("ERROR: Experiment '" + experimentName + "' was not found! aborting...");
					System.exit(1);
				}
			}
			i++;
		}

		if (experimentArgs != null) {
			List<String> argList = new ArrayList<String>();
			for (String arg : args) {
				argList.add(arg);
			}
			String[] experimentArg = experimentArgs.split(" ");
			for (String arg : experimentArg) {
				argList.add(arg);
			}

			String[] modifiedArgs = new String[argList.size()];
			modifiedArgs = argList.toArray(modifiedArgs);
			result = modifiedArgs;
		}

		return result;

	}

	public static void main(String[] args) {
		try {

			args = transformArgsForNamedExperiment(args);

			settings.originalArgs = args;

			int runBlocks = settings.processCommandLineInput(args);

			if (runBlocks > 0) {
				String worldWidths[] = { "15", "25" };
				String resourceDensities[] = { "0.05", "0.15" };
				String difficulties[] = { " ", "-env_diff" };

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
							settings.resetSettings();
							// D.p("completed run block...");

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

		String params = "";
		for (String arg : args) {
			params += arg + " ";
		}
		D.p("BLOCK PARAMS:" + params);

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
