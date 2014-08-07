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

    public enum SeedType {
	NOOPS, ACTIONS, RANDOM;
    }

    private static Settings instance = null;

    public boolean RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = true;

    public boolean SHOW_GRAPHICS = false;

    public boolean PEER_REWARDS = false;

    public long SEED = 1;

    public int GENERATIONS = 10000;

    public int CLONES_PER_SPECIES = 4;

    public int GENE_POOL_SIZE = 512;

    public double COLLECTION_SITE_DENSITY = 0.015625;
    // public double COLLECTION_SITE_DENSITY = 0.031250;

    public int NUMBER_OF_COLLECTION_SITES = 8;

    public double PERC_RESOURCE_CAPTURE_GOAL = 0.75;

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

    public InteractionQuality INTERACTION_QUALITY = InteractionQuality.HIGH;

    public boolean REPORT_DNA_PROGRESSION = false;
    
    public boolean COMPRESS_DNA_PROGRESSION = false;
    

    public int lastReportedCaptures = 0;
    public int lastReportedGeneration = 0;
    public int lastLoggedGeneration = 0;

    public int experimentNumber = 0;

    public String statusCache = "";

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
		.withDescription("species names [detector, extractor, transporter OR homogenous] e.g. detector,transporter")
		.create("species"));

	options.addOption(OptionBuilder.withArgName("interactions").isRequired().hasArg()
		.withDescription("interactions names [none OR trail, broadcast, unicast_n] e.g. trail,broadcast")
		.create("interactions"));

	options.addOption(OptionBuilder.withArgName("interaction_quality").hasArg()
		.withDescription("interaction quality (high, medium, low/poor) [high]").create("interaction_quality"));

	options.addOption(OptionBuilder.withArgName("generations").hasArg().withType(Integer.class)
		.withDescription("maximum generations [" + GENERATIONS + "]").create("generations"));

	options.addOption(OptionBuilder.withArgName("clones").hasArg().withType(Integer.class)
		.withDescription("clones per species [" + CLONES_PER_SPECIES + "]").create("clones"));

	options.addOption(OptionBuilder.withArgName("pool_size").hasArg().withType(Integer.class)
		.withDescription("gene pool size [" + GENE_POOL_SIZE + "]").create("pool_size"));

	options.addOption(OptionBuilder.withArgName("collection_site_density").hasArg().withType(Integer.class)
		.withDescription("collection site density [" + COLLECTION_SITE_DENSITY + "]")
		.create("collection_site_density"));

	options.addOption(OptionBuilder.withArgName("width").hasArg().withType(Integer.class)
		.withDescription("world width [" + WORLD_WIDTH + "]").create("width"));

	options.addOption(OptionBuilder.withArgName("height").hasArg().withType(Integer.class)
		.withDescription("world height [" + WORLD_HEIGHT + "]").create("height"));

	options.addOption(OptionBuilder.withArgName("max_steps").hasArg().withType(Integer.class)
		.withDescription("max steps [" + MAX_STEPS_PER_AGENT + "]").create("max_steps"));

	options.addOption(OptionBuilder.withArgName("obstacle_density").hasArg().withType(Double.class)
		.withDescription("obstacle density [" + OBSTACLE_DENSITY + "]").create("obstacle_density"));

	options.addOption(OptionBuilder.withArgName("resource_density").hasArg().withType(Double.class)
		.withDescription("resource density [" + RESOURCE_DENSITY + "]").create("resource_density"));

	options.addOption(OptionBuilder.withArgName("goal").hasArg().withType(Double.class)
		.withDescription("%resource capture goal [" + PERC_RESOURCE_CAPTURE_GOAL + "]").create("goal"));

	options.addOption(OptionBuilder.withArgName("repeat").hasArg().withType(Integer.class)
		.withDescription("repeat experiment [" + REPEAT + "]").create("repeat"));

	options.addOption(OptionBuilder.withArgName("data_dir").hasArg().withDescription("data dir [" + DATA_DIR + "]")
		.create("data_dir"));

	options.addOption(OptionBuilder.withArgName("job_name").hasArg().withDescription("job name [" + JOB_NAME + "]")
		.create("job_name"));

	options.addOption(OptionBuilder.withArgName("job_set").hasArg().withDescription("job set [" + JOB_SET + "]")
		.create("job_set"));

	options.addOption(OptionBuilder.withArgName("preset_geno").hasArg()
		.withDescription("(noops, actions, random) [random]").create("preset_geno"));

	options.addOption(OptionBuilder.withArgName("mating_success").hasArg().withType(Double.class)
		.withDescription("mating success rate [" + MATING_SUCCESS_RATE + "]").create("mating_success"));

	options.addOption(OptionBuilder.withArgName("mating_prox_rad").hasArg().withType(Integer.class)
		.withDescription("mating proximity radius [" + MATING_PROXIMITY_RADIUS + "]").create("mating_prox_rad"));

	options.addOption(OptionBuilder.withArgName("mating_gen_freq").hasArg().withType(Integer.class)
		.withDescription("mating generation frequency [" + MATING_GENERATION_FREQUENCY + "]")
		.create("mating_gen_freq"));

	options.addOption(OptionBuilder.withArgName("seed").hasArg().withType(Long.class)
		.withDescription("seed for randomizer [" + SEED + "]").create("seed"));

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
		PERC_RESOURCE_CAPTURE_GOAL = new Double(line.getOptionValue("goal")).doubleValue();
	    }

	    printAndStore("PERC_RESOURCE_CAPTURE_GOAL = " + PERC_RESOURCE_CAPTURE_GOAL);

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
		    throw new ParseException("log level: " + logLevel + " was not recognized");
		}

	    }

	    printAndStore("LOG.LEVEL = " + REQUESTED_LOG_LEVEL.toString());

	    if (line.hasOption("species")) {

		String speciesNames = line.getOptionValue("species").toLowerCase();
		if (!(speciesNames.contains("homogenous") || speciesNames.contains("extractor")
			|| speciesNames.contains("detector") || speciesNames.contains("transporter"))) {
		    throw new ParseException("species: " + speciesNames + " was not recognized");
		}
		MODEL_SPECIES = speciesNames;

	    }
	    printAndStore("MODEL_SPECIES = " + MODEL_SPECIES);

	    if (line.hasOption("interactions")) {
		String interactions = line.getOptionValue("interactions").toLowerCase();
		if (!(interactions.contains("none") || interactions.contains("trail")
			|| interactions.contains("broadcast") || interactions.contains("unicast_n"))) {
		    throw new ParseException("interactions: " + interactions + " was not recognized");
		}
		MODEL_INTERACTIONS = interactions;

	    }
	    printAndStore("MODEL_INTERACTIONS = " + MODEL_INTERACTIONS);

	    if (line.hasOption("interaction_quality")) {
		String quality = line.getOptionValue("interaction_quality").toLowerCase();
		if (quality.contains("poor") || quality.contains("low")) {
		    INTERACTION_QUALITY = InteractionQuality.POOR;
		} else if (quality.contains("medium")) {
		    INTERACTION_QUALITY = InteractionQuality.MEDIUM;
		} else if (quality.contains("high")) {
		    INTERACTION_QUALITY = InteractionQuality.HIGH;
		} else {
		    throw new ParseException("interactions_quality: " + quality + " was not recognized");
		}

	    }
	    printAndStore("INTERACTION_QUALITY = " + INTERACTION_QUALITY);

	    if (line.hasOption("generations")) {
		GENERATIONS = new Integer(line.getOptionValue("generations")).intValue();

	    }
	    printAndStore("GENERATIONS = " + GENERATIONS);

	    if (line.hasOption("preset_geno")) {
		String seedPreset = line.getOptionValue("preset_geno");
		if (seedPreset.equalsIgnoreCase("random")) {
		    SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.RANDOM;
		}

		else if (seedPreset.equalsIgnoreCase("actions")) {
		    SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.ACTIONS;
		} else if (seedPreset.equalsIgnoreCase("noops")) {
		    SEED_GENOTYPE_PRESET_INSTRUCTIONS = SeedType.NOOPS;

		} else {
		    throw new ParseException("preset_geno: " + seedPreset + " was not recognized");
		}

	    }

	    printAndStore("PRESET.GENOTYPE = " + SEED_GENOTYPE_PRESET_INSTRUCTIONS);

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

	    if (line.hasOption("collection_site_density")) {
		COLLECTION_SITE_DENSITY = new Double(line.getOptionValue("collection_site_density")).intValue();

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

	    if (line.hasOption("max_steps")) {
		MAX_STEPS_PER_AGENT = new Integer(line.getOptionValue("max_steps")).intValue();

	    }
	    printAndStore("MAX_STEPS_PER_AGENT = " + MAX_STEPS_PER_AGENT);

	    if (line.hasOption("repeat")) {
		REPEAT = new Integer(line.getOptionValue("repeat")).intValue();

	    }
	    printAndStore("NUM_EXPERIMENTS = " + REPEAT);

	    if (line.hasOption("job_set")) {
		JOB_SET = new Integer(line.getOptionValue("job_set")).longValue();
		SEED = (JOB_SET * REPEAT) + SEED;

	    }
	    printAndStore("JOB_SET = " + JOB_SET);
	    D.p("SEED = " + SEED);

	    if (line.hasOption("data_dir")) {
		DATA_DIR = line.getOptionValue("data_dir");

		File dir = new File(DATA_DIR);
		if (dir.exists() && dir.isDirectory()) {
		    // D.p(EVENT_DATA_DIR +
		    // " already exists; contents might be replaced");
		} else if (dir.exists() && !dir.isDirectory()) {
		    throw new ParseException("A file by the name: " + DATA_DIR
			    + " exists and needs to be removed first");
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
		if (!jobName.trim().equals("")) {
		    JOB_NAME = jobName;
		}

		else {
		    throw new ParseException("job_name: [" + jobName + "] was not recognized");
		}

	    }
	    printAndStore("JOB_NAME = " + JOB_NAME);

	    // some calculated values
	    PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.50);
	    PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.50);
	    SIMS_PER_EXPERIMENT = GENERATIONS * GENE_POOL_SIZE;

	    printAndStore("PRIMARY_COLLECTION_SITE_X = " + PRIMARY_COLLECTION_SITE_X);
	    printAndStore("PRIMARY_COLLECTION_SITE_Y = " + PRIMARY_COLLECTION_SITE_Y);
	    printAndStore("MAX_SIMS_PER_EXPERIMENT = " + SIMS_PER_EXPERIMENT);

	    double gridArea = WORLD_WIDTH * WORLD_HEIGHT;

	    int numberOfObstacles = (int) (gridArea * OBSTACLE_DENSITY);
	    int numberOfResources = (int) (gridArea * RESOURCE_DENSITY);
	    int resourceCaptureGoal = (int) ((double) numberOfResources * PERC_RESOURCE_CAPTURE_GOAL);
	    NUMBER_OF_COLLECTION_SITES = (int) ((double) gridArea * COLLECTION_SITE_DENSITY);

	    printAndStore("ACTUAL_OBSTACLES = " + numberOfObstacles);
	    printAndStore("ACTUAL_RESOURCES = " + numberOfResources);
	    printAndStore("ACTUAL RESOURCE_CAPTURE_GOAL = " + resourceCaptureGoal);
	    printAndStore("ACTUAL COLLECTION_SITES = " + NUMBER_OF_COLLECTION_SITES);

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
