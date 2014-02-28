package com.synthverse.evolver.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.synthverse.stacks.Config;
import com.synthverse.stacks.Program;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Species;
import com.synthverse.util.CollectionUtils;
import com.synthverse.util.LogUtils;

import ec.util.MersenneTwisterFast;

public final class CentralizedEvolutionEngine implements Constants {

    private static Logger logger = Logger.getLogger(CentralizedEvolutionEngine.class.getName());
    private DescriptiveStatistics fitnessStats = new DescriptiveStatistics();

    static {
	LogUtils.applyDefaultSettings(logger, Level.ALL);
    }

    private Species species;
    double averageFitness = 0.0f;
    int generationCounter = 0;
    boolean terminationRequested = false;

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

    int genePoolSize;

    double maxMutationRate;
    String evolutionProgressLog;
    private AgentFactory agentFactory;

    /**
     * creates an evolver with all default values
     * 
     * @throws Exception
     */
    public CentralizedEvolutionEngine(AgentFactory agentFactory, Species species) throws Exception {
	this(agentFactory, species, EE_DEF_PERCENT_TOP, EE_DEF_PERCENT_TOP_X_TOP, EE_DEF_PERCENT_TOP_MUTANT,
		EE_DEF_PERCENT_TOP_X_BOTTOM, EE_DEF_PERCENT_BOTTOM, EE_DEF_PERCENT_BOTTOM_MUTANT,
		EE_DEF_PERCENT_BOTTOM_X_BOTTOM, EE_DEF_PERCENT_RANDOM, EE_DEF_GENE_POOL_SIZE, EE_DEF_MAX_MUTATION_RATE,
		EE_DEF_EVOLUTION_PROGRESS_LOG);
    }

