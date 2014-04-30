package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.synthverse.Main;
import com.synthverse.util.DateUtils;
import com.synthverse.util.LogUtils;

/**
 * This stores experiment output into physical medium
 * 
 * @author sadat
 * 
 */
public class ExperimentReporter implements Constants {

    private static Logger logger = Logger.getLogger(ExperimentReporter.class.getName());

    Settings settings = Settings.getInstance();

    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    private static int STRING_BUFFER_MAX_SIZE = 175;

    private final Simulation simulation;
    private BufferedWriter eventWriter = null;
    private BufferedWriter performanceWriter = null;
    private final static char COMMA = ',';
    private StringBuilder sbEvent = new StringBuilder(STRING_BUFFER_MAX_SIZE);
    private StringBuilder sbPerformance = new StringBuilder(STRING_BUFFER_MAX_SIZE);

    private final boolean flushAlways;

    @SuppressWarnings("unused")
    private ExperimentReporter() {
	throw new AssertionError("not allowed");
    }

    public ExperimentReporter(Simulation simulation, boolean flushAlways) throws IOException {

	this.simulation = simulation;
	this.flushAlways = flushAlways;

    }

    private void openFiles() {

	if (simulation.isReportEvents()) {
	    String eventFileName = simulation.getEventFileName();
	    openEventFile(eventFileName);
	}

	if (simulation.isReportPerformance()) {
	    String performanceFileName = simulation.getEventFileName().replace("event", "performance");
	    openPerformanceFile(performanceFileName);
	}

    }

