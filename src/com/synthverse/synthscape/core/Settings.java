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

    public int GENERATIONS = 3000;

    public int CLONES_PER_SPECIES = 10;

    public int EE_DEF_GENE_POOL_SIZE = 2000;

    public int NUMBER_OF_COLLECTION_SITES = 5;

    public double RESOURCE_CAPTURE_GOAL = 0.5;

    public int SIMS_PER_EXPERIMENT = GENERATIONS * EE_DEF_GENE_POOL_SIZE;

    public ProblemComplexity PROBLEM_COMPLEXITY = ProblemComplexity.THREE_SEQUENTIAL_TASKS;

    public EvolutionaryModel EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;

    public String MODEL_SPECIES = "super";

    public String MODEL_INTERACTIONS = "none";

    public int WORLD_WIDTH = 16;

    public int WORLD_HEIGHT = 16;

    public int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);

    public int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);

    private Settings() {

    }

    @SuppressWarnings("static-access")
    public void processCommandLineInput(String[] args) {
	// build up all the command line options
	Options options = new Options();

	options.addOption(new Option("help", "print this message"));
	options.addOption(new Option("randomize_each_sim", "randomize each simulation"));
	options.addOption(new Option("use_4_tasks", "use 4 tasks instead of 3"));
	options.addOption(new Option("collect_all_resources",
		"collect all resources instead of half"));

	options.addOption(OptionBuilder.withArgName("model").isRequired().hasArg()
		.withDescription("island,embedded,alife").create("model"));

	options.addOption(OptionBuilder.withArgName("species").isRequired().hasArg()
		.withDescription("species names (CSV)").create("species"));

	options.addOption(OptionBuilder.withArgName("interactions").isRequired().hasArg()
		.withDescription("interactions names (CSV)").create("interactions"));

	options.addOption(OptionBuilder.withArgName("generations").isRequired().hasArg()
		.withType(Integer.class).withDescription("maximum generations")
		.create("generations"));

	options.addOption(OptionBuilder.withArgName("clones_per_species").isRequired().hasArg()
		.withType(Integer.class).withDescription("clones per species")
		.create("clones_per_species"));

	options.addOption(OptionBuilder.withArgName("genepool_size").isRequired().hasArg()
		.withType(Integer.class).withDescription("gene pool size").create("genepool_size"));

	options.addOption(OptionBuilder.withArgName("collection_sites").isRequired().hasArg()
		.withType(Integer.class).withDescription("number of collection sites")
		.create("collection_sites"));

	options.addOption(OptionBuilder.withArgName("width").isRequired().hasArg()
		.withType(Integer.class).withDescription("world width").create("width"));

	options.addOption(OptionBuilder.withArgName("height").isRequired().hasArg()
		.withType(Integer.class).withDescription("world height").create("height"));

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

	    if (line.hasOption("collect_all_resources")) {
		RESOURCE_CAPTURE_GOAL = 1.0;
	    } else {
		RESOURCE_CAPTURE_GOAL = 0.5;
	    }
	    D.p("RESOURCE_CAPTURE_GOAL=" + RESOURCE_CAPTURE_GOAL);

	    if (line.hasOption("model")) {
		String modelName = line.getOptionValue("model");
		if (modelName.equalsIgnoreCase("island")) {
		    EVOLUTIONARY_MODEL = EvolutionaryModel.ISLAND_MODEL;
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

	    if (line.hasOption("clones_per_species")) {
		CLONES_PER_SPECIES = new Integer(line.getOptionValue("clones_per_species"))
			.intValue();
		D.p("CLONES_PER_SPECIES=" + CLONES_PER_SPECIES);
	    }

	    if (line.hasOption("genepool_size")) {
		EE_DEF_GENE_POOL_SIZE = new Integer(line.getOptionValue("genepool_size"))
			.intValue();
		D.p("EE_DEF_GENE_POOL_SIZE=" + EE_DEF_GENE_POOL_SIZE);
	    }

	    if (line.hasOption("collection_sites")) {
		NUMBER_OF_COLLECTION_SITES = new Integer(line.getOptionValue("collection_sites"))
			.intValue();
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

	    if (line.hasOption("help")) {
		formatter.printHelp("com.synthverse.Main", options);
	    }

	} catch (ParseException exp) {
	    // oops, something went wrong
	    System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	    formatter.printHelp("com.synthverse.Main", options);
	    System.exit(1);

	}

    }

    public static Settings getInstance() {
	if (instance == null) {
	    instance = new Settings();
	}

	return instance;

    }

}
