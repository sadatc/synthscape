package com.synthverse.evolutionengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.synthverse.stacks.Config;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.util.CollectionUtils;
import com.synthverse.util.LogUtils;

import ec.util.MersenneTwisterFast;

public final class Evolver implements EvolverConfig {

	private static Logger logger = Logger.getLogger(Evolver.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Level.ALL);
	}

	double averageFitness = 0.0f;
	int generationCounter = 0;
	boolean terminationRequested = false;

	AgentFactory agentFactory = null;

	ArrayList<Agent> activeBuffer = null;
	ArrayList<Agent> parentBuffer = null;
	ArrayList<Agent> offspringBuffer = null;

	ArrayList<Agent> topPerformers = null;
	ArrayList<Agent> bottomPerformers = null;
	HashSet<Long> entityIdDB = null;

	int aTop;
	int aTopXTop;
	int aTopMutants;
	int aTopXBottom;
	int aBottom;
	int aBottomMutants;
	int aBottomXBottom;
	int aRandom;

	int populationSize;
	int maxGenerations;
	double maxMutationRate;
	String evolutionProgressLog;

	/**
	 * creates an evolver with all default values
	 */
	public Evolver(AgentFactory agentFactory) {
		this(agentFactory, DEFAULT_PERCENT_TOP, DEFAULT_PERCENT_TOP_X_TOP,
				DEFAULT_PERCENT_TOP_MUTANT, DEFAULT_PERCENT_TOP_X_BOTTOM,
				DEFAULT_PERCENT_BOTTOM, DEFAULT_PERCENT_BOTTOM_MUTANT,
				DEFAULT_PERCENT_BOTTOM_X_BOTTOM, DEFAULT_PERCENT_RANDOM,
				DEFAULT_MAX_POPULATION_SIZE, DEFAULT_MAX_GENERATIONS,
				DEFAULT_MAX_MUTATION_RATE, DEFAULT_EVOLUTION_PROGRESS_LOG);
	}

	/**
	 * Creates an evolver of population size and all other default values
	 * 
	 * 
	 * @param agentFactory
	 * @param maxPopulationSize
	 */
	public Evolver(AgentFactory agentFactory, int maxPopulationSize) {
		this(agentFactory, DEFAULT_PERCENT_TOP, DEFAULT_PERCENT_TOP_X_TOP,
				DEFAULT_PERCENT_TOP_MUTANT, DEFAULT_PERCENT_TOP_X_BOTTOM,
				DEFAULT_PERCENT_BOTTOM, DEFAULT_PERCENT_BOTTOM_MUTANT,
				DEFAULT_PERCENT_BOTTOM_X_BOTTOM, DEFAULT_PERCENT_RANDOM,
				maxPopulationSize, DEFAULT_MAX_GENERATIONS,
				DEFAULT_MAX_MUTATION_RATE, DEFAULT_EVOLUTION_PROGRESS_LOG);
	}

	/**
	 * Creates an evolver of agents based on the config parameters.
	 * 
	 * @param agentFactory
	 * @param percentTop
	 * @param percentTopXTop
	 * @param percentTopMutant
	 * @param percentTopXBottom
	 * @param percentBottom
	 * @param percentBottomMutant
	 * @param percentBottomXBottom
	 * @param percentRandom
	 * @param maxPopulationSize
	 * @param maxGenerations
	 * @param maxMutationRate
	 * @param evolutionProgressLog
	 */
	public Evolver(AgentFactory agentFactory, double percentTop,
			double percentTopXTop, double percentTopMutant,
			double percentTopXBottom, double percentBottom,
			double percentBottomMutant, double percentBottomXBottom,
			double percentRandom, int maxPopulationSize, int maxGenerations,
			double maxMutationRate, String evolutionProgressLog) {

		this.agentFactory = agentFactory;

		aTop = (int) ((double) maxPopulationSize * percentTop);
		aTopXTop = (int) ((double) maxPopulationSize * percentTopXTop);
		aTopMutants = (int) ((double) maxPopulationSize * percentTopMutant);
		aTopXBottom = (int) ((double) maxPopulationSize * percentTopXBottom);
		aBottom = (int) ((double) maxPopulationSize * percentBottom);
		aBottomMutants = (int) ((double) maxPopulationSize * percentBottomMutant);
		aBottomXBottom = (int) ((double) maxPopulationSize * percentBottomXBottom);
		aRandom = (int) ((double) maxPopulationSize * percentRandom);

		this.populationSize = maxPopulationSize;
		this.maxGenerations = maxGenerations;
		this.maxMutationRate = maxMutationRate;
		this.evolutionProgressLog = evolutionProgressLog;

		setupDataStructures();

	}

	private final void setupDataStructures() {
		// pre-fill all data strcuture
		// we'll be re-using these over and over again

		parentBuffer = new ArrayList<Agent>(populationSize);
		offspringBuffer = new ArrayList<Agent>(populationSize);

		for (int i = 0; i < populationSize; i++) {
			parentBuffer.add(agentFactory.createFactoryAgent().randomizeGenotype());
			offspringBuffer.add(agentFactory.createFactoryAgent().randomizeGenotype());
		}

		topPerformers = new ArrayList<Agent>();
		bottomPerformers = new ArrayList<Agent>();

		entityIdDB = new HashSet<Long>();

		activeBuffer = parentBuffer;
	}



	private final void orderActivePopulationByFitness() {
		logger.finest(">>>");

		Collections.sort(activeBuffer, Collections.reverseOrder());

		computeStats();
		logger.finest("<<<");
	}

	private final void generateMutants(
			MersenneTwisterFast randomNumberGenerator,
			ArrayList<Agent> candidateParents, int numMutants,
			int offspringBufferIndex) {

		for (int i = 0; i < numMutants; i++) {
			Agent parent = CollectionUtils.pickRandomFromList(
					randomNumberGenerator, candidateParents);
			Agent offspring = offspringBuffer.get(offspringBufferIndex);
			GeneticOperator.pointMutate(randomNumberGenerator, parent,
					offspring, maxMutationRate);

			// do filtering, if needed
			if (Config.FILTER_ENTITY_DUPLICATES) {
				int numRetries = 0;
				long offspringId;

				// now make sure we don't have offspring from before
				while (numRetries < Config.MAX_RETRIES_FOR_ENTITY_FILTERS) {
					offspringId = offspring.getId();

					if (entityIdDB.contains(offspringId)) {
						parent = CollectionUtils.pickRandomFromList(
								randomNumberGenerator, candidateParents);
						GeneticOperator.pointMutate(randomNumberGenerator,
								parent, offspring, maxMutationRate);
						numRetries++;
					} else {
						entityIdDB.add(offspringId);
						break;
					}
				}
			}
			offspringBufferIndex++;
		}

	}

	private final void generateCrosses(
			MersenneTwisterFast randomNumberGenerator,
			ArrayList<Agent> candidateAParents,
			ArrayList<Agent> candidateBParents, int numOffsprings,
			int offspringBufferIndex) {

		for (int i = 0; i < numOffsprings; i++) {
			Agent parentA = CollectionUtils.pickRandomFromList(
					randomNumberGenerator, candidateAParents);
			Agent parentB = CollectionUtils.pickRandomFromList(
					randomNumberGenerator, candidateBParents);
			Agent offspring = offspringBuffer.get(offspringBufferIndex);
			GeneticOperator.cross(randomNumberGenerator, parentA, parentB,
					offspring);

			// do filtering, if needed
			if (Config.FILTER_ENTITY_DUPLICATES) {
				int numRetries = 0;
				long offspringId;

				// now make sure we don't have offspring from before
				while (numRetries < Config.MAX_RETRIES_FOR_ENTITY_FILTERS) {
					offspringId = offspring.getId();

					if (entityIdDB.contains(offspringId)) {
						parentA = CollectionUtils.pickRandomFromList(
								randomNumberGenerator, candidateAParents);
						parentB = CollectionUtils.pickRandomFromList(
								randomNumberGenerator, candidateBParents);
						GeneticOperator.cross(randomNumberGenerator, parentA,
								parentB, offspring);

						numRetries++;
					} else {
						entityIdDB.add(offspringId);
						break;
					}
				}
			}
			offspringBufferIndex++;
		}

	}

	private final void generateNew(int numNewEntities, int offspringBufferIndex) {
		for (int i = 0; i < numNewEntities; i++) {
			Agent offspring = offspringBuffer.get(offspringBufferIndex);
			GeneticOperator.randomize(offspring);

			// do filtering, if needed
			if (Config.FILTER_ENTITY_DUPLICATES) {
				int numRetries = 0;
				long offspringId;

				// now make sure we don't have offspring from before
				while (numRetries < Config.MAX_RETRIES_FOR_ENTITY_FILTERS) {
					offspringId = offspring.getId();

					if (entityIdDB.contains(offspringId)) {
						GeneticOperator.randomize(offspring);
						numRetries++;
					} else {
						entityIdDB.add(offspringId);
						break;
					}
				}
			}
			offspringBufferIndex++;
		}

	}

	private void compareBufffers() {
		String msg = "COMPARISON:\n";
		for (int i = 0; i < populationSize; i++) {
			msg += "\t" + activeBuffer.get(i).getFitness() + "\t\t"
					+ offspringBuffer.get(i).getFitness() + "\n";
		}
		logger.fine(msg);

	}

	public final void generateNextGeneration(
			MersenneTwisterFast randomNumberGenerator) {
		logger.finest(">>>");

		// clear up values from previous generation
		topPerformers.clear();
		bottomPerformers.clear();

		for (int i = 0; i < aTop; i++) {
			topPerformers.add(activeBuffer.get(i));
		}

		for (int i = 0; i < aBottom; i++) {
			bottomPerformers.add(activeBuffer.get(populationSize - i - 1));
		}

		// now we start constructing the offspringBuffer
		int offspringBufferIndex = 0;

		// adding tops
		compareBufffers();

		swapBufferElements(activeBuffer, offspringBuffer, 0, aTop,
				offspringBufferIndex);

		offspringBufferIndex += aTop;

		// adding bottoms

		swapBufferElements(activeBuffer, offspringBuffer, populationSize
				- aBottom - 1, aBottom, offspringBufferIndex);
		offspringBufferIndex += aBottom;

		// adding top x top

		generateCrosses(randomNumberGenerator, topPerformers, topPerformers,
				aTopXTop, offspringBufferIndex);
		offspringBufferIndex += aTopXTop;

		// adding top mutants

		generateMutants(randomNumberGenerator, topPerformers, aTopMutants,
				offspringBufferIndex);
		offspringBufferIndex += aTopMutants;

		// adding top x bottom

		generateCrosses(randomNumberGenerator, topPerformers, bottomPerformers,
				aTopXBottom, offspringBufferIndex);
		offspringBufferIndex += aTopXBottom;

		// adding bottom mutants

		generateMutants(randomNumberGenerator, bottomPerformers,
				aBottomMutants, offspringBufferIndex);
		offspringBufferIndex += aBottomMutants;

		// adding bottom x bottom

		generateCrosses(randomNumberGenerator, bottomPerformers,
				bottomPerformers, aBottomXBottom, offspringBufferIndex);
		offspringBufferIndex += aBottomXBottom;

		// add new random programs

		compareBufffers();

		generateNew(aRandom, offspringBufferIndex);

		// switch buffers:
		// offspringBuffer now becomes the new parentBuffer
		// parentBuffer becomes the new offspringBuffer (and ready to contain
		// the new offsprings of the next generation)
		// activeBuffer is pointed to new parentBuffer
		ArrayList<Agent> savedSwapBuffer = parentBuffer;
		parentBuffer = offspringBuffer;
		offspringBuffer = savedSwapBuffer;
		activeBuffer = parentBuffer;

		String msg = "t=" + aTop + ", tXt=" + aTopXTop + ", tM=" + aTopMutants
				+ ", tXb=" + aTopXBottom + ", b=" + aBottom + ", bM="
				+ aBottomMutants + ", bXb=" + aBottomXBottom + ", n=" + aRandom;

		logger.fine("[GENERATION " + generationCounter
				+ "] REGENERATION COMPLETED:" + msg);

		logger.finest("<<<\n");
		
		orderActivePopulationByFitness();

		generationCounter++;

	}

	public ArrayList<Agent> getActiveBuffer() {
		return activeBuffer;
	}

	public ArrayList<Agent> getTopPerformers() {
		return topPerformers;
	}

	public ArrayList<Agent> getBottomPerformers() {
		return bottomPerformers;
	}

	@SuppressWarnings("unused")
	private final void swapBufferElement(ArrayList<Agent> srcBuffer,
			ArrayList<Agent> targetBuffer, int srcIndex, int targetIndex) {
		Agent savedBaseAgent = targetBuffer.get(targetIndex);
		targetBuffer.set(targetIndex, srcBuffer.get(srcIndex));
		srcBuffer.set(srcIndex, savedBaseAgent);
	}

	private final void swapBufferElements(ArrayList<Agent> srcBuffer,
			ArrayList<Agent> targetBuffer, int srcIndex, int numElements,
			int targetIndex) {
		for (int i = 0; i < numElements; i++) {
			Agent savedBaseAgent = targetBuffer.get(targetIndex + i);
			targetBuffer.set(targetIndex + i, srcBuffer.get(srcIndex + i));
			srcBuffer.set(srcIndex + i, savedBaseAgent);
		}
	}

	private final void computeStats() {
		double sum = 0.0f;

		for (Agent e : activeBuffer) {
			sum += e.getFitness();
		}

		this.averageFitness = sum / (double) populationSize;

	}

}
