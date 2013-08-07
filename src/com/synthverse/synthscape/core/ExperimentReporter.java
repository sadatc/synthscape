package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// test
public class ExperimentReporter implements Constants {

    private static int STRING_BUFFER_MAX_SIZE = 175;

    private final Experiment experiment;
    private BufferedWriter bufferedWriter = null;
    private final static char COMMA = ',';
    private StringBuilder sb = new StringBuilder(STRING_BUFFER_MAX_SIZE);

    private final boolean flushAlways;

    @SuppressWarnings("unused")
    private ExperimentReporter() {
	throw new AssertionError("not allowed");
    }

    public ExperimentReporter(Experiment experiment, boolean flushAlways)
	    throws IOException {
	this.experiment = experiment;
	this.flushAlways = flushAlways;

	openFile();
    }

    public void openFile() {
	File file = new File(experiment.getEventFileName());
	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    bufferedWriter = new BufferedWriter(new FileWriter(
		    file.getAbsoluteFile(), true), REPORT_WRITER_BUFFER_SIZE);

	    if (Constants.INCLUDE_EXPERIMENT_META_DATA) {
		writeExperimentMetaData();
	    }

	    writeCSVHeader();
	} catch (Exception e) {
	    D.p("Exception while trying to open experiment output file: "
		    + e.getMessage());
	    e.printStackTrace();
	}

    }

    private void writeExperimentMetaData() throws IOException {

	bufferedWriter
		.write("SERVER,EXPERIMENT,BATCH_ID,START_DATE,WIDTH,HEIGHT,OBSTACLE_DENSITY, RESOURCE_DENSITY,NUM_AGENTS_PER_SPECIES,NUM_COLLECTION_SITES,NUM_STEPS_PER_AGENT,PROBLEM_COMPLEXITY,SPECIES,INTERACTIONS");
	bufferedWriter.newLine();
	bufferedWriter.append(experiment.getServerName());
	sb.append(COMMA);
	bufferedWriter.append(experiment.getName());
	sb.append(COMMA);
	bufferedWriter.append(experiment.getBatchId());
	sb.append(COMMA);
	bufferedWriter.append(experiment.getStartDate().toString());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getGridWidth());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getGridHeight());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getObstacleDensity());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getResourceDensity());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getNumberOfAgentsPerSpecies());
	sb.append(COMMA);

	bufferedWriter.append("" + experiment.getNumberOfCollectionSites());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getMaxStepsPerAgent());
	sb.append(COMMA);
	bufferedWriter.append("" + experiment.getProblemComplexity());
	sb.append(COMMA);

	bufferedWriter.append("" + experiment.getSpeciesComposition());
	sb.append(COMMA);

	bufferedWriter.append("" + experiment.getInteractionMechanisms());
	sb.append(COMMA);

	bufferedWriter.newLine();

    }

    private void writeCSVHeader() throws IOException {

	bufferedWriter
		.write("SIMULATION,AGENT_GENERATION,AGENT_SPECIES,AGENT_ID,STEP,X,Y,EVENT,SRC,DEST");
	bufferedWriter.newLine();

    }

    public void reportEvent(int simulationNumber, int generation,
	    Species species, int agentId, int step, int x, int y, Event event,
	    String source, String destination) {
	try {
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
