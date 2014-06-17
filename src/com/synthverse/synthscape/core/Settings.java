package com.synthverse.synthscape.core;

import java.io.File;
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

public class Settings {

    private static Settings instance = null;

    public boolean RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = true;

    public boolean SHOW_GRAPHICS = false;

    public boolean PEER_REWARDS = false;

    public int GENERATIONS = 500000;

    public int CLONES_PER_SPECIES = 10;

    public int GENE_POOL_SIZE = 2000;

    public int NUMBER_OF_COLLECTION_SITES = 5;

    public double RESOURCE_CAPTURE_GOAL = 1.0;

    public int SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

    public ProblemComplexity PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;

    public EvolutionaryModel EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;

    public String MODEL_SPECIES = "hetero";

    public String MODEL_INTERACTIONS = "none";

    public int WORLD_WIDTH = 16;

    public int WORLD_HEIGHT = 16;

    public double OBSTACLE_DENSITY = 0.05;

    public double RESOURCE_DENSITY = 0.06;

    public double MATING_SUCCESS_RATE = 0.50;

    public double MATING_PROXIMITY_RADIUS = 2;

    public int MATING_GENERATION_FREQUENCY = 10;

    public int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);

    public int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);

    public Level REQUESTED_LOG_LEVEL = Level.ALL;

    public boolean SEED_GENOTYPE_PRESET_INSTRUCTIONS = false;

    public int EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;
    // TODO: separate this, if needed

    public int MAX_STEPS_PER_AGENT = WORLD_WIDTH * WORLD_HEIGHT * 4;

    public int REPEAT = 1;

    public String EVENT_DATA_DIR = "/tmp";

    public String EVENT_DATA_FILE = EVENT_DATA_DIR + "/event_data.csv";

    public String EXPERIMENT_DETAILS_FILE = EVENT_DATA_DIR + "/experiment_details.txt";

    public List<String> EXPERIMENT_DETAILS = new ArrayList<String>();

    public int lastReportedCaptures = 0;
    public int lastReportedGeneration = 0;
    public int lastLoggedGeneration = 0;

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
	options.addOption(new Option("no_randomization", "do not randomize each sim [default: randomize]"));
	options.addOption(new Option("show_graphics", "show graphics [default: don't show graphics]"));
	options.addOption(new Option("peer_rewards", "peer rewards [default: no peer rewards]"));

	options.addOption(new Option("use_4_tasks", "use 4 tasks, instead of the default 3"));

	options.addOption(OptionBuilder.withArgName("log").hasArg().withDescription("(off,all,info) [all]")
		.create("log"));

	options.addOption(OptionBuilder.withArgName("model").isRequired().hasArg()
		.withDescription("island, embodied, alife").create("model"));

	options.addOption(OptionBuilder
		.withArgName("species")
		.isRequired()
		.hasArg()
		.withDescription("species names [detector, extractor, transporter OR hetero] e.g. detector,transporter")
		.create("species"));

	options.addOption(OptionBuilder
		.withArgName("interactions")
		.isRequired()
		.hasArg()
		.withDescription(
			"interactions names [none OR trail, broadcast, unicast_n,unicast_g] e.g. trail,broadcast")
		.create("interactions"));

	options.addOption(OptionBuilder.withArgName("generations").hasArg().withType(Integer.class)
		.withDescription("maximum generations [" + GENERATIONS + "]").create("generations"));

	options.addOption(OptionBuilder.withArgName("clones").hasArg().withType(Integer.class)
		.withDescription("clones per species [" + CLONES_PER_SPECIES + "]").create("clones"));

	options.addOption(OptionBuilder.withArgName("pool_size").hasArg().withType(Integer.class)
		.withDescription("gene pool size [" + GENE_POOL_SIZE + "]").create("pool_size"));

	options.addOption(OptionBuilder.withArgName("collection_sites").hasArg().withType(Integer.class)
		.withDescription("number of collection sites [" + NUMBER_OF_COLLECTION_SITES + "]")
		.create("collection_sites"));

	options.addOption(OptionBuilder.withArgName("width").hasArg().withType(Integer.class)
		.withDescription("world width [" + WORLD_WIDTH + "]").create("width"));

	options.addOption(OptionBuilder.withArgName("height").hasArg().withType(Integer.class)
		.withDescription("world height [" + WORLD_HEIGHT + "]").create("height"));

	options.addOption(OptionBuilder.withArgName("max_steps").hasArg().withType(Integer.class)
		.withDescription("max steps [" + WORLD_HEIGHT + "]").create("max_steps"));

	options.addOption(OptionBuilder.withArgName("obstacle_density").hasArg().withType(Double.class)
		.withDescription("obstacle density [" + OBSTACLE_DENSITY + "]").create("obstacle_density"));

	options.addOption(OptionBuilder.withArgName("resource_density").hasArg().withType(Double.class)
		.withDescription("resource density [" + RESOURCE_DENSITY + "]").create("resource_density"));

	options.addOption(OptionBuilder.withArgName("goal").hasArg().withType(Double.class)
		.withDescription("resource capture goal [" + RESOURCE_CAPTURE_GOAL + "]").create("goal"));

	options.addOption(OptionBuilder.withArgName("repeat").hasArg().withType(Integer.class)
		.withDescription("repeat experiment [" + REPEAT + "]").create("repeat"));

	options.addOption(OptionBuilder.withArgName("data_dir").hasArg()
		.withDescription("data dir [" + EVENT_DATA_DIR + "]").create("data_dir"));

	options.addOption(OptionBuilder.withArgName("seed_preset").hasArg().withDescription("(true, false) [false]")
		.create("seed_preset"));

	options.addOption(OptionBuilder.withArgName("mating_success").hasArg().withType(Double.class)
		.withDescription("mating success rate [" + MATING_SUCCESS_RATE + "]").create("mating_success"));

	options.addOption(OptionBuilder.withArgName("mating_prox_rad").hasArg().withType(Integer.class)
		.withDescription("mating proximity radius [" + MATING_PROXIMITY_RADIUS + "]").create("mating_prox_rad"));

	options.addOption(OptionBuilder.withArgName("mating_gen_freq").hasArg().withType(Integer.class)
		.withDescription("mating generation frequency [" + MATING_GENERATION_FREQUENCY + "]")
		.create("mating_gen_freq"));

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
		} else if (modelName.equalsIgnoreCase("alife")) {
		    EVOLUTIONARY_MODEL = EvolutionaryModel.ALIFE_MODEL;
		} else {
		    throw new ParseException("model name: " + modelName + " was not recognized");
		}
	    }
	    printAndStore("EVOLUTIONARY_MODEL = " + EVOLUTIONARY_MODEL);

	    if (line.hasOption("no_randomization")) {
		RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = false;
	    }
	    printAndStore("RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = " + RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM);

	    if (line.hasOption("show_graphics")) {
		SHOW_GRAPHICS = true;
	    }
	    printAndStore("SHOW_GRAPHICS = " + SHOW_GRAPHICS);

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
		RESOURCE_CAPTURE_GOAL = new Double(line.getOptionValue("goal")).doubleValue();
	    }

	    printAndStore("RESOURCE_CAPTURE_GOAL = " + RESOURCE_CAPTURE_GOAL);

	    if (line.hasOption("obstacle_density")) {
		OBSTACLE_DENSITY = new Double(line.getOptionValue("obstacle_density")).doubleValue();
	    }

	    printAndStore("OBSTACLE_DENSITY = " + OBSTACLE_DENSITY);

	    if (line.hasOption("resource_density")) {
		RESOURCE_DENSITY = new Double(line.getOptionValue("resource_density")).doubleValue();
	    }
	    printAndStore("RESOURCE_DENSITY = " + RESOURCE_DENSITY);

	    if (line.hasOption("mating_success")) {
		MATING_SUCCESS_RATE = new Double(line.getOptionValue("mating_success")).doubleValue();
	    }
	    printAndStore("MATING_FREQUENCY = " + MATING_SUCCESS_RATE);

	    if (line.hasOption("mating_prox_rad")) {
		MATING_PROXIMITY_RADIUS = new Integer(line.getOptionValue("mating_prox_rad")).intValue();

	    }
	    printAndStore("MATING_PROXIMITY_RADIUS = " + MATING_PROXIMITY_RADIUS);

	    if (line.hasOption("mating_gen_freq")) {
		MATING_GENERATION_FREQUENCY = new Integer(line.getOptionValue("mating_gen_freq")).intValue();

	    }
	    printAndStore("MATING_GENERATION_FREQUENCY = " + MATING_GENERATION_FREQUENCY);

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

		String speciesNames = line.getOptionValue("species").toLowerCase();
		if (!(speciesNames.contains("hetero") || speciesNames.contains("extractor")
			|| speciesNames.contains("detector") || speciesNames.contains("transporter"))) {
		    throw new ParseException("species: " + speciesNames + " was not recognized");
		}
		MODEL_SPECIES = speciesNames;

	    }
	    printAndStore("MODEL_SPECIES = " + MODEL_SPECIES);

	    if (line.hasOption("interactions")) {
		String interactions = line.getOptionValue("interactions").toLowerCase();
		if (!(interactions.contains("none") || interactions.contains("trail")
			|| interactions.contains("broadcast") || interactions.contains("unicast_n") || interactions
			    .contains("unicast_g"))) {
		    throw new ParseException("interactions: " + interactions + " was not recognized");
		}
		MODEL_INTERACTIONS = interactions;

	    }
	    printAndStore("MODEL_INTERACTIONS = " + MODEL_INTERACTIONS);

	    if (line.hasOption("generations")) {
		GENERATIONS = new Integer(line.getOptionValue("generations")).intValue();

	    }
	    printAndStore("GENERATIONS = " + GENERATIONS);

	    if (line.hasOption("seed_preset")) {
		String seedPreset = line.getOptionValue("seed_preset");
		if (seedPreset.equalsIgnoreCase("true")) {
		    SEED_GENOTYPE_PRESET_INSTRUCTIONS = true;

		}

		else if (seedPreset.equalsIgnoreCase("false")) {
		    SEED_GENOTYPE_PRESET_INSTRUCTIONS = false;
		} else {
		    throw new ParseException("seed_preset: " + seedPreset + " was not recognized");
		}

	    }

	    printAndStore("SEED.PRESET = " + SEED_GENOTYPE_PRESET_INSTRUCTIONS);

	    if (line.hasOption("clones")) {
		CLONES_PER_SPECIES = new Integer(line.getOptionValue("clones")).intValue();

	    }
	    printAndStore("CLONES_PER_SPECIES = " + CLONES_PER_SPECIES);

	    if (line.hasOption("pool_size")) {
		GENE_POOL_SIZE = new Integer(line.getOptionValue("pool_size")).intValue();
		EMBODIED_AGENT_POOL_SIZE = GENE_POOL_SIZE;

	    }
	    printAndStore("GENE_POOL_SIZE = " + GENE_POOL_SIZE);
	    printAndStore("EMBODIED_AGENT_POOL_SIZE = " + EMBODIED_AGENT_POOL_SIZE);

	    if (line.hasOption("collection_sites")) {
		NUMBER_OF_COLLECTION_SITES = new Integer(line.getOptionValue("collection_sites")).intValue();

	    }
	    printAndStore("NUMBER_OF_COLLECTION_SITES = " + NUMBER_OF_COLLECTION_SITES);

	    if (line.hasOption("width")) {
		WORLD_WIDTH = new Integer(line.getOptionValue("width")).intValue();

	    }

	    printAndStore("WORLD_WIDTH = " + WORLD_WIDTH);
	    if (line.hasOption("height")) {
		WORLD_HEIGHT = new Integer(line.getOptionValue("height")).intValue();

	    }
	    printAndStore("WORLD_HEIGHT = " + WORLD_HEIGHT);

	    MAX_STEPS_PER_AGENT = WORLD_WIDTH * WORLD_HEIGHT * 4;

	    if (line.hasOption("max_steps")) {
		MAX_STEPS_PER_AGENT = new Integer(line.getOptionValue("max_steps")).intValue();

	    }
	    printAndStore("MAX_STEPS_PER_AGENT = " + MAX_STEPS_PER_AGENT);

	    if (line.hasOption("repeat")) {
		REPEAT = new Integer(line.getOptionValue("repeat")).intValue();

	    }
	    printAndStore("NUM_EXPERIMENTS = " + REPEAT);

	    if (line.hasOption("data_dir")) {
		EVENT_DATA_DIR = line.getOptionValue("data_dir");

		File dir = new File(EVENT_DATA_DIR);
		if (dir.exists() && dir.isDirectory()) {
		    // D.p(EVENT_DATA_DIR +
		    // " already exists; contents might be replaced");
		} else if (dir.exists() && !dir.isDirectory()) {
		    throw new ParseException("A file by the name: " + EVENT_DATA_DIR
			    + " exists and needs to be removed first");
		} else {
		    if (!dir.mkdir()) {
			throw new ParseException("Unable to create: " + EVENT_DATA_DIR + "directory; check permissions");
		    } else {
			// D.p(EVENT_DATA_DIR + " created.");
		    }
		}

		EVENT_DATA_FILE = EVENT_DATA_DIR + "/event_data.csv";
		EXPERIMENT_DETAILS_FILE = EVENT_DATA_DIR + "/experiment_details.txt";

	    }
	    printAndStore("EVENT_DATA_DIR = " + EVENT_DATA_DIR);

	    // some calculated values
	    PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);
	    PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);
	    SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

	    printAndStore("PRIMARY_COLLECTION_SITE_X = " + PRIMARY_COLLECTION_SITE_X);
	    printAndStore("PRIMARY_COLLECTION_SITE_Y = " + PRIMARY_COLLECTION_SITE_Y);
	    printAndStore("MAX_SIMS_PER_EXPERIMENT = " + SIMS_PER_EXPERIMENT);

	    double gridArea = WORLD_WIDTH * WORLD_HEIGHT;

	    int numberOfObstacles = (int) (gridArea * OBSTACLE_DENSITY);
	    int numberOfResources = (int) (gridArea * RESOURCE_DENSITY);
	    int resourceCaptureGoal = (int) ((double) numberOfResources * RESOURCE_CAPTURE_GOAL);

	    printAndStore("ACTUAL_OBSTACLES = " + numberOfObstacles);
	    printAndStore("ACTUAL_RESOURCES = " + numberOfResources);
	    printAndStore("RESOURCE_CAPTURE_GOAL = " + resourceCaptureGoal);

	    D.p("=================================================");

	} catch (ParseException exp) {
	    // oops, something went wrong
	    if (args.length == 1 && args[0].toLowerCase().contains("help")) {
		// check if this was a request for help...
		formatter.printHelp("com.synthverse.Main", options);
		System.exit(0);
	    } else {
		System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		formatter.printHelp("com.synthverse.Main", options);
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
