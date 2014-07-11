package com.synthverse.synthscape.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;

public class Analyzer implements Constants {

    public static ArrayList<String> expectedFields = new ArrayList<String>();

    // field --> row --> summary stats
    public static LinkedHashMap<String, LinkedHashMap<Integer, SummaryStatistics>> fieldRowStats = new LinkedHashMap<String, LinkedHashMap<Integer, SummaryStatistics>>();

    // this tells, for a given field, and row, how many elements were
    // encountered
    public static LinkedHashMap<String, LinkedHashMap<Integer, Integer>> fieldRowDataCount = new LinkedHashMap<String, LinkedHashMap<Integer, Integer>>();

    public static int maxRowNumber = 0;

    public static int numProcessedDataSets = 0;

    /**
     * Need this for filtering for CSV files only...
     * 
     * @author sadat
     * 
     */
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
	// grab command line options from the user
	Options options = new Options();
	options.addOption(OptionBuilder.withArgName("dir").isRequired().hasArg()
		.withDescription("directory with all the csv files").create("dir"));

	options.addOption(OptionBuilder.withArgName("outfile").isRequired().hasArg()
		.withDescription("full pathname to output file").create("outfile"));

	CommandLineParser parser = new BasicParser();
	CommandLine line = parser.parse(options, args);
	String directoryName = "";
	String outFileName = null;

