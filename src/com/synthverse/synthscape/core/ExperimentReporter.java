package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedAgent;
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

    private static int STRING_BUFFER_MAX_SIZE = 300;

    private final Simulation simulation;
    private BufferedWriter eventWriter = null;
    private BufferedWriter performanceWriter = null;
    private final static char COMMA = ',';
    private StringBuilder sbEvent = new StringBuilder(STRING_BUFFER_MAX_SIZE);
    private StringBuilder sbPerformance = new StringBuilder(STRING_BUFFER_MAX_SIZE);

    private int numberOfSpecies;

    private SummaryStatistics summaryFitnessStats = new SummaryStatistics();

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
	    String eventFileName = constructFileName(settings.DATA_DIR, settings.EVENT_DATA_FILE, settings.JOB_NAME, ""
		    + simulation.seed());
	    openEventFile(eventFileName);
	}

	if (simulation.isReportPerformance()) {
	    String performanceFileName = constructFileName(settings.DATA_DIR, settings.PERFORMANCE_DATA_FILE,
		    settings.JOB_NAME, "" + simulation.seed());
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
	    performanceWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true), FILE_IO_BUFFER_SIZE);

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
	    eventWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true), FILE_IO_BUFFER_SIZE);

	} catch (Exception e) {
	    logger.severe("Exception while trying to open experiment output file: " + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

    private void writeExperimentDetails() {

	settings.EXPERIMENT_DETAILS_FILE = constructFileName(settings.DATA_DIR, settings.EXPERIMENT_DETAILS_FILE_MAIN,
		settings.JOB_NAME, "");

	// if this is a whole set of experiments sharing the same job
	// description
	// then this method will be entered multiple times with different
	// experimentNumber
	// this check protects against it and makes sure it is only run once..
	if (settings.experimentNumber <= 1) {

	    File file = new File(settings.EXPERIMENT_DETAILS_FILE);
	    try {

		if (file.exists() && file.isFile()) {
		    file.delete();
		}
		file.createNewFile();

		BufferedWriter detailWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), false),
			FILE_IO_BUFFER_SIZE);

		if (settings.REPEAT > 1) {
		    detailWriter.write("SEED=" + simulation.seed() + "-" + (simulation.seed() + settings.REPEAT));
		} else {
		    detailWriter.write("SEED=" + simulation.seed());
		}
		detailWriter.newLine();

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

    }

    private final String constructFileName(String dir, String fileName, String job, String id) {
	return dir + File.separator + job + id + "_" + DateUtils.getFileNameDateStamp() + "_" + fileName;

    }

    private void writeExperimentEndDate() {

	File file = new File(settings.EXPERIMENT_DETAILS_FILE);
	try {

	    if (file.exists() && file.isFile()) {
		BufferedWriter detailWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true),
			FILE_IO_BUFFER_SIZE);
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

    private void writePerformanceFieldDescription() {
	try {
	    if (simulation.isReportPerformance()) {
		numberOfSpecies = simulation.speciesComposition.size();

		String columnHeader = "GENERATION, CAPTURES_TOTAL, CAPTURES_BEST_CASE, CAPTURES_MEAN, TOT_FITNESS_MEAN, TOT_FITNESS_VAR";
		for (EventType type : EventType.values()) {
		    if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
			continue;
		    } else {
			columnHeader += ", RATE_" + type;
			columnHeader += ", INTERVAL_" + type;
		    }
		}

		for (Species species : simulation.speciesComposition) {
		    String name = species.toString();

		    if (Main.settings.EVOLUTIONARY_MODEL != EvolutionaryModel.ISLAND_MODEL) {
			// in the island model, all species share the same total
			// fitness...
			columnHeader += ", ";
			columnHeader += name + "_FITNESS_MEAN, ";
			columnHeader += name + "_FITNESS_VAR ";
		    }

		    for (EventType type : EventType.values()) {
			if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
			    continue;
			} else {
			    columnHeader += ", " + name + "_RATE_" + type;
			    columnHeader += ", " + name + "_INTERVAL_" + type;
			}
		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			columnHeader += ", " + name + "_TRAILS_SENT, ";
			columnHeader += name + "_TRAILS_RECEIVED ";
		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			columnHeader += ", " + name + "_SENT_BROADCAST_A, ";
			columnHeader += name + "_RECEIVED_BROADCAST_A, ";
			columnHeader += name + "_SENT_BROADCAST_B, ";
			columnHeader += name + "_RECEIVED_BROADCAST_B";
			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
			    columnHeader += ", " + name + "_SENT_BROADCAST_C, ";
			    columnHeader += name + "_RECEIVED_BROADCAST_C";
			}
		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {
			columnHeader += ", " + name + "_SENT_UNICAST_A_CLOSEST, ";
			columnHeader += name + "_RECEIVED_UNICAST_A_CLOSEST, ";
			columnHeader += name + "_SENT_UNICAST_B_CLOSEST, ";
			columnHeader += name + "_RECEIVED_UNICAST_B_CLOSEST";
			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
			    columnHeader += ", " + name + "_SENT_UNICAST_C_CLOSEST, ";
			    columnHeader += name + "_RECEIVED_UNICAST_C_CLOSEST ";
			}
		    }

		}

		columnHeader += ", RES_D2C_STEPS_MEAN, RES_E2C_STEPS_MEAN, RES_DETECTIONS_MEAN, RES_LOADS_MEAN, RES_UNLOADS_MEAN, RES_MEAN_TOUCHES_PER_SIM";

		performanceWriter.write(columnHeader);
		performanceWriter.newLine();
	    }

	} catch (Exception e) {
	    logger.severe("Exception while reporting performance:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);

	}

    }

    public void reportPerformanceEmbodiedModel(int generationCounter, IntervalStats intervalStats,
	    EventStats generationEventStats, EnumMap<Species, EventStats> speciesEventStatsMap,
	    ArrayList<Agent> agents, SummaryStatistics captureStats, SummaryStatistics populationFitnessStats,
	    long simsRun, ResourceCaptureStats resourceCaptureStats) {
	try {

	    if (simulation.isReportPerformance()) {

		sbPerformance.delete(0, sbPerformance.length());

		sbPerformance.append(generationCounter);

		sbPerformance.append(COMMA);
		sbPerformance.append(captureStats.getSum());

		sbPerformance.append(COMMA);
		sbPerformance.append(captureStats.getMax());

		sbPerformance.append(COMMA);
		sbPerformance.append(captureStats.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(populationFitnessStats.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(populationFitnessStats.getVariance());

		for (EventType type : EventType.values()) {
		    if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
			continue;
		    } else {
			sbPerformance.append(COMMA);
			sbPerformance.append((double) generationEventStats.getTypeValue(type) / simsRun);

			sbPerformance.append(COMMA);

			SummaryStatistics stats = intervalStats.getTypeIntervalStats(type);
			sbPerformance.append((stats != null) ? stats.getMean() : "");

		    }
		}

		// TODO: this loop first iterates over species and then for each
		// species it iterates over all the agents
		// Perhaps a better way to do this is to iterate over all the
		// agents and then hold the sums
		// in a species map...

		for (Species species : simulation.speciesComposition) {

		    summaryFitnessStats.clear();

		    long sentTrail = 0;
		    long receivedTrail = 0;

		    long sentBroadcastA = 0;
		    long receivedBroadcastA = 0;

		    long sentBroadcastB = 0;
		    long receivedBroadcastB = 0;

		    long sentBroadcastC = 0;
		    long receivedBroadcastC = 0;

		    long sentUnicastClosestA = 0;
		    long receivedUnicastClosestA = 0;

		    long sentUnicastClosestB = 0;
		    long receivedUnicastClosestB = 0;

		    long sentUnicastClosestC = 0;
		    long receivedUnicastClosestC = 0;

		    // TODO: this should be the top loop
		    for (Agent agent : agents) {
			EmbodiedAgent embodiedAgent = (EmbodiedAgent) agent;
			if (agent.getSpecies() == species) {

			    for (double fitnessValue : embodiedAgent.fitnessStats.getValues()) {
				summaryFitnessStats.addValue(fitnessValue);
			    }

			    if (simulation.interactionMechanisms.contains(InteractionMechanism.TRAIL)) {

				sentTrail += embodiedAgent.poolGenerationEventStats.getValue(Event.SENT_TRAIL);
				receivedTrail += embodiedAgent.poolGenerationEventStats.getValue(Event.RECEIVED_TRAIL);

			    }

			    if (simulation.interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {

				sentBroadcastA = embodiedAgent.poolGenerationEventStats
					.getValue(Event.SENT_BROADCAST_A);
				receivedBroadcastA = embodiedAgent.poolGenerationEventStats
					.getValue(Event.RECEIVED_BROADCAST_A);

				sentBroadcastB = embodiedAgent.poolGenerationEventStats
					.getValue(Event.SENT_BROADCAST_B);
				receivedBroadcastB = embodiedAgent.poolGenerationEventStats
					.getValue(Event.RECEIVED_BROADCAST_B);

				if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				    sentBroadcastC = embodiedAgent.poolGenerationEventStats
					    .getValue(Event.SENT_BROADCAST_C);
				    receivedBroadcastC = embodiedAgent.poolGenerationEventStats
					    .getValue(Event.RECEIVED_BROADCAST_C);

				}

			    }

			    if (simulation.interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

				sentUnicastClosestA = embodiedAgent.poolGenerationEventStats
					.getValue(Event.SENT_UNICAST_A_CLOSEST);
				receivedUnicastClosestA = embodiedAgent.poolGenerationEventStats
					.getValue(Event.RECEIVED_UNICAST_A_CLOSEST);

				sentUnicastClosestB = embodiedAgent.poolGenerationEventStats
					.getValue(Event.SENT_UNICAST_B_CLOSEST);
				receivedUnicastClosestB = embodiedAgent.poolGenerationEventStats
					.getValue(Event.RECEIVED_UNICAST_B_CLOSEST);

				if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				    sentUnicastClosestC = embodiedAgent.poolGenerationEventStats
					    .getValue(Event.SENT_UNICAST_C_CLOSEST);
				    receivedUnicastClosestC = embodiedAgent.poolGenerationEventStats
					    .getValue(Event.RECEIVED_UNICAST_C_CLOSEST);
				}

			    }

			}

		    }

		    sbPerformance.append(COMMA);
		    sbPerformance.append(summaryFitnessStats.getMean());

		    sbPerformance.append(COMMA);
		    sbPerformance.append(summaryFitnessStats.getVariance());

		    for (EventType type : EventType.values()) {
			if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
			    continue;
			} else {
			    sbPerformance.append(COMMA);
			    sbPerformance.append((double) speciesEventStatsMap.get(species).getTypeValue(type)
				    / simsRun);

			    sbPerformance.append(COMMA);

			    SummaryStatistics stats = intervalStats.getSpeciesEventTypeIntervalStats(species, type);
			    sbPerformance.append((stats != null) ? stats.getMean() : "");

			}
		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.TRAIL)) {

			sbPerformance.append(COMMA);
			sbPerformance.append(sentTrail);
			sbPerformance.append(COMMA);
			sbPerformance.append(receivedTrail);

		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {

			sbPerformance.append(COMMA);
			sbPerformance.append(sentBroadcastA);
			sbPerformance.append(COMMA);
			sbPerformance.append(receivedBroadcastA);

			sbPerformance.append(COMMA);
			sbPerformance.append(sentBroadcastB);
			sbPerformance.append(COMMA);
			sbPerformance.append(receivedBroadcastB);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

			    sbPerformance.append(COMMA);
			    sbPerformance.append(sentBroadcastC);
			    sbPerformance.append(COMMA);
			    sbPerformance.append(receivedBroadcastC);

			}

		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

			sbPerformance.append(COMMA);
			sbPerformance.append(sentUnicastClosestA);
			sbPerformance.append(COMMA);
			sbPerformance.append(receivedUnicastClosestA);

			sbPerformance.append(COMMA);
			sbPerformance.append(sentUnicastClosestB);
			sbPerformance.append(COMMA);
			sbPerformance.append(receivedUnicastClosestB);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
			    sbPerformance.append(COMMA);
			    sbPerformance.append(sentUnicastClosestC);
			    sbPerformance.append(COMMA);
			    sbPerformance.append(receivedUnicastClosestC);
			}

		    }

		    D.p("species=" + species + " fitness=" + summaryFitnessStats.getMean());

		}

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.detectionToCaptureInterval.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.extractionToCaptureInterval.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.detections.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.loads.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.unloads.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.touchedResources.getMean());

		performanceWriter.write(sbPerformance.toString());

		performanceWriter.newLine();
		sbPerformance.delete(0, sbPerformance.length());

		if (this.flushAlways) {
		    performanceWriter.flush();
		}

	    }
	} catch (Exception e) {
	    logger.severe("Exception while reporting performance:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }

    public void reportPerformanceIslandModel(int generationCounter, IntervalStats intervalStats,
	    EventStats generationEventStats, EnumMap<Species, EventStats> speciesEventStatsMap,
	    SummaryStatistics captureStats, SummaryStatistics populationFitnessStats, long simsRun,
	    ResourceCaptureStats resourceCaptureStats) {
	try {
	    if (simulation.isReportPerformance()) {

		sbPerformance.delete(0, sbPerformance.length());

		sbPerformance.append(generationCounter);
		sbPerformance.append(COMMA);

		sbPerformance.append(captureStats.getSum());
		sbPerformance.append(COMMA);

		sbPerformance.append(captureStats.getMax());
		sbPerformance.append(COMMA);

		sbPerformance.append(captureStats.getMean());
		sbPerformance.append(COMMA);

		sbPerformance.append(populationFitnessStats.getMean());
		sbPerformance.append(COMMA);

		sbPerformance.append(populationFitnessStats.getVariance());

		for (EventType type : EventType.values()) {
		    if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
			continue;
		    } else {
			sbPerformance.append(COMMA);
			sbPerformance.append((double) generationEventStats.getTypeValue(type) / simsRun);

			sbPerformance.append(COMMA);

			SummaryStatistics stats = intervalStats.getTypeIntervalStats(type);
			sbPerformance.append((stats != null) ? stats.getMean() : "");

		    }
		}

		for (Species species : simulation.speciesComposition) {

		    for (EventType type : EventType.values()) {
			if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
			    continue;
			} else {
			    sbPerformance.append(COMMA);
			    sbPerformance.append((double) speciesEventStatsMap.get(species).getTypeValue(type)
				    / simsRun);

			    sbPerformance.append(COMMA);
			    SummaryStatistics stats = intervalStats.getSpeciesEventTypeIntervalStats(species, type);
			    sbPerformance.append((stats != null) ? stats.getMean() : "");
			}
		    }

		    EventStats eventStats = speciesEventStatsMap.get(species);
		    long sent = 0;
		    long received = 0;

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			sent = eventStats.getValue(Event.SENT_TRAIL);
			received = eventStats.getValue(Event.RECEIVED_TRAIL);
			sbPerformance.append(COMMA);
			sbPerformance.append(sent);
			sbPerformance.append(COMMA);
			sbPerformance.append(received);

		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {

			sent = eventStats.getValue(Event.SENT_BROADCAST_A);
			received = eventStats.getValue(Event.RECEIVED_BROADCAST_A);
			sbPerformance.append(COMMA);
			sbPerformance.append(sent);
			sbPerformance.append(COMMA);
			sbPerformance.append(received);
			sbPerformance.append(COMMA);

			sent = eventStats.getValue(Event.SENT_BROADCAST_B);
			received = eventStats.getValue(Event.RECEIVED_BROADCAST_B);
			sbPerformance.append(sent);
			sbPerformance.append(COMMA);
			sbPerformance.append(received);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

			    sent = eventStats.getValue(Event.SENT_BROADCAST_C);
			    received = eventStats.getValue(Event.RECEIVED_BROADCAST_C);
			    sbPerformance.append(COMMA);
			    sbPerformance.append(sent);
			    sbPerformance.append(COMMA);
			    sbPerformance.append(received);

			}

		    }

		    if (simulation.interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

			sent = eventStats.getValue(Event.SENT_UNICAST_A_CLOSEST);
			received = eventStats.getValue(Event.RECEIVED_UNICAST_A_CLOSEST);
			sbPerformance.append(COMMA);
			sbPerformance.append(sent);
			sbPerformance.append(COMMA);
			sbPerformance.append(received);
			sbPerformance.append(COMMA);

			sent = eventStats.getValue(Event.SENT_UNICAST_B_CLOSEST);
			received = eventStats.getValue(Event.RECEIVED_UNICAST_B_CLOSEST);
			sbPerformance.append(sent);
			sbPerformance.append(COMMA);
			sbPerformance.append(received);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
			    sent = eventStats.getValue(Event.SENT_UNICAST_C_CLOSEST);
			    received = eventStats.getValue(Event.RECEIVED_UNICAST_C_CLOSEST);
			    sbPerformance.append(COMMA);
			    sbPerformance.append(sent);
			    sbPerformance.append(COMMA);
			    sbPerformance.append(received);

			}

		    }
		}

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.detectionToCaptureInterval.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.extractionToCaptureInterval.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.detections.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.loads.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.unloads.getMean());

		sbPerformance.append(COMMA);
		sbPerformance.append(resourceCaptureStats.touchedResources.getMean());

		performanceWriter.write(sbPerformance.toString());

		performanceWriter.newLine();
		sbPerformance.delete(0, sbPerformance.length());

		if (this.flushAlways) {
		    performanceWriter.flush();
		}

	    }

	} catch (Exception e) {
	    logger.severe("Exception while reporting performance:" + e.getMessage());
	    e.printStackTrace();
	    System.exit(0);
	}

    }
}
