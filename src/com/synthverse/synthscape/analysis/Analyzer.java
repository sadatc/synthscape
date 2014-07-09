package com.synthverse.synthscape.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.synthscape.core.D;

public class Analyzer {

    public static ArrayList<String> expectedFields = new ArrayList<String>();
    public static LinkedHashMap<String, SummaryStatistics> fieldStats = new LinkedHashMap<String, SummaryStatistics>();

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
		    D.p("CSV directory named:" + directoryName + "  contains no CSV files");
		}
	    } else {
		D.p("CSV directory named:" + directoryName + "  not found");
	    }

	} else {
	    D.p("No CSV directory mentioned with the -dir command line option");
	}

    }

    private static boolean processFields(String line) {
	boolean result = false;

	if (line.startsWith("GENERATION")) {
	    // this is a field line...
	    // now check if we are encountering field names
	    // for the first time -- if so, store it
	    // otherwise match it...
	    if (expectedFields.size() == 0) {
		// parse rest of the header
		String[] fieldNames = line.split(",");
		for (String fieldName : fieldNames) {
		    fieldName = fieldName.trim();
		    expectedFields.add(fieldName);
		    SummaryStatistics stats = new SummaryStatistics();
		    fieldStats.put(fieldName, stats);
		}
		result = true;
	    } else {

		String[] fieldNames = line.split(",");
		int counter = 0;
		result = true;
		for (String fieldName : fieldNames) {
		    fieldName = fieldName.trim();
		    if (!fieldName.equals(expectedFields.get(counter))) {
			D.p("ERROR:" + fieldName + " didn't match expected field:" + expectedFields.get(counter));
			result = false;
			break;
		    }
		    counter++;
		}

	    }
	} else {
	    // field labels not found as expected...
	    D.p("ERROR: line didn't start with GENERATION as expected");
	    result = false;
	}

	return result;

    }

    private static void processCSVs(File[] csvFiles) throws Exception {
	int csvCounter = 0;

	for (File csvFile : csvFiles) {
	    D.p("going to process:" + csvFile.getName());
	    int linesRead = 0;

	    BufferedReader reader = new BufferedReader(new FileReader(csvFile));
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (linesRead == 0) {
		    if (!processFields(line)) {
			D.p("ERROR while processing " + csvFile.getName() + " ... moving on...");
			break;
		    }
		    linesRead++;
		} else {
		    // process values..
		    processValues(line);
		    //D.p("processing values...");
		}
	    }
	    reader.close();
	    csvCounter++;
	}

	D.p("processed " + csvCounter + " files");

    }

    private static void processValues(String line) {

	
    }

}