	if (line.hasOption("outfile")) {
	    outFileName = line.getOptionValue("outfile");
	    File outFile = new File(outFileName);
	    if (outFile.exists() && !outFile.isFile()) {
		D.p("ERROR: " + outFileName + " exists and is not a file; use a different filename!");
	    } else if (outFile.exists() && outFile.isFile()) {
		D.p("WARNING: " + outFileName + " already exists and will be overwritten!");
		outFile.delete();
	    }

	    // if the directory exists, and it has CSV files only then process
	    // them;
	    // otherwise
	    // return various errors back to the user...
	    if (line.hasOption("dir")) {
		directoryName = line.getOptionValue("dir");
		File csvDirectory = new File(directoryName);
		if (csvDirectory.exists()) {
		    File[] csvFiles = csvDirectory.listFiles(new Analyzer.OnlyCSV());
		    if (csvFiles.length > 0) {
			processCSVs(csvFiles);
			writeSummaryFile(outFile);
		    } else {
			D.p("ERROR: CSV directory named:" + directoryName + "  contains no CSV files");
		    }
		} else {
		    D.p("ERROR: CSV directory named:" + directoryName + "  not found");
		}

	    } else {
		D.p("ERROR: No CSV directory mentioned with the -dir command line option");
	    }

	} else {
	    D.p("ERROR: No outfile mentioned, exiting...");
	}

    }

    /**
     * This writes out the summary file...
     * 
     * @param outFile
     * @throws Exception
     */
    private static void writeSummaryFile(File outFile) throws Exception {
	outFile.createNewFile();

	BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsoluteFile(), false),
		FILE_IO_BUFFER_SIZE);

	// first write the header
	writer.write(expectedFields.get(0));
	for (int i = 1; i < expectedFields.size(); i++) {
	    writer.write(", " + expectedFields.get(i));
	}
	writer.newLine();

	// now write the data...
	for (int rowNumber = 1; rowNumber <= maxRowNumber; rowNumber++) {
	    // go throw each row
	    writer.write(rowNumber + ", ");

	    for (int i = 1; i < expectedFields.size(); i++) {
		// go through each column...
		String data = " ";

		if (fieldRowStats.containsKey(expectedFields.get(i))) {
		    LinkedHashMap<Integer, SummaryStatistics> rowFieldStats = fieldRowStats.get(expectedFields.get(i));
		    if (rowFieldStats.containsKey(rowNumber)) {
			SummaryStatistics stats = rowFieldStats.get(rowNumber);
			//double dataValue = stats.getSum() / numProcessedDataSets;
			double dataValue = stats.getMean();
			data += dataValue;
		    }

		}

		writer.write(data);
		if (i != (expectedFields.size() - 1)) {
		    writer.write(", ");
		}
	    }
	    writer.newLine();
	}

	writer.flush();
	writer.close();

    }

    /**
     * This method does one of two things: (1) It checks if the given row is a
     * header row -- if so, it starts parsing the fields. If this is a first
     * time parse, the fields are stored in a list and the method returns true
     * (2) If this subsequent parse, this matches against the stored field list
     * and makes sure they occur in the same order -- only then it returns true
     * So in a sense this is both a first-time field name grabber and
     * subsequent-time matcher
     * 
     * @param row
     * @return
     */
    private static boolean processFields(String row) {
	boolean result = false;

	if (row.startsWith("GENERATION")) {
	    // this is a field row...
	    // now check if we are encountering field names
	    // for the first time -- if so, store it
	    // otherwise match it...
	    if (expectedFields.size() == 0) {
		// parse rest of the header
		String[] fieldNames = row.split(",");
		for (String fieldName : fieldNames) {
		    fieldName = fieldName.trim();
		    expectedFields.add(fieldName);
		    LinkedHashMap<Integer, SummaryStatistics> rowStats = new LinkedHashMap<Integer, SummaryStatistics>();
		    fieldRowStats.put(fieldName, rowStats);
		}
		result = true;
	    } else {

		String[] fieldNames = row.split(",");
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
	    D.p("ERROR: row didn't start with GENERATION as expected");
	    result = false;
	}

	return result;

    }

    /**
     * Given a list of CSV files, processes them. The first row for each file is
     * checked for headers. Values are read from subsequent files.
     * 
     * @param csvFiles
     * @throws Exception
     */
    private static void processCSVs(File[] csvFiles) throws Exception {
	int csvCounter = 0;
	SummaryStatistics generationStats = new SummaryStatistics();

	for (File csvFile : csvFiles) {

	    int rowNumber = 0;

	    BufferedReader reader = new BufferedReader(new FileReader(csvFile), FILE_IO_BUFFER_SIZE);
	    String row;
	    while ((row = reader.readLine()) != null) {
		if (rowNumber == 0) {
		    // process the header row...
		    // if the header is not found OR if
		    // header doesn't match with what has been read before
		    // skip this file...
		    if (!processFields(row)) {
			D.p("ERROR while processing " + csvFile.getName() + " ... moving on...");
			break;
		    }
		    rowNumber++;
		} else {
		    // process values..
		    processValues(row, rowNumber);
		    rowNumber++;

		}
	    }
	    reader.close();
	    csvCounter++;
	    generationStats.addValue(rowNumber);
	    D.p("processed values from:" + csvFile.getName());
	}

	D.p("processed " + csvCounter + " files");
	D.p("generation stats = "+generationStats);

    }

    private static void processValues(String row, int rowNumber) {
	String[] values = row.split(",");
	boolean valuesProcessed = false;

	for (int i = 0; i < values.length; i++) {
	    String fieldName = expectedFields.get(i);

	    String value = values[i].trim();
	    double doubleValue = 0.0;
	    // if it's empty or nan re-interpret as 0
	    if (value.equals("") || value.equalsIgnoreCase("nan")) {
		// do nothing...
	    } else {
		doubleValue = new Double(value).doubleValue();
		updateMaxRowNumber(rowNumber);
		updateFieldRowCount(fieldName, rowNumber);
		updateFieldRowStats(fieldName, rowNumber, doubleValue);
		valuesProcessed = true;
	    }

	}

	if (valuesProcessed) {
	    numProcessedDataSets++;
	}

    }

    private static void updateFieldRowStats(String fieldName, int rowNumber, double doubleValue) {

	LinkedHashMap<Integer, SummaryStatistics> rowStats = null;

	if (fieldRowStats.containsKey(fieldName)) {
	    rowStats = fieldRowStats.get(fieldName);
	} else {
	    rowStats = new LinkedHashMap<Integer, SummaryStatistics>();
	    fieldRowStats.put(fieldName, rowStats);
	}
	if (rowStats != null) {
	    SummaryStatistics stats;
	    if (rowStats.containsKey(rowNumber)) {
		stats = rowStats.get(rowNumber);
	    } else {
		stats = new SummaryStatistics();
		rowStats.put(rowNumber, stats);
	    }
	    stats.addValue(doubleValue);
	} else {
	    D.p("ERROR: IMPOSSIBLE SITUATION, should never be here!!");
	}

    }

    private static void updateFieldRowCount(String fieldName, int rowNumber) {
	LinkedHashMap<Integer, Integer> rowCounts = null;

	if (fieldRowDataCount.containsKey(fieldName)) {
	    rowCounts = fieldRowDataCount.get(fieldName);
	} else {
	    rowCounts = new LinkedHashMap<Integer, Integer>();
	    fieldRowDataCount.put(fieldName, rowCounts);
	}
	if (rowCounts != null) {

	    if (rowCounts.containsKey(rowNumber)) {
		int count = rowCounts.get(rowNumber);
		count++;
		rowCounts.put(rowNumber, count);
	    } else {
		rowCounts.put(rowNumber, 1);
	    }
	} else {
	    D.p("ERROR: IMPOSSIBLE SITUATION, should never be here!!");
	}

    }

    private static void updateMaxRowNumber(int rowNumber) {
	if (rowNumber > maxRowNumber) {
	    maxRowNumber = rowNumber;
	}
    }

}
