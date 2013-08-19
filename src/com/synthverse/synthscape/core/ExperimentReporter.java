package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.synthverse.util.DateUtils;

/**
 * This stores experiment output into physical medium
 * 
 * @author sadat
 * 
 */
public class ExperimentReporter implements Constants {

    private static int STRING_BUFFER_MAX_SIZE = 175;

    private final Simulation simulation;
    private BufferedWriter bufferedWriter = null;
    private final static char COMMA = ',';
    private StringBuilder sb = new StringBuilder(STRING_BUFFER_MAX_SIZE);

    private final boolean flushAlways;

    @SuppressWarnings("unused")
    private ExperimentReporter() {
	throw new AssertionError("not allowed");
    }

    public ExperimentReporter(Simulation simulation, boolean flushAlways) throws IOException {

	this.simulation = simulation;
	this.flushAlways = flushAlways;

    }

    private void openFile() {
	File file = new File(simulation.getEventFileName());
	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true),
		    REPORT_WRITER_BUFFER_SIZE);

	} catch (Exception e) {
	    D.p("Exception while trying to open experiment output file: " + e.getMessage());
	    e.printStackTrace();
	}

    }

    private void writeExperimentMetaData() {

	try {
	    bufferedWriter
		    .write("SERVER,EXPERIMENT,BATCH_ID,START_DATE,WIDTH,HEIGHT,OBSTACLE_DENSITY,RESOURCE_DENSITY,NUM_AGENTS_PER_SPECIES,NUM_COLLECTION_SITES,NUM_STEPS_PER_AGENT,PROBLEM_COMPLEXITY,SPECIES,INTERACTIONS");
	    bufferedWriter.newLine();

	    bufferedWriter.append(simulation.getServerName());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append(simulation.getExperimentName());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append(simulation.getBatchId());
	    bufferedWriter.append(COMMA);
	    bufferedWriter
		    .append(DateUtils.getReportFormattedDateString(simulation.getStartDate()));
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getGridWidth());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getGridHeight());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getObstacleDensity());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getResourceDensity());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getClonesPerSpecies());
	    bufferedWriter.append(COMMA);

	    bufferedWriter.append("" + simulation.getNumberOfCollectionSites());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getMaxStepsPerAgent());
	    bufferedWriter.append(COMMA);
	    bufferedWriter.append("" + simulation.getProblemComplexity().getId());
	    bufferedWriter.append(COMMA);

	    bufferedWriter.append("" + simulation.getSpeciesCompositionSting());

	    bufferedWriter.append(COMMA);

	    bufferedWriter.append("" + simulation.getInteractionMechanismsString());

	    bufferedWriter.newLine();
	    bufferedWriter.newLine();
	} catch (Exception e) {
	    D.p("Exception while reporting event:" + e.getMessage());
	    e.printStackTrace();

	}

    }

    private void writeFieldDescription() {
	try {
	    bufferedWriter
		    .write("SIMULATION,AGENT_GENERATION,AGENT_SPECIES,AGENT_ID,STEP,X,Y,EVENT,SRC,DEST");
	    bufferedWriter.newLine();

	} catch (Exception e) {
	    D.p("Exception while reporting event:" + e.getMessage());
	    e.printStackTrace();

	}

    }

    public void reportEvent(int simulationNumber, int generation, Species species, int agentId,
	    int step, int x, int y, Event event, String source, String destination) {
	try {
	    if (simulation.isRecordExperiment()) {
		sb.append(simulationNumber);
		sb.append(COMMA);
		sb.append(generation);
		sb.append(COMMA);
		sb.append(species.getId());
		sb.append(COMMA);
		sb.append(agentId);
		sb.append(COMMA);
		sb.append(step);
		sb.append(COMMA);
		sb.append(x);
		sb.append(COMMA);
		sb.append(y);
		sb.append(COMMA);
		sb.append(event.getId());
		sb.append(COMMA);
		sb.append(source);
		sb.append(COMMA);
		sb.append(destination);

		bufferedWriter.write(sb.toString());
		bufferedWriter.newLine();
		sb.delete(0, sb.length());

		if (this.flushAlways) {
		    bufferedWriter.flush();
		}
	    }
	} catch (Exception e) {
	    D.p("Exception while reporting event:" + e.getMessage());
	    e.printStackTrace();

	}

    }

    private void closeFile() {
	try {
	    bufferedWriter.close();
	} catch (Exception e) {
	    D.p("Exception while closing:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    public void cleanupReporter() {
	if (simulation.isRecordExperiment()) {
	    closeFile();
	}
    }

    public void initReporter() {
	if (simulation.isRecordExperiment()) {

	    openFile();
	    if (Constants.INCLUDE_EXPERIMENT_META_DATA) {
		writeExperimentMetaData();
	    }
	    writeFieldDescription();
	}
    }

}
