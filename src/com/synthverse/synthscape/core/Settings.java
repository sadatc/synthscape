package com.synthverse.synthscape.core;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.synthverse.synthscape.core.gui.BridgeState;

public class Settings implements Constants {

	public enum SeedType {
		NOOPS, ACTIONS, RANDOM;
	}

	private static Settings instance = null;

	public boolean SHOW_GRAPHICS = false;

	public boolean CLUSTERED = false;

	public boolean PEER_REWARDS = false;

	public int SEED = 1;

	public int GENERATIONS = 300;

	public int BENCHMARK_GENERATION = 100;

	public int CLONES_PER_SPECIES = 8;

	public int GENE_POOL_SIZE = 512;

	public double COLLECTION_SITE_DENSITY = 0.02;

	public int NUMBER_OF_COLLECTION_SITES = 8;

	public double PERC_RESOURCE_CAPTURE_GOAL = 0.0;

	public int SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

	public ProblemComplexity PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;

	public EvolutionaryModel EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;

	public String MODEL_SPECIES = "homogenous";

	public String MODEL_INTERACTIONS = "none";

	public int WORLD_WIDTH = 15;

	public int WORLD_HEIGHT = 15;

	public int WORLD_GRIDS = WORLD_WIDTH * WORLD_HEIGHT;

	public double OBSTACLE_DENSITY = 0.125;

	public double RESOURCE_DENSITY = 0.05;

	public double MATING_SUCCESS_RATE = 0.3;

	public double MATING_PROXIMITY_RADIUS = 1;

	public int MATING_GENERATION_FREQUENCY = 10;

