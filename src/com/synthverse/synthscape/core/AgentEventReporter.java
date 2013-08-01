package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AgentEventReporter implements Constants {

    private static int STRING_BUFFER_MAX_SIZE = 120;

    private boolean flushConstantly = false;
    private String reportFileName = null;
    private BufferedWriter bufferedWriter = null;
    private final static char COMMA = ',';
    private StringBuilder sb = new StringBuilder(STRING_BUFFER_MAX_SIZE);

    @SuppressWarnings("unused")
    private AgentEventReporter() {
	throw new AssertionError("not allowed");
    }

    public AgentEventReporter(boolean flushConstantly, String reportFile) throws IOException {
	this.flushConstantly = flushConstantly;
	this.reportFileName = reportFile;
	openFile();
    }

    public void openFile() {
	File file = new File(reportFileName);
	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true),
		    REPORT_WRITER_BUFFER_SIZE);
	    writeCSVHeader();
	} catch (Exception e) {
	    D.p("Exception while trying to open experiment output file: " + e.getMessage());
	    e.printStackTrace();
	}

    }

    private void writeCSVHeader() throws IOException {

	bufferedWriter
		.write("SERVER,EXPERIMENT,EXPERIMENT_ID,SIMULATION,GENERATION,AGENT_ID,TRAITS,STEP,EVENT");
	bufferedWriter.newLine();

    }

    public void reportEvent(String serverName, String experimentName, String experimentId,
	    int simulationNumber, int generation, Species species, int agentId, int step,
	    Event event) {
	try {
	    sb.append(serverName);
	    sb.append(COMMA);
	    sb.append(experimentName);
	    sb.append(COMMA);
	    sb.append(experimentId);
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
	    sb.append(event.toString());
	    bufferedWriter.write(sb.toString());
	    bufferedWriter.newLine();
	    sb.delete(0, sb.length());

	    if (this.flushConstantly) {
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
