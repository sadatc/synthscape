package com.synthverse.synthscape.evolutionarymodel.embodied;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.google.common.collect.EvictingQueue;
import com.synthverse.Main;
import com.synthverse.stacks.GenotypeInstruction;
import com.synthverse.stacks.Instruction;
import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Broadcast;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.DynamicEvennessAlgorithm;
import com.synthverse.synthscape.core.Event;
import com.synthverse.synthscape.core.EventStats;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SignalType;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Trait;
import com.synthverse.util.LogUtils;

import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * 
 * An EmbodiedAgent contains within it a Population Island of a particular
 * species. At any point in time, it is running one the individuals of its own
 * island. The only distinction is, during run-time, each embodied agent is
 * running its own instance of an evolution engine, whereas, in island model,
 * there is a centralized evolution engine that coordinates everything
 * 
 * @author sadat
 * 
 */
@SuppressWarnings("serial")
public class EmbodiedAgent extends Agent {

	private static Logger logger = Logger.getLogger(EmbodiedAgent.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	DescriptiveStatistics ancestorFitnessStats = new DescriptiveStatistics(
			Main.settings.DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE);
	double previousAncestorFitnessMean = 0.0;

	public EmbodiedAgentEvolver activeEvolver;
	public Species activeSpecies;
	public Species progenitorSpecies;
	public Map<Species, EmbodiedAgentEvolver> speciesEvolverMap = new HashMap<Species, EmbodiedAgentEvolver>();

	public EventStats poolHistoricalEventStats = new EventStats();

	public EventStats poolGenerationEventStats = new EventStats();

	public int poolSize;

	public EmbodiedAgent mate = null;

	protected static long _optimizationEmbodiedAgentCounter = 0;

	public DescriptiveStatistics fitnessStats = new DescriptiveStatistics();

	public List<Program> partnerABuffer = new ArrayList<Program>();
	public List<Program> partnerBBuffer = new ArrayList<Program>();

	public EventStats passivelyReceivedSignals = new EventStats();

	protected long embodiedAgentId;

	public double computedAlphaDistance;

	public boolean isProgenitor = true;

	public EmbodiedAgent(Simulation simulation, AgentFactory agentFactory, Species species, int poolSize) {
		super(simulation, species);
		this.progenitorSpecies = species;
		this.activeSpecies = species;

		_optimizationEmbodiedAgentCounter++;
		embodiedAgentId = _optimizationEmbodiedAgentCounter;
		setPoolSize(poolSize);

		try {
			activeEvolver = new EmbodiedAgentEvolver(this, simulation, agentFactory, species);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		activeAgent = activeEvolver.getAgent(species, 0, 0);
		activeAgent.setHostAgent(this);
		this.isHostAgent = true;
		activeAgent.isProxyAgent = true;
	}

	public EmbodiedAgent(Simulation sim, AgentFactory agentFactory, Species species, int poolSize, int generationNumber,
			int maxSteps, int startX, int startY) {
		super(sim, species, generationNumber, maxSteps, startX, startY);
		this.progenitorSpecies = species;
		this.activeSpecies = species;

		_optimizationEmbodiedAgentCounter++;
		embodiedAgentId = _optimizationEmbodiedAgentCounter;
		setPoolSize(poolSize);
		try {
			activeEvolver = new EmbodiedAgentEvolver(this, sim, agentFactory, species);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		activeAgent = activeEvolver.getAgent(species, startX, startY);

	}

	final public void synchronizeLocationFromActiveAgent() {
		Int2D location = sim.agentGrid.getObjectLocation(activeAgent);
		this.setX(location.getX());
		this.setY(location.getY());
		sim.agentGrid.setObjectLocation(this, location);
	}

	final public void synchronizeLocationToActiveAgent() {
		Int2D location = sim.agentGrid.getObjectLocation(this);
		activeAgent.setX(location.getX());
		activeAgent.setY(location.getY());
		sim.agentGrid.setObjectLocation(activeAgent, location);
	}

	public void setNextActiveAgent(int newX, int newY) {
		activeAgent = activeEvolver.getAgent(species, newX, newY);
		activeAgent.setHostAgent(this);
		this.isHostAgent = true;
		activeAgent.isProxyAgent = true;

	}

	public void stepAction(SimState state) {

		activeAgent.getVirtualMachine().step();
		// synchronize location
		synchronizeLocationFromActiveAgent();

	}

	private final void mateWithPotentiallyClosebyPartner() {
		// attempt to mate, if any other agents of the same species nearby
		// nearby...

		if (generationsSinceLastMating > Main.settings.MATING_GENERATION_FREQUENCY) {

			// first let's find out the closest agent

			EmbodiedAgent potentialMate = null;
			Bag agentBag = sim.agentGrid.getAllObjects();

			// find the closest agent that is of the same species and within
			// MATING_PROXIMITY_RADIUS distance

			for (int i = 0; i < agentBag.numObjs; i++) {
				Agent agent = (Agent) agentBag.get(i);

				// must be of same species...
				if (agent != this && agent instanceof EmbodiedAgent && agent.getSpecies() == this.getSpecies()) {
					double distance = distance(agent.x, agent.y, this.x, this.y);

					// the partner agent must be close enough
					// the partner must have not mated recently AND
					// meet the success rate...

					if (distance <= Main.settings.MATING_PROXIMITY_RADIUS
							&& agent.generationsSinceLastMating > Main.settings.MATING_GENERATION_FREQUENCY
							&& sim.random.nextDouble() < Main.settings.MATING_SUCCESS_RATE) {
						potentialMate = (EmbodiedAgent) agent;
					}
				}
			}
			if (potentialMate != null) {

				// perform mating...
				replaceBottomWithPartnerTop(this, potentialMate);
				this.generationsSinceLastMating = 0;
				potentialMate.generationsSinceLastMating = 0;
				String speciesName = potentialMate.getSpecies().toString();
				logger.info(speciesName + this.embodiedAgentId + " mated with " + speciesName
						+ potentialMate.embodiedAgentId + " @generation=" + activeEvolver.getGeneration());
			}

		}

	}

	private final void replaceBottomWithPartnerTop(EmbodiedAgent partnerA, EmbodiedAgent partnerB) {

		partnerABuffer.clear();
		partnerBBuffer.clear();

		int partnerASize = partnerA.activeEvolver.activeBuffer.size();
		int partnerBSize = partnerB.activeEvolver.activeBuffer.size();

		int partnerAHalfIndex = partnerASize / 2;
		int partnerBHalfIndex = partnerBSize / 2;

		if (partnerAHalfIndex != partnerBHalfIndex) {
			logger.info("buffer sizes should never be different -- investigate");
			System.exit(1);
		}

		// make a deep copy of A's top half
		for (int i = 0; i < partnerAHalfIndex; i++) {
			partnerABuffer.add(new Program(partnerA.activeEvolver.activeBuffer.get(i).getProgram()));
		}
		// now write this into B's bottom half
		for (int i = partnerBHalfIndex, k = 0; i < partnerBSize; i++, k++) {
			partnerB.activeEvolver.activeBuffer.get(i).setProgram(partnerABuffer.get(k));
		}

		// make a deep copy of B's top half
		for (int i = 0; i < partnerBHalfIndex; i++) {
			partnerBBuffer.add(new Program(partnerB.activeEvolver.activeBuffer.get(i).getProgram()));
		}
		// now write this into A's bottom half
		for (int i = partnerAHalfIndex, k = 0; i < partnerASize; i++, k++) {
			partnerA.activeEvolver.activeBuffer.get(i).setProgram(partnerBBuffer.get(k));
		}

	}

	public void evaluateLocalFitness() {
		// local fitness is evaluated based on what traits this agent has
		this.fitness = 0.0;

		Set<Trait> traits = this.species.getTraits();
		//D.p("XXX: evaluating for "+this.species);

		// if its multicapable -- then evalate as a homogenous
		if (traits.contains(Trait.HOMOGENOUS)) {
			this.fitness += computeFitness(Trait.HOMOGENOUS);
		} else {
			for (Trait trait : traits) {
				this.fitness += computeFitness(trait);
			}
		}
		this.activeAgent.setFitness(this.fitness);
		this.activeAgent.setProvidedFeedback(true);

		Agent archetypeAgent = this.activeAgent.getArchetypeReference();
		if (archetypeAgent != null) {
			archetypeAgent.setFitness(this.fitness);
		}

	}

	public final void reclaimActiveAgent() {
		activeAgent.eventStats.clear();
		activeEvolver.reclaimEmbodiedIslanderAgent(activeAgent);
	}

	private double computeFitness(Trait trait) {
		double result = 0.0;

		if (trait == Trait.DETECTION) {
			result += computeDetectionFitness();
		}

		if (trait == Trait.EXTRACTION) {
			result += computeExtractionFitness();
		}

		if (trait == Trait.PROCESSING) {
			result += computeProcessingFitness();
		}

		if (trait == Trait.HOMOGENOUS || trait == Trait.TRANSPORTATION) {
			result += computeTransportationFitness();
		}

		return result;
	}

	private double computeDetectionFitness() {
		double result = 0.0;

		for (Event event : activeAgent.eventStats.getEvents()) {
			switch (event) {
			case DETECTED_EXTRACTED_RESOURCE:
			case DETECTED_RAW_RESOURCE:
			case DETECTED_PROCESSED_RESOURCE:
			case RECEIVED_DETECTOR_REWARDS:
				result += activeAgent.eventStats.getValue(event);
				break;
			default:
				break;
			}
		}

		return result;
	}

	private double computeExtractionFitness() {
		double result = 0.0;

		for (Event event : activeAgent.eventStats.getEvents()) {
			switch (event) {
			case RECEIVED_EXTRACTOR_REWARDS:
			case EXTRACTED_RESOURCE:
				result += activeAgent.eventStats.getValue(event);
				break;
			default:
				break;
			}
		}

		return result;
	}

	private double computeProcessingFitness() {
		double result = 0.0;

		for (Event event : activeAgent.eventStats.getEvents()) {
			switch (event) {
			case RECEIVED_PROCESSOR_REWARDS:
			case PROCESSED_RESOURCE:
				result += activeAgent.eventStats.getValue(event);
				break;
			default:
				break;
			}
		}

		return result;
	}

	private double computeTransportationFitness() {
		double result = 0.0;

		for (Event event : activeAgent.eventStats.getEvents()) {
			switch (event) {
			case ATTEMPT_TO_UNLOAD_HELD_RESOURCE:
				// D.p("applying small reward for holding resource...");
				result += 0.00125; // tiny reward for being able to hold on to a
									// resource
				break;
			case COLLECTED_RESOURCE:
				result += activeAgent.eventStats.getValue(event);
				break;
			default:
				break;
			}
		}

		return result;
	}

	final public int evolve() {
		int returnValue = -1;
		if (Main.settings.DYNAMIC_EVENNESS) {
			returnValue = evolveWithProliferationOfFittest();
			// returnValue = evolveWithProliferationOfUnFittest();
		} else {
			returnValue = evolveNoSpeciesSwitch();
		}
		return returnValue;
	}

	final public int evolveNoSpeciesSwitch() {
		if (generationsSinceLastMating < Integer.MAX_VALUE) {
			generationsSinceLastMating++;
		}

		int returnValue = activeEvolver.evolve();

		if (sim.matingEnabled) {
			mateWithPotentiallyClosebyPartner();
		}

		return returnValue;

	}

	/**
	 * If fitness has increased in the last
	 * DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE generations create a child
	 * agent of the same type. This child agent has isProgenitor = false If
	 * fitness decreses, and this is NOT a progentor agent, destroy it.
	 * 
	 * @return
	 */
	final public int evolveWithProliferationOfFittest() {

		// FIXME: change all of this...

		if (generationsSinceLastMating < Integer.MAX_VALUE) {
			generationsSinceLastMating++;
		}

		int returnValue = activeEvolver.evolve();

		if (sim.matingEnabled) {
			mateWithPotentiallyClosebyPartner();
		}

		// above, everything evolved as normal..
		// if dynamic evenness is enabled, we now
		// decide if this agent should continue or make way for more fit agents

		if (Main.settings.DYNAMIC_EVENNESS) {

			boolean reproduce = false;
			boolean die = false;

			ancestorFitnessStats.addValue(this.fitnessStats.getMean());
			//D.p(this.species.getName()+": current fitness="+this.fitnessStats.getMean());

			if (ancestorFitnessStats.getN() >= Main.settings.DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE) {
				// calculate the mean fitness of current window
				double fitnessMean = ancestorFitnessStats.getMean();

				// if mean fitness is improving reproduce; if it's reducing, die
				if (fitnessMean != 0.0 && fitnessMean > previousAncestorFitnessMean) {
					reproduce = true;
				} else {
					die = true;
				}

				previousAncestorFitnessMean = fitnessMean;
			}
			if (reproduce) {
				this.getSim().birthQueue.add(this);
			}

			if (die && !isProgenitor) {

				this.getSim().deathQueue.add(this);
			}
		}

		return returnValue;
	}

	/**
	 * If fitness has decreased in the last
	 * DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE generations create a child
	 * agent of the same type. This child agent has isProgenitor = false If
	 * fitness decreses, and this is NOT a progentor agent, destroy it.
	 * 
	 * @return
	 */
	final public int evolveWithProliferationOfUnFittest() {

		// FIXME: change all of this...

		if (generationsSinceLastMating < Integer.MAX_VALUE) {
			generationsSinceLastMating++;
		}

		int returnValue = activeEvolver.evolve();

		if (sim.matingEnabled) {
			mateWithPotentiallyClosebyPartner();
		}

		// above, everything evolved as normal..
		// if dynamic evenness is enabled, we now
		// decide if this agent should switch
		if (Main.settings.DYNAMIC_EVENNESS) {

			boolean reproduce = false;
			boolean die = false;

			ancestorFitnessStats.addValue(this.fitnessStats.getMean());

			if (ancestorFitnessStats.getN() >= Main.settings.DE_GENERATIONS_TO_OBSERVE_FITNESS_PERFORMANCE) {
				// calculate the mean fitness of current window
				double fitnessMean = ancestorFitnessStats.getMean();

				// if mean fitness is decreasing, it will request for more
				// re-inforcements

				if ((fitnessMean != 0.0 && fitnessMean < previousAncestorFitnessMean) || fitnessMean == 0.0) {
					// PROLIFERATE: create a new child agent
					reproduce = true;
				} else {
					// decrease numbers
					die = true;
				}
				previousAncestorFitnessMean = fitnessMean;

			}
			if (reproduce) {

				this.getSim().birthQueue.add(this);

			}

			if (die && !isProgenitor) {

				this.getSim().deathQueue.add(this);
			}
		}

		return returnValue;
	}

	public int getGeneration() {
		return activeEvolver.getGeneration();
	}

	@Override
	public double doubleValue() {
		double result = 0.0; // normal agent
		if (this.isCarryingResource) {
			result = 1.0;
		}
		return result;

	}

	@Override
	protected void initGenotype() {
		super.initGenotype();

		/*
		 * this.program = Program.Factory.createEmpty(sim.random);
		 * this.program.addInstructionsSafely(
		 * "ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE, FLOAT_TANH, FLOAT_LOG1P, ACTION_RESOURCE_LOAD, CODE_POP, INSTRUCTION_SEEK_NOOP, CODE_COPY_FROM_IA, 0.31187350085031484, 24, 0.27755576020413497, BOOLEAN_FROM_INTEGER, ACTION_MOVE_SE, ACTION_MOVE_NE, 57, ACTION_MOVE_SE, FLOAT_DECREMENT, ACTION_MOVE_TO_CLOSEST_HOME, ACTION_RESOURCE_EXTRACT, 0.8213671859786977, FLOAT_YANK, ACTION_RESOURCE_UNLOAD, INSTRUCTION_POP_CODE, FLOAT_EXPM1, INSTRUCTION_JUMP_BEGIN, 6, FLOAT_ASIN, FLOAT_IS_ZERO, 75, CONST_FLOAT_ZERO, INTEGER_SIZE, INTEGER_DUP, ACTION_RESOURCE_PROCESS, CODE_RAND, 56, INTEGER_POP, INSTRUCTION_EXCHANGE_LEFT, ACTION_MOVE_CONDITIONALLY_NS, 81, CODE_SWAP_IA_IP_RESET, true, CODE_ROT, 62, CONST_FALSE, ACTION_MOVE_NE, 0.5895271698179348, FLOAT_SIZE, INTEGER_REVERSE, 44, FLOAT_YANKDUP, CODE_YANKDUP, INTEGER_POP, FLOAT_HYPOT, BOOLEAN_POP, CODE_FLUSH, CONST_FLOAT_PI, FLOAT_MAX, FLOAT_LOG, FLOAT_TAN, FLOAT_INCREMENT, ACTION_RESOURCE_LOAD, 60, FLOAT_ASIN, 48, 0.2003142400412804, ACTION_DETECT_HOME, ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE, FLOAT_ROT, ACTION_DETECT_TRAIL, FLOAT_SIZE, 72, BOOLEAN_REVERSE, ACTION_IS_CARRYING_RAW_RESOURCE, CODE_FLUSH, FLOAT_HYPOT, 68, CODE_YANKDUP, 12, false, 0.3105951084695472, CODE_SWAP_IA_IP_RESET, FLOAT_LOG1P, NOOP, BOOLEAN_RAND, 0.8024601439949111, INSTRUCTION_NOOP_LEFT, FLOAT_FLUSH, ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE, BOOLEAN_POP, 16, CODE_SWAP_IA, 11, INSTRUCTION_JUMP_BEGIN, INTEGER_IS_ZERO, BOOLEAN_NOT, FLOAT_CBRT, ACTION_DETECT_TRAIL, INTEGER_FROM_FLOAT, ACTION_DETECT_TRAIL, 0.01939493213712762, INTEGER_SWAP, FLOAT_ACOS, INTEGER_MOD, CODE_COPY_FROM_IA, ACTION_IS_CARRYING_RAW_RESOURCE, 0.6895529544639853, ACTION_MOVE_SW, BOOLEAN_AND, INSTRUCTION_EXCHANGE_RIGHT, BOOLEAN_AND, INSTRUCTION_JUMP_END, FLOAT_DECREMENT, 18, FLOAT_SQRT, ACTION_RESOURCE_PROCESS, 54, 0.9665568987971895, INSTRUCTION_JUMP_END, NOOP, 0.36981098882995644, INTEGER_LESS_THAN_EQUALS, 24, ACTION_MOVE_CONDITIONALLY_EW, INSTRUCTION_EXCHANGE_LEFT, INTEGER_SIZE, BOOLEAN_FROM_INTEGER, ACTION_LEAVE_TRAIL, INSTRUCTION_EXIT, NOOP, FLOAT_DUP, ACTION_IS_CARRYING_RAW_RESOURCE, CODE_YANKDUP, ACTION_FOLLOW_TRAIL, INTEGER_SWAP, INTEGER_YANK, BOOLEAN_FROM_FLOAT, INTEGER_GREATER_THAN_EQUALS, ACTION_MOVE_SW, BOOLEAN_FROM_INTEGER, FLOAT_SIN, ACTION_MOVE_N, BOOLEAN_POP, FLOAT_EXPM1, INTEGER_ADD, ACTION_MOVE_NW, FLOAT_ASIN, 71, ACTION_MOVE_SE, 56, 78, INTEGER_MUL, FLOAT_REVERSE, 0.8255539393163316, 0.28608223869020977, BOOLEAN_REVERSE, 84, INSTRUCTION_SEEK_NOOP, BOOLEAN_REVERSE, BOOLEAN_NOT, ACTION_MOVE_SE, FLOAT_SIZE, 0.31401562466492916, 0.41195724092538755, 0.07853383835205952, 89, INTEGER_GREATER_THAN, INTEGER_REVERSE, 0.8697769735277823, false, BOOLEAN_FROM_INTEGER, CODE_POP, ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE, 0.8804436040192019, 90, BOOLEAN_RAND, ACTION_IS_CARRYING_EXTRACTED_RESOURCE, FLOAT_LOG10, BOOLEAN_EQUAL, FLOAT_DUP, false, FLOAT_TANH, CODE_POP, FLOAT_LOG10, FLOAT_SIZE, FLOAT_ATAN2, INSTRUCTION_EXCHANGE_LEFT, FLOAT_LOG10, ACTION_FOLLOW_TRAIL, FLOAT_FLUSH, true, 54, INTEGER_RAND, ACTION_IS_CARRYING_RESOURCE, INTEGER_RAND, FLOAT_MOD, INTEGER_LESS_THAN_EQUALS, INTEGER_IS_ZERO, INSTRUCTION_HOP_LEFT, INTEGER_LESS_THAN, FLOAT_ASIN, FLOAT_LOG1P, ACTION_RESOURCE_PROCESS, FLOAT_SIZE, 0.9864214173438045, INSTRUCTION_HOP_LEFT, 1, 86, INSTRUCTION_REVERSE, CODE_SWAP_IA_IP_RESET, FLOAT_GREATER_THAN_EQUALS, 47, false, FLOAT_ROT, INTEGER_LESS_THAN, BOOLEAN_RAND, INTEGER_FLUSH, ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE, INSTRUCTION_JUMP_ABS, 43, 0, FLOAT_CBRT, FLOAT_ATAN2, ACTION_MOVE_W, ACTION_MOVE_S, 0.4554173764835727, 67, 0.9386559955351026, BOOLEAN_YANKDUP, 0.2760193591306723, 0.10777378207784227, BOOLEAN_SWAP, ACTION_MOVE_TO_CLOSEST_AGENT, FLOAT_LOG10, FLOAT_IS_POSITIVE, FLOAT_ACOS, INTEGER_SIZE, INTEGER_IS_ZERO, INTEGER_INCREMENT, INSTRUCTION_EXCHANGE_LEFT, CODE_REVERSE, CODE_REVERSE, INTEGER_EQUAL, BOOLEAN_AND, true, INTEGER_FROM_FLOAT, INTEGER_POP, FLOAT_CBRT, FLOAT_FLOOR, 17, ACTION_RESOURCE_EXTRACT, 93, BOOLEAN_POP, FLOAT_YANK, INTEGER_SWAP, INSTRUCTION_NOOP_RIGHT, ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE, CODE_ROT"
		 * ); VirtualMachine vm = VirtualMachine.Factory.createDefault(sim,
		 * this, sim.random); vm.loadProgram(this.program);
		 * vm.setCpuCycles(sim.getMaxStepsPerAgent());
		 * this.setVirtualMachine(vm);
		 */

		// this.program = Program.Factory.createRandom(sim.random);

		this.program = Program.Factory.createEmpty(sim.random);

		boolean spaceLeft = true;

		do {
			this.program.addInstructionSafely(
					GenotypeInstruction.fromInstruction(Instruction.ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_DETECT_HOME));
			this.program.addInstructionSafely(
					GenotypeInstruction.fromInstruction(Instruction.ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE));
			this.program
					.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_DETECT_TRAIL));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_FOLLOW_TRAIL));
			this.program.addInstructionSafely(
					GenotypeInstruction.fromInstruction(Instruction.ACTION_IS_CARRYING_EXTRACTED_RESOURCE));
			this.program.addInstructionSafely(
					GenotypeInstruction.fromInstruction(Instruction.ACTION_IS_CARRYING_PROCESSED_RESOURCE));
			this.program.addInstructionSafely(
					GenotypeInstruction.fromInstruction(Instruction.ACTION_IS_CARRYING_RAW_RESOURCE));
			this.program
					.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_IS_CARRYING_RESOURCE));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_E));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_W));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_NE));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_NW));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_SE));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_SW));
			this.program
					.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_MOVE_TO_CLOSEST_HOME));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_RESOURCE_EXTRACT));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_RESOURCE_LOAD));
			this.program.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_RESOURCE_PROCESS));
			spaceLeft = this.program
					.addInstructionSafely(GenotypeInstruction.fromInstruction(Instruction.ACTION_RESOURCE_UNLOAD));
		} while (spaceLeft);

		VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
		vm.overwriteGenotypeWithProgram(this.program);
		vm.setCpuCycles(sim.getMaxStepsPerAgent());
		this.setVirtualMachine(vm);

		//
		// this.program.addInstructionSafely(Instruction.ACTION_DETECT_RAW_RESOURCE);
		// logger.info(this.program.toTranslatedString());

		// this.program = Program.Factory.createEmpty(sim.random);
		// this.program.addInstructionSafely(Instruction.ACTION_DETECT_RAW_RESOURCE);
		// this.program.addInstructionSafely(Instruction.ACTION_DETECT_RAW_RESOURCE);
		// start here tomorrow morning

		/*
		 * this.program = Program.Factory.createRandom(sim.random);
		 * VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this,
		 * sim.random); vm.loadProgram(this.program);
		 * vm.setCpuCycles(sim.getMaxStepsPerAgent());
		 * this.setVirtualMachine(vm);
		 */
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public static long get_optimizationEmbodiedAgentCounter() {
		return _optimizationEmbodiedAgentCounter;
	}

	public Agent getActiveAgent() {
		return activeAgent;
	}

	public void setActiveAgent(Agent activeAgent) {
		this.activeAgent = activeAgent;
	}

	@Override
	protected void setLocation(int newX, int newY) {
		setX(newX);
		setY(newY);
		activeAgent.setX(newX);
		activeAgent.setY(newY);
	}

}