	public int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.50);

	public int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.50);

	public Level REQUESTED_LOG_LEVEL = Level.ALL;

	public SeedType SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.RANDOM;

	public int EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;

	public int MAX_STEPS_PER_AGENT = 1024;

	public int REPEAT = 1;

	public String DATA_DIR = File.separator + "tmp";

	public String JOB_NAME = "test";

	public String EXPERIMENT_NAME = "";

	public static boolean RANDOMIZE_SIM_SEED = true;

	public String PERFORMANCE_DATA_FILE = "perf_dat.csv";

	public String EVENT_DATA_FILE = "evnt_dat.csv";

	public String DNA_PROGRESSION_FILE = "dna_dat.gz";

	public String EXPERIMENT_DETAILS_FILE = "exp_det.txt";

	public boolean REPORT_EXPERIMENT_DETAILS = false;

	public String EXPERIMENT_DETAILS_FILE_MAIN = "exp_det.txt";

	public List<String> EXPERIMENT_DETAILS = new ArrayList<String>();

	public InteractionQuality INTERACTION_QUALITY = InteractionQuality.HIGHEST;

	public boolean CONSTRAINED_INTERACTIONS = true;

	public boolean REPORT_DNA_PROGRESSION = false;

	public boolean COMPRESS_DNA_PROGRESSION = false;
	
	public boolean REDUCED_LOGGING = true;

	public boolean SPECIES_LEVEL_REPORT = false;

	public boolean DYNAMIC_EVENNESS = false;

	public boolean MANUAL_EVENNESS = false;

	public boolean ME_RANDOM_POP_RATIO = false;

	public String ME_POP_RATIO = "1:1:1:1"; // d:e:t:p default ratio

	public int DE_INITIAL_CLONES = 4;

	public int DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE = 50;

	public int DE_GENERATIONS_TO_OBSERVE_SIGNAL_CHANGES = 5;

	public int DE_MAX_POPULATION = 24;

	public int ME_MAX_POPULATION = 24;

	public DynamicEvennessAlgorithm DE_ALGORITHM = DynamicEvennessAlgorithm.DE_SIGNAL_DEMAND_BASED_SWITCH;

	public Environment ENVIRONMENT = Environment.RANDOM;

	public int HOLD_EVENT_THRESHOLD = 1;

	public int lastReportedCaptures = 0;
	public int lastReportedGeneration = 0;
	public int lastLoggedGeneration = 0;

	public int experimentNumber = 0;

	public String statusCache = EMPTY_STRING;

	public int generationCounter = 0;

	public String[] originalArgs = null;

	public boolean __showGraphics = false;
	public boolean __useSoundEffects = false;
	public boolean __guiStarted = false;

	public Integer __renderStageLock = new Integer(0);
	public boolean __simulationStarted = false;
	public BridgeState __bridgeState = null;

	public long __animationDelay = Constants.SHORT_PAUSE;

	public int __numDetectors = 0;
	public int __numExtractors = 0;
	public int __numTransporters = 0;
	public int __numProcessors = 0;

	public int _RESOURCE_BOX_WIDTH = 0;
	public int _RESOURCE_BOX_WIDTH_PADDING = 0;

	public int _RESOURCE_BOX_WIDTH_HALF1 = 0;
	public int _RESOURCE_BOX_WIDTH_HALF2 = 0;

	public int _ACTUAL_RESOURCES = 0;
	public int _ACTUAL_OBSTACLES = 0;

	public int _RESOURCE_BOX_LEFT = 0;
	public int _RESOURCE_BOX_RIGHT = 0;
	public int _RESOURCE_BOX_UP = 0;
	public int _RESOURCE_BOX_DOWN = 0;

	public int _RESOURCE_BOX_LEFT_PERIM = 0;
	public int _RESOURCE_BOX_RIGHT_PERIM = 0;

	public int _RESOURCE_BOX_UP_PERIM = 0;
	public int _RESOURCE_BOX_DOWN_PERIM = 0;

	public int _WIDTH_EDGE = 0;
	public int _HEIGHT_EDGE = 0;
	
	public double EXTRACTED_RESOURCE_PERCENT = 0.0;
	

	private Settings() {

	}

	private void printAndStore(String msg) {
		D.p(msg);
		EXPERIMENT_DETAILS.add(msg);
	}

	@SuppressWarnings("static-access")

	public int processRunBlocks(String[] args) {
		int result = 0;

		Options options = new Options();

		options.addOption(OptionBuilder.withArgName("blocks").hasArg().withType(Integer.class)
				.withDescription("final run blocks (e.g. 5)").create("blocks"));

		HelpFormatter formatter = new HelpFormatter();

		// create the parser
		CommandLineParser parser = new BasicParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("blocks")) {
				result = new Integer(line.getOptionValue("blocks")).intValue();

			}
		} catch (ParseException exp) {
			// oops, something went wrong
			if (args.length == 1 && args[0].toLowerCase().contains("help")) {
				// check if this was a request for help...
				formatter.printHelp("com.synthverse.Main", options);
				System.exit(0);
			} else {
				System.err.println("Parsing failed.  Reason: " + exp.getMessage());
				// formatter.printHelp("com.synthverse.Main", options);
				System.exit(1);
			}
		}

		return result;
	}

	@SuppressWarnings("static-access")
	public int processCommandLineInput(String[] args) {
		// build up all the command line options
		int runBlocks = 0;
		Options options = new Options();

		options.addOption(OptionBuilder.withArgName("blocks").hasArg().withType(Integer.class)
				.withDescription("final run blocks (e.g. 5)").create("blocks"));

		options.addOption(new Option("help", "print this message"));

		options.addOption(new Option("slr", "species level report [default: do not report at species level]"));

		options.addOption(new Option("de", "use dynamic evenness"));

		options.addOption(new Option("me", "use manual evenness"));

		options.addOption(new Option("ranpr", "in manual evenness use random population ratio"));

		options.addOption(OptionBuilder.withArgName("manpr").hasArg()
				.withDescription("manual pop ratio [" + ME_POP_RATIO + "]").create("manpr"));

		options.addOption(OptionBuilder.withArgName("degofp").hasArg().withType(Integer.class)
				.withDescription("dynamic evenness param: gen. to observe fitness performance ["
						+ DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE + "]")
				.create("degofp"));

		options.addOption(OptionBuilder.withArgName("demp").hasArg().withType(Integer.class)
				.withDescription("dynamic evenness param: max population [" + DE_MAX_POPULATION + "]").create("demp"));

		options.addOption(OptionBuilder.withArgName("memp").hasArg().withType(Integer.class)
				.withDescription("manual evenness param: max population [" + ME_MAX_POPULATION + "]").create("memp"));

		options.addOption(new Option("env_diff", "use difficult environment [default: random environment]"));

		options.addOption(new Option("env_vdiff", "use very difficult environment [default: random environment]"));

		options.addOption(new Option("no_randomization", "do not randomize each sim [default: randomize]"));

		options.addOption(new Option("show_graphics", "show graphics [default: don't show graphics]"));

		options.addOption(new Option("clustered", "start agent clustered [default: distributed]"));

		options.addOption(new Option("peer_rewards", "peer rewards [default: no peer rewards]"));

		options.addOption(new Option("use_4_tasks", "use 4 tasks, instead of the default 3"));

		options.addOption(
				OptionBuilder.withArgName("log").hasArg().withDescription("(off,all,info) [all]").create("log"));

		options.addOption(OptionBuilder.withArgName("model").isRequired().hasArg()
				.withDescription("island, embodied, alife").create("model"));

		options.addOption(OptionBuilder.withArgName("species").isRequired().hasArg()
				.withDescription(
						"species names [detector, extractor, transporter OR homogenous] e.g. detector,transporter")
				.create("species"));

		options.addOption(OptionBuilder.withArgName("int").isRequired().hasArg()
				.withDescription("interactions names [none OR trail, broadcast, unicast_n] e.g. trail,broadcast")
				.create("int"));

		options.addOption(new Option(("unconstrained"), "use unconstrainted interactions [default: constrained]"));

		options.addOption(OptionBuilder.withArgName("iquality").hasArg()
				.withDescription("interaction quality (highest, high, medium, low/poor) [highest]").create("iquality"));

		options.addOption(OptionBuilder.withArgName("gen").hasArg().withType(Integer.class)
				.withDescription("maximum generations [" + GENERATIONS + "]").create("gen"));

		options.addOption(OptionBuilder.withArgName("clones").hasArg().withType(Integer.class)
				.withDescription("clones per species [" + CLONES_PER_SPECIES + "]").create("clones"));

		options.addOption(OptionBuilder.withArgName("psize").hasArg().withType(Integer.class)
				.withDescription("gene pool size [" + GENE_POOL_SIZE + "]").create("psize"));

		options.addOption(OptionBuilder.withArgName("cdensity").hasArg().withType(Integer.class)
				.withDescription("cdensity [" + COLLECTION_SITE_DENSITY + "]").create("cdensity"));

		options.addOption(OptionBuilder.withArgName("width").hasArg().withType(Integer.class)
				.withDescription("world width [" + WORLD_WIDTH + "]").create("width"));

		options.addOption(OptionBuilder.withArgName("height").hasArg().withType(Integer.class)
				.withDescription("world height [" + WORLD_HEIGHT + "]").create("height"));

		options.addOption(OptionBuilder.withArgName("bench").hasArg().withType(Integer.class)
				.withDescription("max steps [" + BENCHMARK_GENERATION + "]").create("bench"));

		options.addOption(OptionBuilder.withArgName("msteps").hasArg().withType(Integer.class)
				.withDescription("max steps [" + MAX_STEPS_PER_AGENT + "]").create("msteps"));

		options.addOption(OptionBuilder.withArgName("odensity").hasArg().withType(Double.class)
				.withDescription("obstacle density [" + OBSTACLE_DENSITY + "]").create("odensity"));

		options.addOption(OptionBuilder.withArgName("rdensity").hasArg().withType(Double.class)
				.withDescription("resource density [" + RESOURCE_DENSITY + "]").create("rdensity"));
		
		options.addOption(OptionBuilder.withArgName("erperc").hasArg().withType(Double.class)
				.withDescription("extracted resource percent [" + EXTRACTED_RESOURCE_PERCENT + "]").create("erperc"));

		options.addOption(OptionBuilder.withArgName("goal").hasArg().withType(Double.class)
				.withDescription("%resource capture goal [" + PERC_RESOURCE_CAPTURE_GOAL + "; 0 means no goal]")
				.create("goal"));

		options.addOption(OptionBuilder.withArgName("repeat").hasArg().withType(Integer.class)
				.withDescription("repeat experiment [" + REPEAT + "]").create("repeat"));

		options.addOption(OptionBuilder.withArgName("ddir").hasArg().withDescription("data dir [" + DATA_DIR + "]")
				.create("ddir"));

		options.addOption(OptionBuilder.withArgName("job_name").hasArg().withDescription("job name [" + JOB_NAME + "]")
				.create("job_name"));

		options.addOption(OptionBuilder.withArgName("experiment").hasArg()
				.withDescription("used to run experiment blocks").create("experiment"));

		options.addOption(OptionBuilder.withArgName("preset").hasArg()
				.withDescription("(noops, actions, random) [random]").create("preset"));

		options.addOption(OptionBuilder.withArgName("ms").hasArg().withType(Double.class)
				.withDescription("mating success rate [" + MATING_SUCCESS_RATE + "]").create("ms"));

		options.addOption(OptionBuilder.withArgName("mpr").hasArg().withType(Integer.class)
				.withDescription("mating proximity radius [" + MATING_PROXIMITY_RADIUS + "]").create("mpr"));

		options.addOption(OptionBuilder.withArgName("mgf").hasArg().withType(Integer.class)
				.withDescription("mating generation frequency [" + MATING_GENERATION_FREQUENCY + "]").create("mgf"));

		options.addOption(OptionBuilder.withArgName("seed").hasArg().withType(Long.class)
				.withDescription("seed for randomizer [" + SEED + "]").create("seed"));

		HelpFormatter formatter = new HelpFormatter();

		// create the parser
		CommandLineParser parser = new BasicParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// D.p("=============== INPUT PARAMETERS ===============");

			if (line.hasOption("blocks")) {
				runBlocks = new Integer(line.getOptionValue("blocks")).intValue();

				if (runBlocks > 0) {
					D.p("going to do run " + runBlocks + " blocks...");
					return runBlocks;
				} else {
					D.p("No run blocks...direct run...");
				}
			}

			//
			// If we are here, then all pre-processing as far as run-blocks are
			// concerned have been taken care of
			//

			String argsToString = "";
			for (String anArg : args) {
				argsToString += anArg + " ";
			}
			printAndStore("ARG = " + argsToString);

			if (line.hasOption("experiment")) {
				String experimentName = line.getOptionValue("experiment");
				if (!experimentName.trim().equals(EMPTY_STRING)) {
					EXPERIMENT_NAME = experimentName;
					printAndStore("EXPERIMENT_NAME = " + EXPERIMENT_NAME);
				}

			}

			if (line.hasOption("model")) {
				String modelName = line.getOptionValue("model").toLowerCase();
				if (modelName.equalsIgnoreCase("island")) {
					EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;
				} else if (modelName.equalsIgnoreCase("embodied")) {
					EVOLUTIONARY_MODEL = EvolutionaryModel.EMBODIED_MODEL;
					if (!line.hasOption("peer_rewards")) {
						PEER_REWARDS = true;
						D.p("** NOTE: in this decentralized models PEER_REWARDS is being set to true!! Use peer_rewards option to override");
					}
				} else if (modelName.equalsIgnoreCase("alife")) {
					EVOLUTIONARY_MODEL = EvolutionaryModel.ALIFE_MODEL;
					if (!line.hasOption("peer_rewards")) {
						PEER_REWARDS = true;
						D.p("** NOTE: in this decentralized models PEER_REWARDS is being set to true!! Use peer_rewards option to override");
					}
				} else {
					throw new ParseException("model name: " + modelName + " was not recognized");
				}
			}
			printAndStore("EVOLUTIONARY_MODEL = " + EVOLUTIONARY_MODEL);

			if (line.hasOption("show_graphics")) {
				SHOW_GRAPHICS = true;
			}
			printAndStore("SHOW_GRAPHICS = " + SHOW_GRAPHICS);

			if (line.hasOption("env_diff")) {
				ENVIRONMENT = Environment.DIFFICULT;
			}

			if (line.hasOption("env_vdiff")) {
				ENVIRONMENT = Environment.VERY_DIFFICULT;
			}
			printAndStore("ENVIRONMENT = " + ENVIRONMENT);

			if (line.hasOption("no_randomization")) {
				RANDOMIZE_SIM_SEED = false;
			}
			printAndStore("RANDOMIZE_SIM_SEED = " + RANDOMIZE_SIM_SEED);

			if (line.hasOption("slr")) {
				SPECIES_LEVEL_REPORT = true;
			}
			printAndStore("SPECIES_LEVEL_REPORT = " + SPECIES_LEVEL_REPORT);

			if (line.hasOption("clustered")) {
				CLUSTERED = true;
			}
			printAndStore("CLUSTERED = " + CLUSTERED);

			if (line.hasOption("peer_rewards")) {
				if (EVOLUTIONARY_MODEL == EvolutionaryModel.ISLAND_MODEL) {
					PEER_REWARDS = false;
					throw new ParseException(
							"peer rewards are not allowed in the island model (centralized fitness evaluation)");
				} else {
					PEER_REWARDS = true;
				}
			}
			printAndStore("PEER_REWARDS = " + PEER_REWARDS);

			if (line.hasOption("use_4_tasks")) {
				PROBLEM_COMPLEXITY = ProblemComplexity.FOUR_SEQUENTIAL_TASKS;
			} else {
				PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;
			}

			printAndStore("PROBLEM_COMPLEXITY = " + PROBLEM_COMPLEXITY);

			if (line.hasOption("goal")) {
				PERC_RESOURCE_CAPTURE_GOAL = new Double(line.getOptionValue("goal")).doubleValue();
			}

			printAndStore("PERC_RESOURCE_CAPTURE_GOAL = " + PERC_RESOURCE_CAPTURE_GOAL);

			if (line.hasOption("odensity")) {
				OBSTACLE_DENSITY = new Double(line.getOptionValue("odensity")).doubleValue();
			}

			printAndStore("OBSTACLE_DENSITY = " + OBSTACLE_DENSITY);

			if (line.hasOption("rdensity")) {
				RESOURCE_DENSITY = new Double(line.getOptionValue("rdensity")).doubleValue();
			}
			printAndStore("RESOURCE_DENSITY = " + RESOURCE_DENSITY);

			if (line.hasOption("erperc")) {
				EXTRACTED_RESOURCE_PERCENT = new Double(line.getOptionValue("erperc")).doubleValue();
			}
			printAndStore("EXTRACTED_RESOURCE_PERCENT = " + EXTRACTED_RESOURCE_PERCENT);
			
			
			if (line.hasOption("ms")) {
				MATING_SUCCESS_RATE = new Double(line.getOptionValue("ms")).doubleValue();
			}
			printAndStore("MATING_FREQUENCY = " + MATING_SUCCESS_RATE);

			if (line.hasOption("mpr")) {
				MATING_PROXIMITY_RADIUS = new Integer(line.getOptionValue("mpr")).intValue();

			}
			printAndStore("MATING_PROXIMITY_RADIUS = " + MATING_PROXIMITY_RADIUS);

			if (line.hasOption("mgf")) {
				MATING_GENERATION_FREQUENCY = new Integer(line.getOptionValue("mgf")).intValue();

			}
			printAndStore("MATING_GENERATION_FREQUENCY = " + MATING_GENERATION_FREQUENCY);

			if (line.hasOption("seed")) {
				SEED = new Integer(line.getOptionValue("seed")).intValue();

			}

			if (line.hasOption("log")) {
				String logLevel = line.getOptionValue("log");
				if (logLevel.equalsIgnoreCase("all")) {
					REQUESTED_LOG_LEVEL = Level.ALL;

				} else if (logLevel.equalsIgnoreCase("off")) {
					REQUESTED_LOG_LEVEL = Level.OFF;
				}

				else if (logLevel.equalsIgnoreCase("info")) {
					REQUESTED_LOG_LEVEL = Level.INFO;
				} else {
					throw new ParseException("log level: " + logLevel + " was not recognized");
				}

			}

			printAndStore("LOG.LEVEL = " + REQUESTED_LOG_LEVEL.toString());

			if (line.hasOption("species")) {

				String speciesNamesArg = line.getOptionValue("species").toLowerCase();

				String[] speciesNamesList = speciesNamesArg.split(",");

				// go over each CSV entry and verify that they are valid
				for (String speciesNames : speciesNamesList) {
					if (!(speciesNames.contains("homogenous") || speciesNames.contains("extractor")
							|| speciesNames.contains("detector") || speciesNames.contains("transporter")
							|| speciesNames.contains("processor") || speciesNames.contains("+d")
							|| speciesNames.contains("+e") || speciesNames.contains("+t") || speciesNames.contains("+p")
							|| speciesNames.contains("^de") || speciesNames.contains("^dt")
							|| speciesNames.contains("^et") || speciesNames.contains("^dp")
							|| speciesNames.contains("^ep") || speciesNames.contains("^pt")
							|| speciesNames.contains("*det") || speciesNames.contains("*dep")
							|| speciesNames.contains("*dpt") || speciesNames.contains("*ept")
							|| speciesNames.contains("#dept"))) {
						throw new ParseException("species: " + speciesNames + " was not recognized");
					}
				}
				MODEL_SPECIES = speciesNamesArg;

			}
			printAndStore("MODEL_SPECIES = " + MODEL_SPECIES);

			if (line.hasOption("int")) {
				String interactions = line.getOptionValue("int").toLowerCase();
				if (!(interactions.contains("none") || interactions.contains("trail")
						|| interactions.contains("broadcast") || interactions.contains("unicast_n"))) {
					throw new ParseException("interactions: " + interactions + " was not recognized");
				}
				MODEL_INTERACTIONS = interactions;

			}
			printAndStore("MODEL_INTERACTIONS = " + MODEL_INTERACTIONS);

			if (line.hasOption("iquality")) {
				String quality = line.getOptionValue("iquality").toLowerCase();
				if (quality.contains("poor") || quality.contains("low")) {
					INTERACTION_QUALITY = InteractionQuality.POOR;
				} else if (quality.contains("medium")) {
					INTERACTION_QUALITY = InteractionQuality.MEDIUM;
				} else if (quality.contains("highest")) {
					INTERACTION_QUALITY = InteractionQuality.HIGHEST;
				} else if (quality.contains("high")) {
					INTERACTION_QUALITY = InteractionQuality.HIGH;
				} else {
					throw new ParseException("iquality: " + quality + " was not recognized");
				}

			}
			printAndStore("INTERACTION_QUALITY = " + INTERACTION_QUALITY);

			if (line.hasOption("unconstrained")) {

				CONSTRAINED_INTERACTIONS = false;

			}

			if (MODEL_INTERACTIONS.toLowerCase().contains("trail")) {

				CONSTRAINED_INTERACTIONS = false;
				D.p("Warning! With trail interaction, constrained interactions are not allowed, setting to false..");
			}

			printAndStore("CONSTRAINED_INTERACTIONS = " + CONSTRAINED_INTERACTIONS);

			if (line.hasOption("gen")) {
				GENERATIONS = new Integer(line.getOptionValue("gen")).intValue();

			}
			printAndStore("GENERATIONS = " + GENERATIONS);

			if (line.hasOption("preset")) {
				String seedPreset = line.getOptionValue("preset");
				if (seedPreset.equalsIgnoreCase("random")) {
					SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.RANDOM;
				}

				else if (seedPreset.equalsIgnoreCase("actions")) {
					SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.ACTIONS;
				} else if (seedPreset.equalsIgnoreCase("noops")) {
					SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.NOOPS;

				} else {
					throw new ParseException("preset: " + seedPreset + " was not recognized");
				}

			}

			printAndStore("PRESET.GENOTYPE = " + SEED_GENOTYPE_PRESET_INSTRUCTIONS);

			if (line.hasOption("psize")) {
				GENE_POOL_SIZE = new Integer(line.getOptionValue("psize")).intValue();
				EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;

			}
			printAndStore("GENE_POOL_SIZE = " + GENE_POOL_SIZE);
			printAndStore("EMBODIED_AGENT_POOL_SIZE = " + EMBODIED_AGENT_POOL_SIZE);

			if (line.hasOption("cdensity")) {
				COLLECTION_SITE_DENSITY = new Double(line.getOptionValue("cdensity")).intValue();

			}
			printAndStore("COLLECTION_SITE_DENSITY = " + COLLECTION_SITE_DENSITY);

			if (line.hasOption("width")) {
				WORLD_WIDTH = new Integer(line.getOptionValue("width")).intValue();

			}

			printAndStore("WORLD_WIDTH = " + WORLD_WIDTH);
			if (line.hasOption("height")) {
				WORLD_HEIGHT = new Integer(line.getOptionValue("height")).intValue();

			}
			printAndStore("WORLD_HEIGHT = " + WORLD_HEIGHT);

			MAX_STEPS_PER_AGENT = WORLD_WIDTH * WORLD_HEIGHT * 4;

			if (line.hasOption("msteps")) {
				MAX_STEPS_PER_AGENT = new Integer(line.getOptionValue("msteps")).intValue();

			}
			printAndStore("MAX_STEPS_PER_AGENT = " + MAX_STEPS_PER_AGENT);

			if (line.hasOption("bench")) {
				BENCHMARK_GENERATION = new Integer(line.getOptionValue("bench")).intValue();

			}
			printAndStore("BENCHMARK_GENERATION = " + BENCHMARK_GENERATION);

			if (line.hasOption("repeat")) {
				REPEAT = new Integer(line.getOptionValue("repeat")).intValue();

			}
			printAndStore("NUM_EXPERIMENTS = " + REPEAT);

			if (RANDOMIZE_SIM_SEED) {
				SEED = Settings.getSecureRandom();
			}

			// D.p("SEED = " + SEED);

			if (line.hasOption("ddir")) {
				DATA_DIR = line.getOptionValue("ddir");

				File dir = new File(DATA_DIR);
				if (dir.exists() && dir.isDirectory()) {
					// D.p(EVENT_DATA_DIR +
					// " already exists; contents might be replaced");
				} else if (dir.exists() && !dir.isDirectory()) {
					throw new ParseException(
							"A file by the name: " + DATA_DIR + " exists and needs to be removed first");
				} else {
					if (!dir.mkdir()) {
						throw new ParseException("Unable to create: " + DATA_DIR + "directory; check permissions");
					} else {
						// D.p(EVENT_DATA_DIR + " created.");
					}
				}

			}
			printAndStore("DATA_DIR = " + DATA_DIR);

			if (line.hasOption("job_name")) {
				String jobName = line.getOptionValue("job_name");
				if (!jobName.trim().equals(EMPTY_STRING)) {
					JOB_NAME = jobName;
				}

				else {
					throw new ParseException("job_name: [" + jobName + "] was not recognized");
				}

			}
			printAndStore("JOB_NAME = " + JOB_NAME);

			if (line.hasOption("de")) {
				boolean validOptions = false;
				Set<Species> parsedSpecies = parseSpeciesString(MODEL_SPECIES);

				if (EVOLUTIONARY_MODEL == EvolutionaryModel.ALIFE_MODEL
						|| EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
					if (parsedSpecies.size() == 3 && parsedSpecies.contains(Species.DETECTOR)
							&& parsedSpecies.contains(Species.EXTRACTOR)
							&& parsedSpecies.contains(Species.TRANSPORTER)) {
						DYNAMIC_EVENNESS = true;
						validOptions = true;
						CLONES_PER_SPECIES = DE_INITIAL_CLONES;
					} else if (parsedSpecies.size() == 4 && parsedSpecies.contains(Species.PROCESSOR)
							&& parsedSpecies.contains(Species.DETECTOR) && parsedSpecies.contains(Species.EXTRACTOR)
							&& parsedSpecies.contains(Species.TRANSPORTER)) {
						DYNAMIC_EVENNESS = true;
						validOptions = true;
						CLONES_PER_SPECIES = DE_INITIAL_CLONES;
					}
				}

				if (!validOptions) {

					D.p("Dynamic Evenness is only implemented for non-island models with detectors,extractors,transporters and processors");
					System.exit(1);
				}

			}
			printAndStore("DYNAMIC_EVENNESS = " + DYNAMIC_EVENNESS);

			if (DYNAMIC_EVENNESS) {
				if (line.hasOption("degofp")) {
					DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE = new Integer(line.getOptionValue("degofp"))
							.intValue();

				}
				printAndStore("DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE = "
						+ DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE);

				if (line.hasOption("demp")) {
					DE_MAX_POPULATION = new Integer(line.getOptionValue("demp")).intValue();

				}
				printAndStore("DE_MAX_POPULATION = " + DE_MAX_POPULATION);

			}

			if (line.hasOption("me")) {
				if (!DYNAMIC_EVENNESS) {
					boolean validOptions = false;
					Set<Species> parsedSpecies = parseSpeciesString(MODEL_SPECIES);

					if (EVOLUTIONARY_MODEL == EvolutionaryModel.ALIFE_MODEL
							|| EVOLUTIONARY_MODEL == EvolutionaryModel.EMBODIED_MODEL) {
						if (parsedSpecies.size() == 3 && parsedSpecies.contains(Species.DETECTOR)
								&& parsedSpecies.contains(Species.EXTRACTOR)
								&& parsedSpecies.contains(Species.TRANSPORTER)) {
							MANUAL_EVENNESS = true;
							validOptions = true;

						} else if (parsedSpecies.size() == 4 && parsedSpecies.contains(Species.PROCESSOR)
								&& parsedSpecies.contains(Species.DETECTOR) && parsedSpecies.contains(Species.EXTRACTOR)
								&& parsedSpecies.contains(Species.TRANSPORTER)) {
							MANUAL_EVENNESS = true;
							validOptions = true;

						}
					}

					if (!validOptions) {
						D.p("Mynamic Evenness is only implemented for non-island models with detectors,extractors,transporters and processors");
						System.exit(1);
					}

				} else {
					D.p("Manual Evenness can only be used when not using Dynamic Evenness...exiting!");
					System.exit(1);
				}
			}

			printAndStore("MANUAL_EVENNESS = " + MANUAL_EVENNESS);

			if (MANUAL_EVENNESS) {
				if (line.hasOption("ranpr")) {
					ME_RANDOM_POP_RATIO = true;
				}
				printAndStore("ME_RANDOM_POP_RATIO = " + ME_RANDOM_POP_RATIO);

				if (line.hasOption("manpr")) {
					if (ME_RANDOM_POP_RATIO) {
						D.p("Cannot use both ranpr and manpr picky only one...exiting!");
						System.exit(1);
					}
					ME_POP_RATIO = line.getOptionValue("manpr");
					
					// if the string is surrounded by 's, then take them out
					if(ME_POP_RATIO.startsWith("'") && ME_POP_RATIO.endsWith("'")) {
						ME_POP_RATIO = ME_POP_RATIO.substring(1, ME_POP_RATIO.length()-1);
					}
					printAndStore("ME_POP_RATIO = " + ME_POP_RATIO);
				} else {
					if (!ME_RANDOM_POP_RATIO) {
						D.p("Neither ranpr nor manpr used...exiting!");
						System.exit(1);
					}
				}

				if (line.hasOption("memp")) {
					ME_MAX_POPULATION = new Integer(line.getOptionValue("memp")).intValue();
				}
				printAndStore("ME_MAX_POPULATION = " + ME_MAX_POPULATION);
			}

			if (line.hasOption("clones")) {

				CLONES_PER_SPECIES = new Integer(line.getOptionValue("clones")).intValue();

			}
			printAndStore("CLONES_PER_SPECIES = " + CLONES_PER_SPECIES);

			// some calculated values
			if (ENVIRONMENT == Environment.DIFFICULT || ENVIRONMENT == Environment.VERY_DIFFICULT) {
				PRIMARY_COLLECTION_SITE_X = 0;
				PRIMARY_COLLECTION_SITE_Y = 0;
			} else {
				PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.50);
				PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.50);
			}
			SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

			printAndStore("PRIMARY_COLLECTION_SITE_X = " + PRIMARY_COLLECTION_SITE_X);
			printAndStore("PRIMARY_COLLECTION_SITE_Y = " + PRIMARY_COLLECTION_SITE_Y);
			printAndStore("MAX_SIMS_PER_EXPERIMENT = " + SIMS_PER_EXPERIMENT);

			WORLD_GRIDS = WORLD_WIDTH * WORLD_HEIGHT;

			int numberOfObstacles = (int) (WORLD_GRIDS * OBSTACLE_DENSITY);
			int numberOfResources = (int) (WORLD_GRIDS * RESOURCE_DENSITY);
			_ACTUAL_RESOURCES = numberOfResources;
			_ACTUAL_OBSTACLES = numberOfObstacles;

			_RESOURCE_BOX_WIDTH = (int) Math.ceil(Math.sqrt((double) numberOfResources));
			_RESOURCE_BOX_WIDTH_PADDING = _RESOURCE_BOX_WIDTH + 2;
			_RESOURCE_BOX_WIDTH_HALF1 = _RESOURCE_BOX_WIDTH / 2;
			_RESOURCE_BOX_WIDTH_HALF2 = _RESOURCE_BOX_WIDTH_HALF1 + 1;

			if (PERC_RESOURCE_CAPTURE_GOAL == 0.0) {
				PERC_RESOURCE_CAPTURE_GOAL = Integer.MAX_VALUE;
			}
			int resourceCaptureGoal = (int) ((double) numberOfResources * PERC_RESOURCE_CAPTURE_GOAL);

			NUMBER_OF_COLLECTION_SITES = (int) ((double) WORLD_GRIDS * COLLECTION_SITE_DENSITY);

			// these computations will help make difficult environment generator
			// work faster by pre-computing these values
			// extra numbers account for the obstruction walls
			_RESOURCE_BOX_LEFT = (WORLD_WIDTH - (_RESOURCE_BOX_WIDTH + 2)) / 2;
			_RESOURCE_BOX_RIGHT = _RESOURCE_BOX_LEFT + _RESOURCE_BOX_WIDTH + 1;
			_RESOURCE_BOX_UP = (WORLD_HEIGHT - (_RESOURCE_BOX_WIDTH + 2)) / 2;
			_RESOURCE_BOX_DOWN = _RESOURCE_BOX_UP + _RESOURCE_BOX_WIDTH + 1;

			_RESOURCE_BOX_LEFT_PERIM = _RESOURCE_BOX_LEFT - 1;
			_RESOURCE_BOX_RIGHT_PERIM = _RESOURCE_BOX_RIGHT + 1;
			_RESOURCE_BOX_UP_PERIM = _RESOURCE_BOX_UP - 1;
			_RESOURCE_BOX_DOWN_PERIM = _RESOURCE_BOX_DOWN + 1;

			_WIDTH_EDGE = WORLD_WIDTH - 1;
			_HEIGHT_EDGE = WORLD_HEIGHT - 1;

			printAndStore("ACTUAL_OBSTACLES = " + numberOfObstacles);
			printAndStore("ACTUAL_RESOURCES = " + numberOfResources);

			printAndStore("ACTUAL RESOURCE_CAPTURE_GOAL = " + resourceCaptureGoal);
			printAndStore("ACTUAL COLLECTION_SITES = " + NUMBER_OF_COLLECTION_SITES);

			// D.p("=================================================");

		} catch (ParseException exp) {
			// oops, something went wrong
			if (args.length == 1 && args[0].toLowerCase().contains("help")) {
				// check if this was a request for help...
				formatter.printHelp("com.synthverse.Main", options);
				System.exit(0);
			} else {
				System.err.println("Parsing failed.  Reason: " + exp.getMessage());
				// formatter.printHelp("com.synthverse.Main", options);
				System.exit(1);
			}
		}
		return 0;

	}

	public static SecureRandom secureRandomDevice = new SecureRandom();

	public final static int getSecureRandom() {

		byte[] sbuf = secureRandomDevice.generateSeed(32);
		ByteBuffer bb = ByteBuffer.wrap(sbuf);
		int secureRandom = bb.getInt();
		secureRandom += (int) System.nanoTime();

		// this ensures always +ve numbers
		if (secureRandom < 0) {
			secureRandom = -secureRandom;
		}
		return secureRandom;
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}

		return instance;

	}

	public static void reset() {
		if (instance != null) {
			instance.resetSettings();
		}

	}

	public void resetSettings() {
		SHOW_GRAPHICS = false;

		CLUSTERED = false;

		PEER_REWARDS = false;

		SEED = 1;

		BENCHMARK_GENERATION = 100;

		CLONES_PER_SPECIES = 8;

		GENE_POOL_SIZE = 512;

		COLLECTION_SITE_DENSITY = 0.02;

		NUMBER_OF_COLLECTION_SITES = 8;

		PERC_RESOURCE_CAPTURE_GOAL = 0.0;

		SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

		PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;

		EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;

		MODEL_SPECIES = "homogenous";

		MODEL_INTERACTIONS = "none";

		WORLD_WIDTH = 15;

		WORLD_HEIGHT = 15;

		WORLD_GRIDS = WORLD_WIDTH * WORLD_HEIGHT;

		OBSTACLE_DENSITY = 0.125;

		RESOURCE_DENSITY = 0.05;

		MATING_SUCCESS_RATE = 0.3;

		MATING_PROXIMITY_RADIUS = 1;

		MATING_GENERATION_FREQUENCY = 10;

		PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.50);

		PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.50);

		REQUESTED_LOG_LEVEL = Level.ALL;

		SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.RANDOM;

		EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;

		MAX_STEPS_PER_AGENT = 1024;

		REPEAT = 1;

		DATA_DIR = File.separator + "tmp";

		JOB_NAME = "test";

		RANDOMIZE_SIM_SEED = true;

		PERFORMANCE_DATA_FILE = "perf_dat.csv";

		EVENT_DATA_FILE = "evnt_dat.csv";

		DNA_PROGRESSION_FILE = "dna_dat.gz";

		EXPERIMENT_DETAILS_FILE = "exp_det.txt";
		
		REPORT_EXPERIMENT_DETAILS = false;

		EXPERIMENT_DETAILS_FILE_MAIN = "exp_det.txt";

		EXPERIMENT_DETAILS = new ArrayList<String>();

		INTERACTION_QUALITY = InteractionQuality.HIGHEST;

		CONSTRAINED_INTERACTIONS = true;

		REPORT_DNA_PROGRESSION = false;

		COMPRESS_DNA_PROGRESSION = false;
		
		REDUCED_LOGGING = true;

		SPECIES_LEVEL_REPORT = false;

		DYNAMIC_EVENNESS = false;

		MANUAL_EVENNESS = false;

		ME_RANDOM_POP_RATIO = false;

		ME_POP_RATIO = "1:1:1:1"; // d:e:t:p default ratio

		DE_INITIAL_CLONES = 4;

		DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE = 50;

		DE_GENERATIONS_TO_OBSERVE_SIGNAL_CHANGES = 5;

		DE_MAX_POPULATION = 24;

		ME_MAX_POPULATION = 24;

		DE_ALGORITHM = DynamicEvennessAlgorithm.DE_SIGNAL_DEMAND_BASED_SWITCH;

		ENVIRONMENT = Environment.RANDOM;

		HOLD_EVENT_THRESHOLD = 1;

		lastReportedCaptures = 0;
		lastReportedGeneration = 0;
		lastLoggedGeneration = 0;

		experimentNumber = 0;

		statusCache = EMPTY_STRING;

		generationCounter = 0;

		originalArgs = null;

		__showGraphics = false;
		__useSoundEffects = false;
		__guiStarted = false;

		__renderStageLock = new Integer(0);
		__simulationStarted = false;
		__bridgeState = null;

		__animationDelay = Constants.SHORT_PAUSE;

		__numDetectors = 0;
		__numExtractors = 0;
		__numTransporters = 0;
		__numProcessors = 0;

		_RESOURCE_BOX_WIDTH = 0;
		_RESOURCE_BOX_WIDTH_PADDING = 0;

		_RESOURCE_BOX_WIDTH_HALF1 = 0;
		_RESOURCE_BOX_WIDTH_HALF2 = 0;

		_ACTUAL_RESOURCES = 0;
		_ACTUAL_OBSTACLES = 0;

		_RESOURCE_BOX_LEFT = 0;
		_RESOURCE_BOX_RIGHT = 0;
		_RESOURCE_BOX_UP = 0;
		_RESOURCE_BOX_DOWN = 0;

		_RESOURCE_BOX_LEFT_PERIM = 0;
		_RESOURCE_BOX_RIGHT_PERIM = 0;

		_RESOURCE_BOX_UP_PERIM = 0;
		_RESOURCE_BOX_DOWN_PERIM = 0;

		_WIDTH_EDGE = 0;
		_HEIGHT_EDGE = 0;

	}

	public Set<Species> parseSpeciesString(String speciesString) {

		Set<Species> speciesSet = new TreeSet<Species>(new SpeciesComparator());

		if (speciesString.contains("+d")) {
			speciesSet.add(Species.D);

		}

		if (speciesString.contains("+p")) {
			speciesSet.add(Species.P);

		}

		if (speciesString.contains("+e")) {
			speciesSet.add(Species.E);

		}

		if (speciesString.contains("+t")) {
			speciesSet.add(Species.T);

		}

		if (speciesString.contains("^de")) {
			speciesSet.add(Species.DE);

		}

		if (speciesString.contains("^dt")) {
			speciesSet.add(Species.DT);

		}

		if (speciesString.contains("^et")) {
			speciesSet.add(Species.ET);

		}

		if (speciesString.contains("^dp")) {
			speciesSet.add(Species.DP);
		}

		if (speciesString.contains("^ep")) {
			speciesSet.add(Species.EP);
		}

		if (speciesString.contains("^pt")) {
			speciesSet.add(Species.PT);
		}

		if (speciesString.contains("*det")) {
			speciesSet.add(Species.DET);

		}

		if (speciesString.contains("*dep")) {
			speciesSet.add(Species.DEP);

		}

		if (speciesString.contains("*dpt")) {
			speciesSet.add(Species.DPT);

		}

		if (speciesString.contains("*ept")) {
			speciesSet.add(Species.EPT);

		}

		if (speciesString.contains("#dept")) {
			speciesSet.add(Species.DEPT);

		}

		if (speciesString.contains("detector")) {
			speciesSet.add(Species.DETECTOR);

		}

		if (speciesString.contains("extractor")) {
			speciesSet.add(Species.EXTRACTOR);

		}

		if (speciesString.contains("transporter")) {
			speciesSet.add(Species.TRANSPORTER);

		}

		if (speciesString.contains("processor")) {
			speciesSet.add(Species.PROCESSOR);

		}

		if (speciesString.contains("homogenous")) {
			speciesSet.add(Species.HOMOGENOUS);

		}

		return speciesSet;

	}

}
