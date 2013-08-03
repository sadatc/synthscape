package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExperimentReporter implements Constants {

    private static int STRING_BUFFER_MAX_SIZE = 120;

    private final String experimentName;
    private final String serverName;
    private final String batchId;

    private boolean flushAlways = false;
    private String reportFileName = null;
    private BufferedWriter bufferedWriter = null;
    private final static char COMMA = ',';
    private StringBuilder sb = new StringBuilder(STRING_BUFFER_MAX_SIZE);

    @SuppressWarnings("unused")
    private ExperimentReporter() {
	throw new AssertionError("not allowed");
    }

    public ExperimentReporter(Experiment experiment, boolean flushAlways)
	    throws IOException {
	this.serverName = experiment.getServerName();
	this.experimentName = experiment.getName();
	this.batchId = experiment.getBatchId();
	this.flushAlways = flushAlways;
	this.reportFileName = experiment.getEventFileName();
	openFile();
    }

    public void openFile() {
	File file = new File(reportFileName);
	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    bufferedWriter = new BufferedWriter(new FileWriter(
		    file.getAbsoluteFile(), true), REPORT_WRITER_BUFFER_SIZE);
	    writeCSVHeader();
	} catch (Exception e) {
	    D.p("Exception while trying to open experiment output file: "
		    + e.getMessage());
	    e.printStackTrace();
	}

    }

    private void writeCSVHeader() throws IOException {

	bufferedWriter
		.write("SERVER,EXPERIMENT,BATCH_ID,SIMULATION,AGENT_GENERATION,AGENT_SPECIES,AGENT_ID,STEP,X,Y,EVENT,SRC,DEST");
	bufferedWriter.newLine();

    }

    public void reportEvent(int simulationNumber, int generation,
	    Species species, int agentId, int step, int x, int y, Event event,
	    String source, String destination) {
	try {
	    sb.append(serverName);
	    sb.append(COMMA);
	    sb.append(experimentName);
	    sb.append(COMMA);
	    sb.append(batchId);
	    sb.append(COMMA);
	    sb.append(simulationNumber);
	    sb.append(COMMA);
	    sb.append(generation);
	    sb.append(COMMA);
	    sb.append(species.getAbbreviation());
	    sb.append(COMMA);
	    sb.append(agentId);
	    sb.append(COMMA);
	    sb.append(step);
	    sb.append(COMMA);
	    sb.append(x);
	    sb.append(COMMA);
	    sb.append(y);
	    sb.append(COMMA);
	    sb.append(event.toString());
	    sb.append(source);
	    sb.append(COMMA);
	    sb.append(destination);
	    sb.append(COMMA);
	    bufferedWriter.write(sb.toString());
	    bufferedWriter.newLine();
	    sb.delete(0, sb.length());

	    if (this.flushAlways) {
		bufferedWriter.flush();
	    }
	} catch (Exception e) {
	    D.p("Exception while closing:" + e.getMessage());
	    e.printStackTrace();

	}

    }

    public void closeFile() {
	try {
	    bufferedWriter.close();
	} catch (Exception e) {
	    D.p("Exception while closing:" + e.getMessage());
	    e.printStackTrace();
	}
    }

}
