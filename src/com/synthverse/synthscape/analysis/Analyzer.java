package com.synthverse.synthscape.analysis;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.synthverse.synthscape.core.D;

public class Analyzer {

    static class OnlyCSV implements FilenameFilter {
	public boolean accept(File fullPath, String fileName) {
	    boolean result = false;
	    if (fileName.toLowerCase().endsWith(".csv")) {
		result = true;
	    }
	    return result;
	}
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) throws Exception {
	Options options = new Options();

	options.addOption(OptionBuilder.withArgName("dir").isRequired().hasArg()
		.withDescription("directory with all the csv files").create("dir"));

	CommandLineParser parser = new BasicParser();

	CommandLine line = parser.parse(options, args);
	String directoryName = "";
	if (line.hasOption("dir")) {
	    directoryName = line.getOptionValue("dir");
	    File csvDirectory = new File(directoryName);
	    if (csvDirectory.exists()) {
		File[] csvFiles = csvDirectory.listFiles(new Analyzer.OnlyCSV());
		if (csvFiles.length > 0) {
		    processCSVs(csvFiles);
		} else {
		    throw new Exception("CSV directory named:" + directoryName + "  contains no CSV files");
		}
	    } else {
		throw new Exception("CSV directory named:" + directoryName + "  not found");
	    }

	} else {
	    throw new Exception("No CSV directory mentioned with the -dir command line option");
	}

    }

    private static void processCSVs(File[] csvFiles) {
	for(File csvFile: csvFiles) {
	    D.p("going to process:"+csvFile.getName());
	}

    }

}
