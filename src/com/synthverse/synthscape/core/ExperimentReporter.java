package com.synthverse.synthscape.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.synthverse.Main;
import com.synthverse.stacks.Program;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedAgent;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandEvolver;
import com.synthverse.util.DateUtils;
import com.synthverse.util.LogUtils;

/**
 * This stores experiment output into physical medium
 * 
 * @author sadat
 * 
 */
public class ExperimentReporter implements Constants {

	private static Logger logger = Logger.getLogger(ExperimentReporter.class
			.getName());

	Settings settings = Settings.getInstance();

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	private static int STRING_BUFFER_MAX_SIZE = 300;

	private final Simulation simulation;
	private BufferedWriter eventWriter = null;
	private BufferedWriter performanceWriter = null;
	private BufferedWriter dnaWriter = null;

	private HashMap<Integer, StringBuilder> poolAlphaMap = new HashMap<Integer, StringBuilder>();

	private final static char COMMA = ',';

	private StringBuilder sbEvent = new StringBuilder(STRING_BUFFER_MAX_SIZE);
	private StringBuilder sbPerformance = new StringBuilder(
			STRING_BUFFER_MAX_SIZE);

	@SuppressWarnings("unused")
	private int numberOfSpecies;

	private SummaryStatistics summaryFitnessStats = new SummaryStatistics();

	private SummaryStatistics summaryAlphaDiffStats = new SummaryStatistics();

	GZIPOutputStream gzOutputStream = null;
	OutputStreamWriter osWriter = null;
	FileOutputStream fileOutputStream = null;

	private final boolean flushAlways;

	@SuppressWarnings("unused")
	private ExperimentReporter() {
		throw new AssertionError("not allowed");
	}

	public ExperimentReporter(Simulation simulation, boolean flushAlways)
			throws IOException {

		this.simulation = simulation;
		this.flushAlways = flushAlways;

	}

	private void openFiles() {
		String seedString = Integer.toString(settings.SEED, 16);

		if (simulation.isReportEvents()) {
			String eventFileName = constructFileName(settings.DATA_DIR,
					settings.EVENT_DATA_FILE, settings.JOB_NAME, seedString);
			eventWriter = openFile(eventFileName);
		}

		if (simulation.isReportPerformance()) {
			String performanceFileName = constructFileName(settings.DATA_DIR,
					settings.PERFORMANCE_DATA_FILE, settings.JOB_NAME,
					seedString);
			performanceWriter = openFile(performanceFileName);
		}

		String dnaProgressionFileName = constructFileName(settings.DATA_DIR,
				settings.DNA_PROGRESSION_FILE, settings.JOB_NAME, seedString);
		dnaWriter = openCompressedFile(dnaProgressionFileName);

	}