    private void openPerformanceFile(String fileName) {

	File file = new File(fileName);
	try {
	    if (!file.exists()) {
		file.createNewFile();

	    } else {

	    }
	    performanceWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true),
		    REPORT_WRITER_BUFFER_SIZE);

	} catch (Exception e) {
	    logger.severe("Exception while trying to open experiment output file: " + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

    private void openEventFile(String fileName) {
	File file = new File(fileName);
	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    eventWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true), REPORT_WRITER_BUFFER_SIZE);

	} catch (Exception e) {
	    logger.severe("Exception while trying to open experiment output file: " + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

    private void writeExperimentDetails() {

	File file = new File(settings.EXPERIMENT_DETAILS_FILE);
	try {

	    if (file.exists() && file.isFile()) {
		file.delete();
	    }
	    file.createNewFile();

	    BufferedWriter detailWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), false),
		    REPORT_WRITER_BUFFER_SIZE);

	    for (String line : settings.EXPERIMENT_DETAILS) {
		detailWriter.write(line);
		detailWriter.newLine();
	    }
	    detailWriter.write("EXPERIMENT_START = " + new Date());
	    detailWriter.newLine();

	    detailWriter.flush();
	    detailWriter.close();

	} catch (Exception e) {
	    logger.severe("Exception while trying to open experiment details file: " + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

    private void writeExperimentEndDate() {

	File file = new File(settings.EXPERIMENT_DETAILS_FILE);
	try {

	    if (file.exists() && file.isFile()) {
		BufferedWriter detailWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true),
			REPORT_WRITER_BUFFER_SIZE);
		detailWriter.write("EXPERIMENT_END = " + new Date());
		detailWriter.newLine();

		detailWriter.flush();
		detailWriter.close();

	    }

	} catch (Exception e) {
	    logger.severe("Exception while trying to open experiment details file: " + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

    private void writeExperimentMetaData() {

	try {
	    if (simulation.isReportEvents()) {
		eventWriter
			.write("SERVER,EXPERIMENT,BATCH_ID,START_DATE,WIDTH,HEIGHT,OBSTACLE_DENSITY,RESOURCE_DENSITY,AGENTS_PER_SPECIES,GENE_POOL,COLLECTION_SITES,MAX_STEPS,PROBLEM_COMPLEXITY,SPECIES,INTERACTIONS");
		eventWriter.newLine();

		eventWriter.append(simulation.getServerName());
		eventWriter.append(COMMA);
		eventWriter.append(simulation.getExperimentName());
		eventWriter.append(COMMA);
		eventWriter.append(simulation.getBatchId());
		eventWriter.append(COMMA);
		eventWriter.append(DateUtils.getReportFormattedDateString(simulation.getStartDate()));
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getGridWidth());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getGridHeight());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getObstacleDensity());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getResourceDensity());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getClonesPerSpecies());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getGenePoolSize());
		eventWriter.append(COMMA);

		eventWriter.append("" + simulation.getNumberOfCollectionSites());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getMaxStepsPerAgent());
		eventWriter.append(COMMA);
		eventWriter.append("" + simulation.getProblemComplexity().getId());
		eventWriter.append(COMMA);

		eventWriter.append("" + simulation.getSpeciesCompositionSting());

		eventWriter.append(COMMA);

		eventWriter.append("" + simulation.getInteractionMechanismsString());

		eventWriter.newLine();
		eventWriter.newLine();
	    }
	} catch (Exception e) {
	    logger.info("Exception while reporting event:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);

	}

    }

    private void writeEventFieldDescription() {
	try {
	    if (simulation.isReportEvents()) {
		eventWriter.write("SIMULATION,GENERATION,SPECIES,ID,STEP,X,Y,EVENT,SRC,DEST");
		eventWriter.newLine();
	    }

	} catch (Exception e) {
	    logger.severe("Exception while reporting event:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);

	}

    }

    public void reportEvent(long simulationNumber, int generation, Species species, int agentId, int step, int x,
	    int y, Event event, String source, String destination) {
	try {
	    if (simulation.isReportEvents()) {
		sbEvent.append(simulationNumber);
		sbEvent.append(COMMA);
		sbEvent.append(generation);
		sbEvent.append(COMMA);
		sbEvent.append(species.getId());
		sbEvent.append(COMMA);
		sbEvent.append(agentId);
		sbEvent.append(COMMA);
		sbEvent.append(step);
		sbEvent.append(COMMA);
		sbEvent.append(x);
		sbEvent.append(COMMA);
		sbEvent.append(y);
		sbEvent.append(COMMA);
		sbEvent.append(event.toString());
		sbEvent.append(COMMA);
		sbEvent.append(source);
		sbEvent.append(COMMA);
		sbEvent.append(destination);

		eventWriter.write(sbEvent.toString());
		eventWriter.newLine();
		sbEvent.delete(0, sbEvent.length());

		if (this.flushAlways) {
		    eventWriter.flush();
		}
	    }
	} catch (Exception e) {
	    logger.severe("Exception while reporting event:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);

	}

    }

    private void closeFiles() {
	try {
	    if (eventWriter != null) {
		eventWriter.close();
	    }
	    if (performanceWriter != null) {
		performanceWriter.close();
	    }

	} catch (Exception e) {
	    logger.info("Exception while closing:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}
    }

    public void cleanupReporter() {
	if (simulation.isReportEvents() || simulation.isReportPerformance()) {
	    closeFiles();
	}

	writeExperimentEndDate();
    }

    public void initReporter() {

	writeExperimentDetails();

	if (simulation.isReportEvents() || simulation.isReportPerformance()) {

	    openFiles();
	    if (Constants.INCLUDE_EXPERIMENT_META_DATA) {
		writeExperimentMetaData();
	    }
	    writeEventFieldDescription();
	    writePerformanceFieldDescription();
	}
    }

    private void writePerformanceFieldDescription() {
	try {
	    if (simulation.isReportPerformance()) {
		performanceWriter
			.write("POPULATION,GENERATION,FITNESS_MEAN,FITNESS_VAR,FITNESS_MIN,FITNESS_MAX,CAPTURES,TRAIL_SENT,TRAIL_RECEIVED,TRAIL_SEARCHED,POOL_COMPOSITION");
		performanceWriter.newLine();
	    }

	} catch (Exception e) {
	    logger.severe("Exception while reporting performance:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);

	}

    }

    public String getPoolCompositionString(DescriptiveStatistics fitnessStats) {
	String msg = "";

	double[] fitnessValues = fitnessStats.getSortedValues();

	int fitnessBin = (int) fitnessValues[fitnessValues.length - 1];

	int binCount = 1;

	for (int i = fitnessValues.length - 2; i >= 0; i--) {

	    int currentAgentFitness = (int) fitnessValues[i];
	    if (currentAgentFitness == fitnessBin) {
		binCount++;
	    } else {
		msg += fitnessBin + ":" + binCount + " ";
		// reseta
		binCount = 1;
		fitnessBin = currentAgentFitness;
	    }
	}
	msg += fitnessBin + ":" + binCount + " ";
	return msg;
    }

    public void reportPerformanceIslandModel(int generationCounter, Stats simStats, Stats poolStats,
	    DescriptiveStatistics fitnessStats) {
	try {

	    int captures = poolStats.getValue(Event.COLLECTED_RESOURCE);
	    settings.lastReportedCaptures = captures;

	    if (settings.lastReportedGeneration == generationCounter) {
		return;
	    }

	    int trailSent = poolStats.getValue(Event.SEARCHED_GENERIC_TRAIL);
	    int trailReceived = poolStats.getValue(Event.RECEIVED_GENERIC_TRAIL);
	    int trailSearched = poolStats.getValue(Event.SEARCHED_GENERIC_TRAIL);

	    poolStats.clear();

	    String poolComposition = getPoolCompositionString(fitnessStats);

	    if (simulation.isReportPerformance()) {

		sbPerformance.delete(0, sbPerformance.length());
		sbPerformance.append(fitnessStats.getN());
		sbPerformance.append(COMMA);
		sbPerformance.append(generationCounter);
		sbPerformance.append(COMMA);
		sbPerformance.append(fitnessStats.getMean());
		sbPerformance.append(COMMA);

		sbPerformance.append(fitnessStats.getVariance());
		sbPerformance.append(COMMA);

		sbPerformance.append(fitnessStats.getMin());
		sbPerformance.append(COMMA);

		sbPerformance.append(fitnessStats.getMax());
		sbPerformance.append(COMMA);

		sbPerformance.append(captures);
		sbPerformance.append(COMMA);

		sbPerformance.append(trailSent);
		sbPerformance.append(COMMA);

		sbPerformance.append(trailReceived);
		sbPerformance.append(COMMA);

		sbPerformance.append(trailSearched);
		sbPerformance.append(COMMA);

		sbPerformance.append(poolComposition);


		performanceWriter.write(sbPerformance.toString());

		performanceWriter.newLine();
		sbPerformance.delete(0, sbPerformance.length());

		if (this.flushAlways) {
		    performanceWriter.flush();
		}
		settings.lastReportedGeneration = generationCounter;
	    }
	} catch (Exception e) {
	    logger.severe("Exception while reporting performance:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

/*    
    public void reportPerformance(int generationCounter, Stats simStats, Stats poolStats,
	    DescriptiveStatistics fitnessStats) {
	try {

	    int captures = poolStats.getValue(Event.COLLECTED_RESOURCE);
	    settings.lastReportedCaptures = captures;

	    if (settings.lastReportedGeneration == generationCounter) {
		return;
	    }

	    int trailSent = poolStats.getValue(Event.SEARCHED_GENERIC_TRAIL);
	    int trailReceived = poolStats.getValue(Event.RECEIVED_GENERIC_TRAIL);
	    int trailSearched = poolStats.getValue(Event.SEARCHED_GENERIC_TRAIL);

	    poolStats.clear();

	    String poolComposition = getPoolCompositionString(fitnessStats);

	    if (simulation.isReportPerformance()) {

		sbPerformance.delete(0, sbPerformance.length());
		sbPerformance.append(fitnessStats.getN());
		sbPerformance.append(COMMA);
		sbPerformance.append(generationCounter);
		sbPerformance.append(COMMA);
		sbPerformance.append(fitnessStats.getMean());
		sbPerformance.append(COMMA);

		sbPerformance.append(fitnessStats.getVariance());
		sbPerformance.append(COMMA);

		sbPerformance.append(fitnessStats.getMin());
		sbPerformance.append(COMMA);

		sbPerformance.append(fitnessStats.getMax());
		sbPerformance.append(COMMA);

		sbPerformance.append(captures);
		sbPerformance.append(COMMA);

		sbPerformance.append(trailSent);
		sbPerformance.append(COMMA);

		sbPerformance.append(trailReceived);
		sbPerformance.append(COMMA);

		sbPerformance.append(trailSearched);
		sbPerformance.append(COMMA);

		sbPerformance.append(poolComposition);


		performanceWriter.write(sbPerformance.toString());

		performanceWriter.newLine();
		sbPerformance.delete(0, sbPerformance.length());

		if (this.flushAlways) {
		    performanceWriter.flush();
		}
		settings.lastReportedGeneration = generationCounter;
	    }
	} catch (Exception e) {
	    logger.severe("Exception while reporting performance:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }
*/
    
}