    /**
     * Creates an evolver of population size and all other default values
     * 
     * 
     * @param agentFactory
     * @param genePoolSize
     * @throws Exception
     */
    public CentralizedEvolutionEngine(AgentFactory agentFactory, Species species, int genePoolSize) throws Exception {
	this(agentFactory, species, EE_DEF_PERCENT_TOP, EE_DEF_PERCENT_TOP_X_TOP, EE_DEF_PERCENT_TOP_MUTANT,
		EE_DEF_PERCENT_TOP_X_BOTTOM, EE_DEF_PERCENT_BOTTOM, EE_DEF_PERCENT_BOTTOM_MUTANT,
		EE_DEF_PERCENT_BOTTOM_X_BOTTOM, EE_DEF_PERCENT_RANDOM, genePoolSize, EE_DEF_MAX_MUTATION_RATE,
		EE_DEF_EVOLUTION_PROGRESS_LOG);
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
     * @param genePoolSize
     * @param maxMutationRate
     * @param evolutionProgressLog
     * @throws Exception
     */
    public CentralizedEvolutionEngine(AgentFactory agentFactory, Species species, double percentTop,
	    double percentTopXTop, double percentTopMutant, double percentTopXBottom, double percentBottom,
	    double percentBottomMutant, double percentBottomXBottom, double percentRandom, int genePoolSize,
	    double maxMutationRate, String evolutionProgressLog) throws Exception {

	this.agentFactory = agentFactory;
	this.species = species;

	aTop = (int) ((double) genePoolSize * percentTop);
	if (aTop <= 0) {
	    throw new Exception("aTop is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aTopXTop = (int) ((double) genePoolSize * percentTopXTop);
	if (aTopXTop <= 0) {
	    throw new Exception("aTopXTop is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aTopMutants = (int) ((double) genePoolSize * percentTopMutant);
	if (aTopMutants <= 0) {
	    throw new Exception("aTopMutants is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aTopXBottom = (int) ((double) genePoolSize * percentTopXBottom);
	if (aTopXBottom <= 0) {
	    throw new Exception("aTopXBottom is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aBottom = (int) ((double) genePoolSize * percentBottom);
	if (aBottom <= 0) {
	    throw new Exception("aBottom is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aBottomMutants = (int) ((double) genePoolSize * percentBottomMutant);
	if (aBottomMutants <= 0) {
	    throw new Exception("aBottomMutants is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aBottomXBottom = (int) ((double) genePoolSize * percentBottomXBottom);
	if (aBottomXBottom <= 0) {
	    throw new Exception("aBottomXBottom is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}
	aRandom = (int) ((double) genePoolSize * percentRandom);
	if (aRandom <= 0) {
	    throw new Exception("aRandom is too low. Most likely, genePool=" + genePoolSize + ", is too small");
	}

	this.genePoolSize = genePoolSize;

	this.maxMutationRate = maxMutationRate;
	this.evolutionProgressLog = evolutionProgressLog;

	initDataStructures();

    }

    private final void initDataStructures() {
	// pre-fill all data strcuture
	// we'll be re-using these over and over again

	parentBuffer = new ArrayList<Agent>(genePoolSize);
	offspringBuffer = new ArrayList<Agent>(genePoolSize);

	logger.info(">>> started creating agent pool...");
	logger.info("pre-filling parentBuffer genepool with " + genePoolSize + " agents...");
	for (int i = 0; i < genePoolSize; i++) {
	    parentBuffer.add(agentFactory.getNewFactoryAgent(species));
	}

	logger.info("pre-filling offspringBuffer genepool with " + genePoolSize + " agents...");
	for (int i = 0; i < genePoolSize; i++) {
	    offspringBuffer.add(agentFactory.getNewFactoryAgent(species));
	}

	logger.info("<<< finished creating agent pool.\n");
	topPerformers = new ArrayList<Agent>();
	bottomPerformers = new ArrayList<Agent>();

	entityIdDB = new HashSet<Long>();

	activeBuffer = parentBuffer;

	// printActiveBuffer();
    }

    private final void orderActivePopulationByFitness() {

	Collections.sort(activeBuffer, Collections.reverseOrder());

	computeStats();

    }

    private final void generateMutants(MersenneTwisterFast randomNumberGenerator, ArrayList<Agent> candidateParents,
	    int numMutants, int offspringBufferIndex) {

	for (int i = 0; i < numMutants; i++) {
	    Agent parent = CollectionUtils.pickRandomFromList(randomNumberGenerator, candidateParents);
	    Agent offspring = offspringBuffer.get(offspringBufferIndex);
	    
	    //offspring.setFitness(0);
	    //offspring.setAccumulatedMaxFitness(0);
	    
	    GeneticOperator.pointMutate(randomNumberGenerator, parent, offspring, maxMutationRate);

	    // do filtering, if needed
	    if (Config.FILTER_ENTITY_DUPLICATES) {
		int numRetries = 0;
		long offspringId;

		// now make sure we don't have offspring from before
		while (numRetries < Config.MAX_RETRIES_FOR_ENTITY_FILTERS) {
		    offspringId = offspring.getId();

		    if (entityIdDB.contains(offspringId)) {
			parent = CollectionUtils.pickRandomFromList(randomNumberGenerator, candidateParents);
			GeneticOperator.pointMutate(randomNumberGenerator, parent, offspring, maxMutationRate);
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

    private final void generateCrosses(MersenneTwisterFast randomNumberGenerator, ArrayList<Agent> candidateAParents,
	    ArrayList<Agent> candidateBParents, int numOffsprings, int offspringBufferIndex) {

	for (int i = 0; i < numOffsprings; i++) {
	    Agent parentA = CollectionUtils.pickRandomFromList(randomNumberGenerator, candidateAParents);
	    Agent parentB = CollectionUtils.pickRandomFromList(randomNumberGenerator, candidateBParents);
	    Agent offspring = offspringBuffer.get(offspringBufferIndex);

	    //offspring.setAccumulatedMaxFitness(0);
	    //offspring.setFitness(0);
	    
	    GeneticOperator.cross(randomNumberGenerator, parentA, parentB, offspring);

	    // do filtering, if needed
	    if (Config.FILTER_ENTITY_DUPLICATES) {
		int numRetries = 0;
		long offspringId;

		// now make sure we don't have offspring from before
		while (numRetries < Config.MAX_RETRIES_FOR_ENTITY_FILTERS) {
		    offspringId = offspring.getId();

		    if (entityIdDB.contains(offspringId)) {
			parentA = CollectionUtils.pickRandomFromList(randomNumberGenerator, candidateAParents);
			parentB = CollectionUtils.pickRandomFromList(randomNumberGenerator, candidateBParents);
			GeneticOperator.cross(randomNumberGenerator, parentA, parentB, offspring);

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
	    //offspring.setFitness(0);
	    //offspring.setAccumulatedMaxFitness(0);
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

    private void reportPerformance() {
	fitnessStats.clear();
	for (int i = 0; i < genePoolSize; i++) {
	    fitnessStats.addValue(activeBuffer.get(i).getFitness());
	}
	agentFactory.getSimulation().reportPerformance(generationCounter, fitnessStats);

	/*
	 * logger.fine("generation="+generationCounter+"; n=" +
	 * fitnessStats.getN() + "; max=" + fitnessStats.getMax() + "; mean=" +
	 * fitnessStats.getMean() + "; var=" + fitnessStats.getVariance());
	 */

    }

    private void compareBuffers() {
	String msg = "COMPARISON:\n";
	for (int i = 0; i < genePoolSize; i++) {
	    msg += "\t" + activeBuffer.get(i).getFitness() + "\t\t" + offspringBuffer.get(i).getFitness() + "\n";
	}
	logger.fine(msg);

    }

    private void printActiveBuffer() {
	logger.info("==========ACTIVE BUFFER =============");
	for (int i = 0; i< activeBuffer.size(); i++) {
	    Agent agent = activeBuffer.get(i);
	    Program p = agent.getProgram();
	    logger.info("--->" + agent.getAgentId() + ":" + agent.getFitness() + ":" + p.getFingerPrint());
	}
	logger.info("=====================================");

    }

    public final void generateNextGeneration(MersenneTwisterFast randomNumberGenerator) {

	printActiveBuffer();
	//logger.info("about to generate the next generation...");
	//logger.info("reporting on previous generation...");
	reportPerformance();
	//logger.info("done reporting...");

	// clear up values from previous generation
	topPerformers.clear();
	bottomPerformers.clear();

	for (int i = 0; i < aTop; i++) {
	    topPerformers.add(activeBuffer.get(i));
	}

	for (int i = 0; i < aBottom; i++) {
	    bottomPerformers.add(activeBuffer.get(genePoolSize - i - 1));
	}

	// now we start constructing the offspringBuffer
	int offspringBufferIndex = 0;

	// adding tops
	// compareBuffers();

	swapBufferElements(activeBuffer, offspringBuffer, 0, aTop, offspringBufferIndex);

	offspringBufferIndex += aTop;

	// adding bottoms

	swapBufferElements(activeBuffer, offspringBuffer, genePoolSize - aBottom - 1, aBottom, offspringBufferIndex);
	offspringBufferIndex += aBottom;

	// adding top x top

	generateCrosses(randomNumberGenerator, topPerformers, topPerformers, aTopXTop, offspringBufferIndex);
	offspringBufferIndex += aTopXTop;

	// adding top mutants

	generateMutants(randomNumberGenerator, topPerformers, aTopMutants, offspringBufferIndex);
	offspringBufferIndex += aTopMutants;

	// adding top x bottom

	generateCrosses(randomNumberGenerator, topPerformers, bottomPerformers, aTopXBottom, offspringBufferIndex);
	offspringBufferIndex += aTopXBottom;

	// adding bottom mutants

	generateMutants(randomNumberGenerator, bottomPerformers, aBottomMutants, offspringBufferIndex);
	offspringBufferIndex += aBottomMutants;

	// adding bottom x bottom

	generateCrosses(randomNumberGenerator, bottomPerformers, bottomPerformers, aBottomXBottom, offspringBufferIndex);
	offspringBufferIndex += aBottomXBottom;

	// add new random programs

	// compareBuffers();

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

	/*
	 * String msg = "t=" + aTop + ", tXt=" + aTopXTop + ", tM=" +
	 * aTopMutants + ", tXb=" + aTopXBottom + ", b=" + aBottom + ", bM=" +
	 * aBottomMutants + ", bXb=" + aBottomXBottom + ", n=" + aRandom;
	 * 
	 * logger.fine("[GENERATION " + generationCounter +
	 * "] REGENERATION COMPLETED:" + msg);
	 */

	orderActivePopulationByFitness();

	/*
	for(Agent agent: activeBuffer) {
	    logger.info("making sure "+agent.getAgentId()+" fitness="+agent.getFitness());
	    if(agent.getFitness()!=0) {
		logger.info("trap2");
	    }
	}
	*/
	generationCounter++;
	//printActiveBuffer();

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
    private final void swapBufferElement(ArrayList<Agent> srcBuffer, ArrayList<Agent> targetBuffer, int srcIndex,
	    int targetIndex) {
	Agent savedBaseAgent = targetBuffer.get(targetIndex);
	targetBuffer.set(targetIndex, srcBuffer.get(srcIndex));
	srcBuffer.set(srcIndex, savedBaseAgent);
    }

    private final void swapBufferElements(ArrayList<Agent> srcBuffer, ArrayList<Agent> targetBuffer, int srcIndex,
	    int numElements, int targetIndex) {
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

	this.averageFitness = sum / (double) genePoolSize;

    }

    public int getGenePoolSize() {
	return genePoolSize;
    }

}