	private BufferedWriter openFile(String fileName) {

		BufferedWriter writer = null;

		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();

			} else {

			}
			writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile(),
					true), FILE_IO_BUFFER_SIZE);

		} catch (Exception e) {
			logger.severe("Exception while trying to open experiment output file: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

		return writer;
	}

	private BufferedWriter openCompressedFile(String fileName) {

		BufferedWriter writer = null;

		if (!settings.COMPRESS_DNA_PROGRESSION) {
			fileName = fileName.replace(".gz", ".csv");
		}

		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();

			} else {

			}

			if (settings.COMPRESS_DNA_PROGRESSION) {
				fileOutputStream = new FileOutputStream(fileName, true);
				gzOutputStream = new GZIPOutputStream(fileOutputStream,
						FILE_IO_BUFFER_SIZE);
				osWriter = new OutputStreamWriter(gzOutputStream);
				writer = new BufferedWriter(osWriter);

			} else {
				writer = new BufferedWriter(new FileWriter(
						file.getAbsoluteFile(), true), FILE_IO_BUFFER_SIZE);
			}

		} catch (Exception e) {
			logger.severe("Exception while trying to open experiment output file: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

		return writer;
	}

	private void writeExperimentDetails() {

		settings.EXPERIMENT_DETAILS_FILE = constructFileName(settings.DATA_DIR,
				settings.EXPERIMENT_DETAILS_FILE_MAIN, settings.JOB_NAME,
				EMPTY_STRING);

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

				BufferedWriter detailWriter = new BufferedWriter(
						new FileWriter(file.getAbsoluteFile(), false),
						FILE_IO_BUFFER_SIZE);

				/*
				detailWriter.write("SEED=" + settings.SEED + "-"
						+ (settings.SEED + settings.REPEAT - 1));
				*/
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
				logger.severe("Exception while trying to open experiment details file: "
						+ e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}

	}

	private final String constructFileName(String dir, String fileName,
			String jobName, String simulationSeed) {
		return dir + File.separator + jobName + "_" + simulationSeed + "_"
				+ DateUtils.getFileNameDateStamp() + "_" + fileName;

	}

	private void writeExperimentEndDate() {

		File file = new File(settings.EXPERIMENT_DETAILS_FILE);
		try {

			if (file.exists() && file.isFile()) {
				BufferedWriter detailWriter = new BufferedWriter(
						new FileWriter(file.getAbsoluteFile(), true),
						FILE_IO_BUFFER_SIZE);
				detailWriter.write("EXPERIMENT_END = " + new Date());
				detailWriter.newLine();

				detailWriter.flush();
				detailWriter.close();

			}

		} catch (Exception e) {
			logger.severe("Exception while trying to open experiment details file: "
					+ e.getMessage());
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
				eventWriter
						.append(DateUtils
								.getReportFormattedDateString(simulation
										.getStartDate()));
				eventWriter.append(COMMA);
				eventWriter.append(Integer.toString(simulation.getGridWidth()));
				eventWriter.append(COMMA);
				eventWriter
						.append(Integer.toString(simulation.getGridHeight()));
				eventWriter.append(COMMA);
				eventWriter.append(Double.toString(simulation
						.getObstacleDensity()));
				eventWriter.append(COMMA);
				eventWriter.append(Double.toString(simulation
						.getResourceDensity()));
				eventWriter.append(COMMA);
				eventWriter.append(Integer.toString(simulation
						.getClonesPerSpecies()));
				eventWriter.append(COMMA);
				eventWriter.append(Integer.toString(simulation
						.getGenePoolSize()));
				eventWriter.append(COMMA);

				eventWriter.append(Integer.toString(simulation
						.getNumberOfCollectionSites()));
				eventWriter.append(COMMA);
				eventWriter.append(Integer.toString(simulation
						.getMaxStepsPerAgent()));
				eventWriter.append(COMMA);
				eventWriter.append(Integer.toString(simulation
						.getProblemComplexity().getId()));
				eventWriter.append(COMMA);

				eventWriter.append(simulation.getSpeciesCompositionString());

				eventWriter.append(COMMA);

				eventWriter.append(simulation.getInteractionMechanismsString());

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
				eventWriter
						.write("SIMULATION,GENERATION,SPECIES,ID,STEP,X,Y,EVENT,SRC,DEST");
				eventWriter.newLine();
			}

		} catch (Exception e) {
			logger.severe("Exception while reporting event:" + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		}

	}

	private void writeDnaFieldDescription() {
		try {

			dnaWriter.write("SIMULATION,GENERATION,SPECIES,POOL_ID,GENOTYPE");
			dnaWriter.newLine();

		} catch (Exception e) {
			logger.severe("Exception while reporting dna:" + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		}

	}

	public void reportEvent(long simulationNumber, int generation,
			Species species, int agentId, int step, int x, int y, Event event,
			long source, long destination) {
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

	public void reportAlphaProgram(int generation, int poolId, Species species,
			Program currentAlphaProgram) {

		try {

			StringBuilder alphaCache = getAlphaCacheByPoolId(poolId);

			alphaCache.delete(0, alphaCache.length());
			alphaCache.append(settings.experimentNumber + "," + generation
					+ "," + species + "," + poolId + "," + currentAlphaProgram);

			if (settings.REPORT_DNA_PROGRESSION) {

				dnaWriter.write(alphaCache.toString());
				dnaWriter.newLine();

				if (this.flushAlways) {
					dnaWriter.flush();
				}
			}
		} catch (Exception e) {
			logger.severe("Exception while reporting event:" + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		}

	}

	private StringBuilder getAlphaCacheByPoolId(int poolId) {
		StringBuilder cache = null;
		if (poolAlphaMap.containsKey(poolId)) {
			cache = poolAlphaMap.get(poolId);
		} else {
			cache = new StringBuilder(STRING_BUFFER_MAX_SIZE);
			poolAlphaMap.put(poolId, cache);
		}

		return cache;

	}

	private void closeFiles() {
		try {
			if (eventWriter != null) {
				eventWriter.close();
			}
			if (performanceWriter != null) {
				performanceWriter.close();
			}
			if (dnaWriter != null) {

				// write out the champions...
				if (!settings.REPORT_DNA_PROGRESSION) {

					for (StringBuilder alphaCache : poolAlphaMap.values()) {
						dnaWriter.write(alphaCache.toString());
						dnaWriter.newLine();
					}

					if (this.flushAlways) {
						dnaWriter.flush();
					}
				}

				dnaWriter.close();
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}

				if (gzOutputStream != null) {
					gzOutputStream.close();
				}

				if (osWriter != null) {
					osWriter.close();
				}
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
			writeDnaFieldDescription();
		}
	}

	public String getPoolCompositionString(DescriptiveStatistics fitnessStats) {
		String msg = EMPTY_STRING;

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

				String columnHeader = "GENERATION, SIMS, TDELTA, MEM, CAPTURES_TOTAL, CAPTURES_BEST_CASE, CAPTURES_MEAN, TOT_FITNESS_MEAN, TOT_FITNESS_VAR";
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

					columnHeader += ", ";
					columnHeader += name + "_ALPHA_DIST";

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

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.TRAIL)) {
						columnHeader += ", " + name + "_TRAILS_SENT, ";
						columnHeader += name + "_TRAILS_RECEIVED ";
					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.BROADCAST)) {
						columnHeader += ", " + name + "_SENT_BROADCAST_A, ";
						columnHeader += name + "_RECEIVED_BROADCAST_A, ";
						columnHeader += name + "_SENT_BROADCAST_B, ";
						columnHeader += name + "_RECEIVED_BROADCAST_B";
						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
							columnHeader += ", " + name + "_SENT_BROADCAST_C, ";
							columnHeader += name + "_RECEIVED_BROADCAST_C";
						}
					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {
						columnHeader += ", " + name
								+ "_SENT_UNICAST_A_CLOSEST, ";
						columnHeader += name + "_RECEIVED_UNICAST_A_CLOSEST, ";
						columnHeader += name + "_SENT_UNICAST_B_CLOSEST, ";
						columnHeader += name + "_RECEIVED_UNICAST_B_CLOSEST";
						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
							columnHeader += ", " + name
									+ "_SENT_UNICAST_C_CLOSEST, ";
							columnHeader += name
									+ "_RECEIVED_UNICAST_C_CLOSEST ";
						}
					}

				}

				columnHeader += ", RES_D2C_STEPS_MEAN, RES_E2C_STEPS_MEAN, RES_DETECTIONS_MEAN, RES_LOADS_MEAN, RES_UNLOADS_MEAN, RES_MEAN_TOUCHES_PER_SIM, TOT_TRAIL_SENT, TOT_TRAIL_RECEIVED, TOT_BROADCAST_SENT, TOT_BROADCAST_RECEIVED, TOT_UNICAST_SENT, TOT_UNICAST_RECEIVED";

				performanceWriter.write(columnHeader);
				performanceWriter.newLine();
			}

		} catch (Exception e) {
			logger.severe("Exception while reporting performance:"
					+ e.getMessage());
			e.printStackTrace();
			System.exit(0);

		}

	}

	public void reportPerformanceEmbodiedModel(int generationCounter,
			IntervalStats intervalStats, EventStats generationEventStats,
			EnumMap<Species, EventStats> speciesEventStatsMap,
			ArrayList<Agent> agents, SummaryStatistics captureStats,
			SummaryStatistics populationFitnessStats, long simsRun,
			ResourceCaptureStats resourceCaptureStats, long simCounter,
			long timeDelta, long allocatedMemory) {
		try {

			if (simulation.isReportPerformance()) {

				sbPerformance.delete(0, sbPerformance.length());

				formattedAppend(sbPerformance, generationCounter);

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, simCounter);

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, timeDelta);
				
				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, allocatedMemory);

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, captureStats.getSum());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, captureStats.getMax());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, captureStats.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, populationFitnessStats.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						populationFitnessStats.getVariance());

				for (EventType type : EventType.values()) {
					if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
						continue;
					} else {
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(
								sbPerformance,
								(double) generationEventStats
										.getTypeValue(type) / simsRun);

						formattedAppend(sbPerformance, COMMA);

						SummaryStatistics stats = intervalStats
								.getTypeIntervalStats(type);

						if (stats != null) {
							formattedAppend(sbPerformance, stats.getMean());
						} else {
							formattedAppend(sbPerformance, EMPTY_STRING);
						}

					}
				}

				// TODO: this loop first iterates over species and then for each
				// species it iterates over all the agents
				// Perhaps a better way to do this is to iterate over all the
				// agents and then hold the sums
				// in a species map...

				settings.statusCache = EMPTY_STRING;

				for (Species species : simulation.speciesComposition) {

					summaryFitnessStats.clear();
					summaryAlphaDiffStats.clear();

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

							summaryAlphaDiffStats
									.addValue(embodiedAgent.computedAlphaDistance);

							for (double fitnessValue : embodiedAgent.fitnessStats
									.getValues()) {
								summaryFitnessStats.addValue(fitnessValue);
							}

							if (simulation.interactionMechanisms
									.contains(InteractionMechanism.TRAIL)) {

								sentTrail += embodiedAgent.poolGenerationEventStats
										.getValue(Event.SENT_TRAIL);
								receivedTrail += embodiedAgent.poolGenerationEventStats
										.getValue(Event.RECEIVED_TRAIL);

							}

							if (simulation.interactionMechanisms
									.contains(InteractionMechanism.BROADCAST)) {

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

							if (simulation.interactionMechanisms
									.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

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

					formattedAppend(sbPerformance, COMMA);
					formattedAppend(sbPerformance,
							summaryAlphaDiffStats.getMean());

					formattedAppend(sbPerformance, COMMA);
					formattedAppend(sbPerformance,
							summaryFitnessStats.getMean());

					formattedAppend(sbPerformance, COMMA);
					formattedAppend(sbPerformance,
							summaryFitnessStats.getVariance());

					for (EventType type : EventType.values()) {
						if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
							continue;
						} else {
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance,
									(double) speciesEventStatsMap.get(species)
											.getTypeValue(type) / simsRun);

							formattedAppend(sbPerformance, COMMA);

							SummaryStatistics stats = intervalStats
									.getSpeciesEventTypeIntervalStats(species,
											type);

							if (stats != null) {
								formattedAppend(sbPerformance, stats.getMean());
							} else {
								formattedAppend(sbPerformance, EMPTY_STRING);
							}

						}
					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.TRAIL)) {

						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sentTrail);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, receivedTrail);

					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.BROADCAST)) {

						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sentBroadcastA);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, receivedBroadcastA);

						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sentBroadcastB);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, receivedBroadcastB);

						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, sentBroadcastC);
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, receivedBroadcastC);

						}

					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sentUnicastClosestA);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, receivedUnicastClosestA);

						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sentUnicastClosestB);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, receivedUnicastClosestB);

						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, sentUnicastClosestC);
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance,
									receivedUnicastClosestC);
						}
						D.p("XXX:" + species + ":A:" + sentUnicastClosestA
								+ ":" + receivedUnicastClosestA + "--B:"
								+ sentUnicastClosestB + ":"
								+ receivedUnicastClosestB);
					}

					settings.statusCache += species + ":"
							+ summaryFitnessStats.getMean() + " ";

				}

				formattedAppend(sbPerformance, COMMA);
				sbPerformance
						.append(resourceCaptureStats.detectionToCaptureInterval
								.getMean());

				formattedAppend(sbPerformance, COMMA);
				sbPerformance
						.append(resourceCaptureStats.extractionToCaptureInterval
								.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.detections.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.loads.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.unloads.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.touchedResources.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, Trail.getCounter());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, Trail.getUsed());
				Trail.resetSendReceiveCounters();

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, Broadcast.getCounter());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, Broadcast.getUsed());
				Broadcast.resetSendReceiveCounters();

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, Unicast.getCounter());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, Unicast.getUsed());
				Unicast.resetSendReceiveCounters();

				performanceWriter.write(sbPerformance.toString());

				performanceWriter.newLine();
				sbPerformance.delete(0, sbPerformance.length());

				if (this.flushAlways) {
					performanceWriter.flush();
				}

			}
		} catch (Exception e) {
			logger.severe("Exception while reporting performance:"
					+ e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

	}

	public final void formattedAppend(StringBuilder sb, char value) {
		sb.append(value);
	}

	public final void formattedAppend(StringBuilder sb, int value) {
		sb.append(value);
	}

	public final void formattedAppend(StringBuilder sb, long value) {
		sb.append(value);
	}

	public final void formattedAppend(StringBuilder sb, String value) {
		sb.append(value);
	}

	public final void formattedAppend(StringBuilder sb, double value) {
		// sb.append(value);
		sb.append(String.format(Constants.CSV_DOUBLE_FORMAT, value));
	}

	public void reportPerformanceIslandModel(int generationCounter,
			IntervalStats intervalStats, EventStats generationEventStats,
			EnumMap<Species, EventStats> speciesEventStatsMap,
			SummaryStatistics captureStats,
			SummaryStatistics populationFitnessStats, long simsRun,
			ResourceCaptureStats resourceCaptureStats,
			LinkedHashMap<Species, PopulationIslandEvolver> speciesIslandMap,
			long simCounter, long timeDelta, long allocatedMemory) {
		try {
			if (simulation.isReportPerformance()) {

				sbPerformance.delete(0, sbPerformance.length());

				formattedAppend(sbPerformance, generationCounter);
				formattedAppend(sbPerformance, COMMA);

				formattedAppend(sbPerformance, simCounter);
				formattedAppend(sbPerformance, COMMA);
				
				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, timeDelta);
				
				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance, allocatedMemory);

				formattedAppend(sbPerformance, captureStats.getSum());
				formattedAppend(sbPerformance, COMMA);

				formattedAppend(sbPerformance, captureStats.getMax());
				formattedAppend(sbPerformance, COMMA);

				formattedAppend(sbPerformance, captureStats.getMean());
				formattedAppend(sbPerformance, COMMA);

				formattedAppend(sbPerformance, populationFitnessStats.getMean());
				formattedAppend(sbPerformance, COMMA);

				formattedAppend(sbPerformance,
						populationFitnessStats.getVariance());

				for (EventType type : EventType.values()) {
					if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
						continue;
					} else {
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(
								sbPerformance,
								(double) generationEventStats
										.getTypeValue(type) / simsRun);

						formattedAppend(sbPerformance, COMMA);

						SummaryStatistics stats = intervalStats
								.getTypeIntervalStats(type);

						if (stats != null) {
							formattedAppend(sbPerformance, stats.getMean());
						} else {
							formattedAppend(sbPerformance, EMPTY_STRING);
						}

					}
				}

				for (Species species : simulation.speciesComposition) {

					PopulationIslandEvolver evolver = speciesIslandMap
							.get(species);
					formattedAppend(sbPerformance, COMMA);
					sbPerformance
							.append((double) evolver.computedAlphaDistance);

					for (EventType type : EventType.values()) {
						if ((Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS && type == EventType.PROCESSING)) {
							continue;
						} else {
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance,
									(double) speciesEventStatsMap.get(species)
											.getTypeValue(type) / simsRun);

							formattedAppend(sbPerformance, COMMA);
							SummaryStatistics stats = intervalStats
									.getSpeciesEventTypeIntervalStats(species,
											type);

							if (stats != null) {
								formattedAppend(sbPerformance, stats.getMean());
							} else {
								formattedAppend(sbPerformance, EMPTY_STRING);
							}

						}
					}

					EventStats eventStats = speciesEventStatsMap.get(species);
					long sent = 0;
					long received = 0;

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.TRAIL)) {
						sent = eventStats.getValue(Event.SENT_TRAIL);
						received = eventStats.getValue(Event.RECEIVED_TRAIL);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sent);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, received);

					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.BROADCAST)) {

						sent = eventStats.getValue(Event.SENT_BROADCAST_A);
						received = eventStats
								.getValue(Event.RECEIVED_BROADCAST_A);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sent);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, received);
						formattedAppend(sbPerformance, COMMA);

						sent = eventStats.getValue(Event.SENT_BROADCAST_B);
						received = eventStats
								.getValue(Event.RECEIVED_BROADCAST_B);
						formattedAppend(sbPerformance, sent);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, received);

						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

							sent = eventStats.getValue(Event.SENT_BROADCAST_C);
							received = eventStats
									.getValue(Event.RECEIVED_BROADCAST_C);
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, sent);
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, received);

						}

					}

					if (simulation.interactionMechanisms
							.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

						sent = eventStats
								.getValue(Event.SENT_UNICAST_A_CLOSEST);
						received = eventStats
								.getValue(Event.RECEIVED_UNICAST_A_CLOSEST);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, sent);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, received);
						formattedAppend(sbPerformance, COMMA);

						sent = eventStats
								.getValue(Event.SENT_UNICAST_B_CLOSEST);
						received = eventStats
								.getValue(Event.RECEIVED_UNICAST_B_CLOSEST);
						formattedAppend(sbPerformance, sent);
						formattedAppend(sbPerformance, COMMA);
						formattedAppend(sbPerformance, received);

						if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
							sent = eventStats
									.getValue(Event.SENT_UNICAST_C_CLOSEST);
							received = eventStats
									.getValue(Event.RECEIVED_UNICAST_C_CLOSEST);
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, sent);
							formattedAppend(sbPerformance, COMMA);
							formattedAppend(sbPerformance, received);

						}

					}
				}

				formattedAppend(sbPerformance, COMMA);
				sbPerformance
						.append(resourceCaptureStats.detectionToCaptureInterval
								.getMean());

				formattedAppend(sbPerformance, COMMA);
				sbPerformance
						.append(resourceCaptureStats.extractionToCaptureInterval
								.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.detections.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.loads.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.unloads.getMean());

				formattedAppend(sbPerformance, COMMA);
				formattedAppend(sbPerformance,
						resourceCaptureStats.touchedResources.getMean());

				performanceWriter.write(sbPerformance.toString());

				performanceWriter.newLine();
				sbPerformance.delete(0, sbPerformance.length());

				if (this.flushAlways) {
					performanceWriter.flush();
				}

			}

		} catch (Exception e) {
			logger.severe("Exception while reporting performance:"
					+ e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}

	}
}
