package com.synthverse.synthscape.core;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.synthverse.evolver.islands.PopulationIslandEvolver;
import com.synthverse.synthscape.experiment.dissertation.islands.PopulationIslandSimulation;

public class Settings {

    private static Settings instance = null;

    public boolean RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = false;

    public int GENERATIONS = 500000;

    public int CLONES_PER_SPECIES = 10;

    public int EE_DEF_GENE_POOL_SIZE = 2000;

    public int NUMBER_OF_COLLECTION_SITES = 5;

    public double RESOURCE_CAPTURE_GOAL = 0.5;

    public int SIMS_PER_EXPERIMENT = GENERATIONS * EE_DEF_GENE_POOL_SIZE;

    public ProblemComplexity PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;

    public EvolutionaryModel EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;

    public String MODEL_SPECIES = "super";

    public String MODEL_INTERACTIONS = "none";

    public int WORLD_WIDTH = 20;

    public int WORLD_HEIGHT = 20;

    public double OBSTACLE_DENSITY = 0.05;

    public double RESOURCE_DENSITY = 0.06;

    public int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);

    public int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);

    private Settings() {

    }

    @SuppressWarnings("static-access")
    public void processCommandLineInput(String[] args) {
	// build up all the command line options
	Options options = new Options();

	options.addOption(new Option("help", "print this message"));
	options.addOption(new Option("randomize_each_sim", "randomize each sim [true]"));
	options.addOption(new Option("use_4_tasks", "use 4 tasks [3]"));

	options.addOption(OptionBuilder.withArgName("model").isRequired().hasArg()
		.withDescription("island,embedded,alife").create("model"));

	options.addOption(OptionBuilder.withArgName("species").isRequired().hasArg()
		.withDescription("species names (detector,extractor,transporter,super)").create("species"));

	options.addOption(OptionBuilder.withArgName("interactions").isRequired().hasArg()
		.withDescription("interactions names (trail,broadcast,unicast_n,unicast_g)").create("interactions"));

	options.addOption(OptionBuilder.withArgName("generations").hasArg().withType(Integer.class)
		.withDescription("maximum generations [" + GENERATIONS + "]").create("generations"));

	options.addOption(OptionBuilder.withArgName("clones").hasArg().withType(Integer.class)
		.withDescription("clones per species [" + CLONES_PER_SPECIES + "]").create("clones"));

	options.addOption(OptionBuilder.withArgName("pool_size").hasArg().withType(Integer.class)
		.withDescription("gene pool size [" + EE_DEF_GENE_POOL_SIZE + "]").create("pool_size"));

	options.addOption(OptionBuilder.withArgName("collection_sites").hasArg().withType(Integer.class)
		.withDescription("number of collection sites [" + NUMBER_OF_COLLECTION_SITES + "]")
		.create("collection_sites"));

	options.addOption(OptionBuilder.withArgName("width").hasArg().withType(Integer.class)
		.withDescription("world width [" + WORLD_WIDTH + "]").create("width"));

	options.addOption(OptionBuilder.withArgName("height").hasArg().withType(Integer.class)
		.withDescription("world height [" + WORLD_HEIGHT + "]").create("height"));

	options.addOption(OptionBuilder.withArgName("obstacle_density").hasArg().withType(Double.class)
		.withDescription("obstacle density [" + OBSTACLE_DENSITY + "]").create("obstacle_density"));

	options.addOption(OptionBuilder.withArgName("resource_density").hasArg().withType(Double.class)
		.withDescription("resource density [" + RESOURCE_DENSITY + "]").create("resource_density"));

	options.addOption(OptionBuilder.withArgName("goal").hasArg().withType(Double.class)
		.withDescription("resource capture goal [" + RESOURCE_CAPTURE_GOAL + "]").create("goal"));

	HelpFormatter formatter = new HelpFormatter();

	// create the parser
	CommandLineParser parser = new BasicParser();

	try {
	    // parse the command line arguments
	    CommandLine line = parser.parse(options, args);

	    if (line.hasOption("randomize_each_sim")) {
		RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = true;
	    } else {
		RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM = false;
	    }
	    D.p("RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM=" + RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM);

	    if (line.hasOption("use_4_tasks")) {
		PROBLEM_COMPLEXITY = ProblemComplexity.FOUR_SEQUENTIAL_TASKS;
	    } else {
		PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;
	    }
	    D.p("PROBLEM_COMPLEXITY=" + PROBLEM_COMPLEXITY);

	    if (line.hasOption("goal")) {
		RESOURCE_CAPTURE_GOAL = new Double(line.getOptionValue("goal")).doubleValue();
	    }
	    D.p("RESOURCE_CAPTURE_GOAL=" + RESOURCE_CAPTURE_GOAL);

	    if (line.hasOption("obstacle_density")) {
		OBSTACLE_DENSITY = new Double(line.getOptionValue("obstacle_density")).doubleValue();
	    }
	    D.p("OBSTACLE_DENSITY=" + OBSTACLE_DENSITY);

	    if (line.hasOption("resource_density")) {
		RESOURCE_DENSITY = new Double(line.getOptionValue("resource_density")).doubleValue();
	    }
	    D.p("RESOURCE_DENSITY=" + RESOURCE_DENSITY);

	    if (line.hasOption("model")) {
		String modelName = line.getOptionValue("model");
		if (modelName.equalsIgnoreCase("island")) {
		    EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;
		} else {
		    throw new ParseException("model name:"+modelName+" was not recognized");
		}
		D.p("EVOLUTIONARY_MODEL=" + EVOLUTIONARY_MODEL);
	    }

	    if (line.hasOption("species")) {
		MODEL_SPECIES = line.getOptionValue("species");
		D.p("MODEL_SPECIES=" + MODEL_SPECIES);
	    }

	    if (line.hasOption("interactions")) {
		MODEL_INTERACTIONS = line.getOptionValue("interactions");
		D.p("MODEL_INTERACTIONS=" + MODEL_INTERACTIONS);
	    }

	    if (line.hasOption("generations")) {
		GENERATIONS = new Integer(line.getOptionValue("generations")).intValue();
		D.p("GENERATIONS=" + GENERATIONS);
	    }

	    if (line.hasOption("clones")) {
		CLONES_PER_SPECIES = new Integer(line.getOptionValue("clones")).intValue();
		D.p("CLONES_PER_SPECIES=" + CLONES_PER_SPECIES);
	    }

	    if (line.hasOption("pool_size")) {
		EE_DEF_GENE_POOL_SIZE = new Integer(line.getOptionValue("pool_size")).intValue();
		D.p("EE_DEF_GENE_POOL_SIZE=" + EE_DEF_GENE_POOL_SIZE);
	    }

	    if (line.hasOption("collection_sites")) {
		NUMBER_OF_COLLECTION_SITES = new Integer(line.getOptionValue("collection_sites")).intValue();
		D.p("NUMBER_OF_COLLECTION_SITES=" + NUMBER_OF_COLLECTION_SITES);
	    }

	    if (line.hasOption("width")) {
		WORLD_WIDTH = new Integer(line.getOptionValue("width")).intValue();
		D.p("WORLD_WIDTH=" + WORLD_WIDTH);
	    }

	    if (line.hasOption("height")) {
		WORLD_HEIGHT = new Integer(line.getOptionValue("height")).intValue();
		D.p("WORLD_HEIGHT=" + WORLD_HEIGHT);
	    }

	    // some calculated values
	    PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);
	    PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);
	    SIMS_PER_EXPERIMENT = GENERATIONS * EE_DEF_GENE_POOL_SIZE;

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
