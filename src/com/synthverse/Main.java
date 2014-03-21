package com.synthverse;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.synthverse.synthscape.core.D;

@SuppressWarnings("static-access")
public class Main {
    public static void main(String[] args) {
	D.p("WELCOME");

	// build up all the command line options
	Options options = new Options();

	options.addOption(new Option("help", "print this message"));
	options.addOption(new Option("randomize_each_sim", "randomize each simulation"));
	options.addOption(new Option("use_4_task", "use 4 tasks instead of 3"));
	options.addOption(new Option("collect_all_resources", "collect all resources instead of half"));

	options.addOption(OptionBuilder.withArgName("model").isRequired().hasArg()
		.withDescription("island,embedded,alife").create("model"));

	options.addOption(OptionBuilder.withArgName("generations").isRequired().hasArg().withType(Integer.class)
		.withDescription("maximum generations").create("generations"));

	options.addOption(OptionBuilder.withArgName("clones_per_species").isRequired().hasArg().withType(Integer.class)
		.withDescription("clones per species").create("clones_per_species"));

	options.addOption(OptionBuilder.withArgName("genepool_size").isRequired().hasArg().withType(Integer.class)
		.withDescription("gene pool size").create("genepool_size"));
	
	options.addOption(OptionBuilder.withArgName("collection_sites").isRequired().hasArg().withType(Integer.class)
		.withDescription("number of collection sites").create("collection_sites"));

	HelpFormatter formatter = new HelpFormatter();

	// create the parser
	CommandLineParser parser = new BasicParser();

	try {
	    // parse the command line arguments
	    CommandLine line = parser.parse(options, args);

	    if (line.hasOption("model")) {
		D.p("model=" + line.getOptionValue("model"));
	    }

	    if (line.hasOption("generations")) {
		D.p("generations=" + line.getOptionValue("generations"));
	    }

	    if (line.hasOption("help")) {

		formatter.printHelp("com.synthverse.Main", options);

	    }

	} catch (ParseException exp) {
	    // oops, something went wrong
	    System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	    formatter.printHelp("com.synthverse.Main", options);

	}

	// PopulationIslandSimulation.main(arg);
    }

}
