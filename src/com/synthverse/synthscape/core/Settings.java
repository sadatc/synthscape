package com.synthverse.synthscape.core;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Settings implements Constants {

	public enum SeedType {
		NOOPS, ACTIONS, RANDOM;
	}

	private static Settings instance = null; 

	public boolean SHOW_GRAPHICS = false;

	public boolean CLUSTERED = false;

	public boolean PEER_REWARDS = false;

	public long SEED = 1;

	public int GENERATIONS = 1000;

	public int BENCHMARK_GENERATION = 100;

	public int CLONES_PER_SPECIES = 4;

	public int GENE_POOL_SIZE = 512;

	public double COLLECTION_SITE_DENSITY = 0.015625;
	// public double COLLECTION_SITE_DENSITY = 0.031250;

	public int NUMBER_OF_COLLECTION_SITES = 8;

	public double PERC_RESOURCE_CAPTURE_GOAL = 0.0;

	public int SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

	public ProblemComplexity PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;

	public EvolutionaryModel EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;

	public String MODEL_SPECIES = "homogenous";

	public String MODEL_INTERACTIONS = "none";

	public int WORLD_WIDTH = 16;

	public int WORLD_HEIGHT = 16;

	public double OBSTACLE_DENSITY = 0.125;

	public double RESOURCE_DENSITY = 0.0625;

	public double MATING_SUCCESS_RATE = 0.3;

	public double MATING_PROXIMITY_RADIUS = 1;

	public int MATING_GENERATION_FREQUENCY = 10;

	public int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.50);

	public int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.50);

	public Level REQUESTED_LOG_LEVEL = Level.ALL;

	public SeedType SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.RANDOM;

	public int EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;
	// TODO: separate this, if needed

	public int MAX_STEPS_PER_AGENT = WORLD_WIDTH * WORLD_HEIGHT * 4;

	public int REPEAT = 1;

	public String DATA_DIR = File.separator + "tmp";

	public String JOB_NAME = "test";

	public long JOB_SET = 0;

	public String PERFORMANCE_DATA_FILE = "perf_dat.csv";

	public String EVENT_DATA_FILE = "evnt_dat.csv";

	public String DNA_PROGRESSION_FILE = "dna_dat.gz";

	public String EXPERIMENT_DETAILS_FILE = "exp_det.txt";

	public String EXPERIMENT_DETAILS_FILE_MAIN = "exp_det.txt";

	public List<String> EXPERIMENT_DETAILS = new ArrayList<String>();

	public InteractionQuality INTERACTION_QUALITY = InteractionQuality.HIGHEST;

	public boolean CONSTRAINED_INTERACTIONS = true;

	public boolean REPORT_DNA_PROGRESSION = false;

	public boolean COMPRESS_DNA_PROGRESSION = false;

	public int lastReportedCaptures = 0;
	public int lastReportedGeneration = 0;
	public int lastLoggedGeneration = 0;

	public int experimentNumber = 0;

	public String statusCache = EMPTY_STRING;

	public int generationCounter = 0;

	private Settings() {

	}

	private void printAndStore(String msg) {
		D.p(msg);
		EXPERIMENT_DETAILS.add(msg);
	}

	@SuppressWarnings("static-access")
	public void processCommandLineInput(String[] args) {
		// build up all the command line options
		Options options = new Options();

		options.addOption(new Option("help", "print this message"));

		options.addOption(new Option("random_seed",
				"always start with random seed"));

		options.addOption(new Option("no_randomization",
				"do not randomize each sim [default: randomize]"));

		options.addOption(new Option("show_graphics",
				"show graphics [default: don't show graphics]"));

		options.addOption(new Option("clustered",
				"start agent clustered [default: distributed]"));

		options.addOption(new Option("peer_rewards",
				"peer rewards [default: no peer rewards]"));

		options.addOption(new Option("use_4_tasks",
				"use 4 tasks, instead of the default 3"));

		options.addOption(OptionBuilder.withArgName("log").hasArg()
				.withDescription("(off,all,info) [all]").create("log"));

		options.addOption(OptionBuilder.withArgName("model").isRequired()
				.hasArg().withDescription("island, embodied, alife")
				.create("model"));

		options.addOption(OptionBuilder
				.withArgName("species")
				.isRequired()
				.hasArg()
				.withDescription(
						"species names [detector, extractor, transporter OR homogenous] e.g. detector,transporter")
				.create("species"));

		options.addOption(OptionBuilder
				.withArgName("int")
				.isRequired()
				.hasArg()
				.withDescription(
						"interactions names [none OR trail, broadcast, unicast_n] e.g. trail,broadcast")
				.create("int"));

		options.addOption(new Option(("unconstrained"),
				"use unconstrainted interactions [default: constrained]"));

		options.addOption(OptionBuilder
				.withArgName("iquality")
				.hasArg()
				.withDescription(
						"interaction quality (highest, high, medium, low/poor) [highest]")
				.create("iquality"));

		options.addOption(OptionBuilder.withArgName("gen").hasArg()
				.withType(Integer.class)
				.withDescription("maximum generations [" + GENERATIONS + "]")
				.create("gen"));

		options.addOption(OptionBuilder
				.withArgName("clones")
				.hasArg()
				.withType(Integer.class)
				.withDescription(
						"clones per species [" + CLONES_PER_SPECIES + "]")
				.create("clones"));

		options.addOption(OptionBuilder.withArgName("psize").hasArg()
				.withType(Integer.class)
				.withDescription("gene pool size [" + GENE_POOL_SIZE + "]")
				.create("psize"));

		options.addOption(OptionBuilder
				.withArgName("cdensity")
				.hasArg()
				.withType(Integer.class)
				.withDescription(
						"cdensity [" + COLLECTION_SITE_DENSITY
								+ "]").create("cdensity"));

		options.addOption(OptionBuilder.withArgName("width").hasArg()
				.withType(Integer.class)
				.withDescription("world width [" + WORLD_WIDTH + "]")
				.create("width"));

		options.addOption(OptionBuilder.withArgName("height").hasArg()
				.withType(Integer.class)
				.withDescription("world height [" + WORLD_HEIGHT + "]")
				.create("height"));

		options.addOption(OptionBuilder.withArgName("bench")
				.hasArg().withType(Integer.class)
				.withDescription("max steps [" + BENCHMARK_GENERATION + "]")
				.create("bench"));

		options.addOption(OptionBuilder.withArgName("msteps").hasArg()
				.withType(Integer.class)
				.withDescription("max steps [" + MAX_STEPS_PER_AGENT + "]")
				.create("msteps"));

		options.addOption(OptionBuilder.withArgName("odensity").hasArg()
				.withType(Double.class)
				.withDescription("obstacle density [" + OBSTACLE_DENSITY + "]")
				.create("odensity"));

		options.addOption(OptionBuilder.withArgName("rdensity").hasArg()
				.withType(Double.class)
				.withDescription("resource density [" + RESOURCE_DENSITY + "]")
				.create("rdensity"));

		options.addOption(OptionBuilder
				.withArgName("goal")
				.hasArg()
				.withType(Double.class)
				.withDescription(
						"%resource capture goal [" + PERC_RESOURCE_CAPTURE_GOAL
								+ "; 0 means no goal]").create("goal"));

		options.addOption(OptionBuilder.withArgName("repeat").hasArg()
				.withType(Integer.class)
				.withDescription("repeat experiment [" + REPEAT + "]")
				.create("repeat"));

		options.addOption(OptionBuilder.withArgName("ddir").hasArg()
				.withDescription("data dir [" + DATA_DIR + "]")
				.create("ddir"));

		options.addOption(OptionBuilder.withArgName("job_name").hasArg()
				.withDescription("job name [" + JOB_NAME + "]")
				.create("job_name"));

		options.addOption(OptionBuilder.withArgName("job_set").hasArg()
				.withDescription("job set [" + JOB_SET + "]").create("job_set"));

		options.addOption(OptionBuilder.withArgName("preset").hasArg()
				.withDescription("(noops, actions, random) [random]")
				.create("preset"));

		options.addOption(OptionBuilder
				.withArgName("ms")
				.hasArg()
				.withType(Double.class)
				.withDescription(
						"mating success rate [" + MATING_SUCCESS_RATE + "]")
				.create("ms"));

		options.addOption(OptionBuilder
				.withArgName("mpr")
				.hasArg()
				.withType(Integer.class)
				.withDescription(
						"mating proximity radius [" + MATING_PROXIMITY_RADIUS
								+ "]").create("mpr"));

		options.addOption(OptionBuilder
				.withArgName("mgf")
				.hasArg()
				.withType(Integer.class)
				.withDescription(
						"mating generation frequency ["
								+ MATING_GENERATION_FREQUENCY + "]")
				.create("mgf"));

		options.addOption(OptionBuilder.withArgName("seed").hasArg()
				.withType(Long.class)
				.withDescription("seed for randomizer [" + SEED + "]")
				.create("seed"));

		HelpFormatter formatter = new HelpFormatter();

		// create the parser
		CommandLineParser parser = new BasicParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			D.p("=============== INPUT PARAMETERS ===============");

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
					throw new ParseException("model name: " + modelName
							+ " was not recognized");
				}
			}
			printAndStore("EVOLUTIONARY_MODEL = " + EVOLUTIONARY_MODEL);

			if (line.hasOption("show_graphics")) {
				SHOW_GRAPHICS = true;
			}
			printAndStore("SHOW_GRAPHICS = " + SHOW_GRAPHICS);

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
				PERC_RESOURCE_CAPTURE_GOAL = new Double(
						line.getOptionValue("goal")).doubleValue();
			}

			printAndStore("PERC_RESOURCE_CAPTURE_GOAL = "
					+ PERC_RESOURCE_CAPTURE_GOAL);

			if (line.hasOption("odensity")) {
				OBSTACLE_DENSITY = new Double(
						line.getOptionValue("odensity")).doubleValue();
			}

			printAndStore("OBSTACLE_DENSITY = " + OBSTACLE_DENSITY);

			if (line.hasOption("rdensity")) {
				RESOURCE_DENSITY = new Double(line.getOptionValue("rdensity"))
						.doubleValue();
			}
			printAndStore("RESOURCE_DENSITY = " + RESOURCE_DENSITY);

			if (line.hasOption("ms")) {
				MATING_SUCCESS_RATE = new Double(
						line.getOptionValue("ms")).doubleValue();
			}
			printAndStore("MATING_FREQUENCY = " + MATING_SUCCESS_RATE);

			if (line.hasOption("mpr")) {
				MATING_PROXIMITY_RADIUS = new Integer(
						line.getOptionValue("mpr")).intValue();

			}
			printAndStore("MATING_PROXIMITY_RADIUS = "
					+ MATING_PROXIMITY_RADIUS);

			if (line.hasOption("mgf")) {
				MATING_GENERATION_FREQUENCY = new Integer(
						line.getOptionValue("mgf")).intValue();

			}
			printAndStore("MATING_GENERATION_FREQUENCY = "
					+ MATING_GENERATION_FREQUENCY);

			if (line.hasOption("seed")) { 
				SEED = new Integer(line.getOptionValue("seed")).longValue();

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
					throw new ParseException("log level: " + logLevel
							+ " was not recognized");
				}

			}

			printAndStore("LOG.LEVEL = " + REQUESTED_LOG_LEVEL.toString());

			if (line.hasOption("species")) {

				String speciesNames = line.getOptionValue("species")
						.toLowerCase();
				if (!(speciesNames.contains("homogenous")
						|| speciesNames.contains("extractor")
						|| speciesNames.contains("detector") || speciesNames
							.contains("transporter"))) {
					throw new ParseException("species: " + speciesNames
							+ " was not recognized");
				}
				MODEL_SPECIES = speciesNames;

			}
			printAndStore("MODEL_SPECIES = " + MODEL_SPECIES);

			if (line.hasOption("int")) {
				String interactions = line.getOptionValue("int")
						.toLowerCase();
				if (!(interactions.contains("none")
						|| interactions.contains("trail")
						|| interactions.contains("broadcast") || interactions
							.contains("unicast_n"))) {
					throw new ParseException("interactions: " + interactions
							+ " was not recognized");
				}
				MODEL_INTERACTIONS = interactions;

			}
			printAndStore("MODEL_INTERACTIONS = " + MODEL_INTERACTIONS);

			if (line.hasOption("iquality")) {
				String quality = line.getOptionValue("iquality")
						.toLowerCase();
				if (quality.contains("poor") || quality.contains("low")) {
					INTERACTION_QUALITY = InteractionQuality.POOR;
				} else if (quality.contains("medium")) {
					INTERACTION_QUALITY = InteractionQuality.MEDIUM;
				} else if (quality.contains("highest")) {
					INTERACTION_QUALITY = InteractionQuality.HIGHEST;
				} else if (quality.contains("high")) {
					INTERACTION_QUALITY = InteractionQuality.HIGH;
				} else {
					throw new ParseException("iquality: " + quality
							+ " was not recognized");
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

			printAndStore("CONSTRAINED_INTERACTIONS = "
					+ CONSTRAINED_INTERACTIONS);

			if (line.hasOption("gen")) {
				GENERATIONS = new Integer(line.getOptionValue("gen"))
						.intValue();

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
					throw new ParseException("preset: " + seedPreset
							+ " was not recognized");
				}

			}

			printAndStore("PRESET.GENOTYPE = "
					+ SEED_GENOTYPE_PRESET_INSTRUCTIONS);

			if (line.hasOption("clones")) {
				CLONES_PER_SPECIES = new Integer(line.getOptionValue("clones"))
						.intValue();

			}
			printAndStore("CLONES_PER_SPECIES = " + CLONES_PER_SPECIES);

			if (line.hasOption("psize")) {
				GENE_POOL_SIZE = new Integer(line.getOptionValue("psize"))
						.intValue();
				EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;

			}
			printAndStore("GENE_POOL_SIZE = " + GENE_POOL_SIZE);
			printAndStore("EMBODIED_AGENT_POOL_SIZE = "
					+ EMBODIED_AGENT_POOL_SIZE);

			if (line.hasOption("cdensity")) {
				COLLECTION_SITE_DENSITY = new Double(
						line.getOptionValue("cdensity"))
						.intValue();

			}
			printAndStore("COLLECTION_SITE_DENSITY = "
					+ COLLECTION_SITE_DENSITY);

			if (line.hasOption("width")) {
				WORLD_WIDTH = new Integer(line.getOptionValue("width"))
						.intValue();

			}

			printAndStore("WORLD_WIDTH = " + WORLD_WIDTH);
			if (line.hasOption("height")) {
				WORLD_HEIGHT = new Integer(line.getOptionValue("height"))
						.intValue();

			}
			printAndStore("WORLD_HEIGHT = " + WORLD_HEIGHT);

			MAX_STEPS_PER_AGENT = WORLD_WIDTH * WORLD_HEIGHT * 4;

			if (line.hasOption("msteps")) {
				MAX_STEPS_PER_AGENT = new Integer(
						line.getOptionValue("msteps")).intValue();

			}
			printAndStore("MAX_STEPS_PER_AGENT = " + MAX_STEPS_PER_AGENT);

			if (line.hasOption("bench")) {
				BENCHMARK_GENERATION = new Integer(
						line.getOptionValue("bench")).intValue();

			}
			printAndStore("BENCHMARK_GENERATION = " + BENCHMARK_GENERATION);

			if (line.hasOption("repeat")) {
				REPEAT = new Integer(line.getOptionValue("repeat")).intValue();

			}
			printAndStore("NUM_EXPERIMENTS = " + REPEAT);

			if (line.hasOption("job_set")) {
				JOB_SET = new Integer(line.getOptionValue("job_set"))
						.longValue();

				SEED = (JOB_SET * REPEAT) + SEED;
			}
			printAndStore("JOB_SET = " + JOB_SET);

			if (line.hasOption("random_seed")) {
				SecureRandom sec = new SecureRandom();
				byte[] sbuf = sec.generateSeed(8);
				ByteBuffer bb = ByteBuffer.wrap(sbuf);
				long secureRandom = bb.getLong();
				secureRandom += System.nanoTime();
				if (line.hasOption("job_set")) {
					SEED = (JOB_SET * REPEAT) + SEED + secureRandom;
				} else {
					SEED = secureRandom;
				}
				if (SEED < 0) {
					SEED = -SEED;
				}
			}

			D.p("SEED = " + SEED);

			if (line.hasOption("ddir")) {
				DATA_DIR = line.getOptionValue("ddir");

				File dir = new File(DATA_DIR);
				if (dir.exists() && dir.isDirectory()) {
					// D.p(EVENT_DATA_DIR +
					// " already exists; contents might be replaced");
				} else if (dir.exists() && !dir.isDirectory()) {
					throw new ParseException("A file by the name: " + DATA_DIR
							+ " exists and needs to be removed first");
				} else {
					if (!dir.mkdir()) {
						throw new ParseException("Unable to create: "
								+ DATA_DIR + "directory; check permissions");
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
					throw new ParseException("job_name: [" + jobName
							+ "] was not recognized");
				}

			}
			printAndStore("JOB_NAME = " + JOB_NAME);

			// some calculated values
			PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.50);
			PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.50);
			SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

			printAndStore("PRIMARY_COLLECTION_SITE_X = "
					+ PRIMARY_COLLECTION_SITE_X);
			printAndStore("PRIMARY_COLLECTION_SITE_Y = "
					+ PRIMARY_COLLECTION_SITE_Y);
			printAndStore("MAX_SIMS_PER_EXPERIMENT = " + SIMS_PER_EXPERIMENT);

			double gridArea = WORLD_WIDTH * WORLD_HEIGHT;

			int numberOfObstacles = (int) (gridArea * OBSTACLE_DENSITY);
			int numberOfResources = (int) (gridArea * RESOURCE_DENSITY);

			if (PERC_RESOURCE_CAPTURE_GOAL == 0.0) {
				PERC_RESOURCE_CAPTURE_GOAL = Integer.MAX_VALUE;
			}
			int resourceCaptureGoal = (int) ((double) numberOfResources * PERC_RESOURCE_CAPTURE_GOAL);

			NUMBER_OF_COLLECTION_SITES = (int) ((double) gridArea * COLLECTION_SITE_DENSITY);

			printAndStore("ACTUAL_OBSTACLES = " + numberOfObstacles);
			printAndStore("ACTUAL_RESOURCES = " + numberOfResources);

			printAndStore("ACTUAL RESOURCE_CAPTURE_GOAL = "
					+ resourceCaptureGoal);
			printAndStore("ACTUAL COLLECTION_SITES = "
					+ NUMBER_OF_COLLECTION_SITES);

			D.p("=================================================");

		} catch (ParseException exp) {
			// oops, something went wrong
			if (args.length == 1 && args[0].toLowerCase().contains("help")) {
				// check if this was a request for help...
				formatter.printHelp("com.synthverse.Main", options);
				System.exit(0);
			} else {
				System.err.println("Parsing failed.  Reason: "
						+ exp.getMessage());
				// formatter.printHelp("com.synthverse.Main", options);
				System.exit(1);
			}
		}

	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}

		return instance;

	}

}
