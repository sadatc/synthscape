package com.synthverse.evolutionengine;

import com.synthverse.stacks.Instruction;
import com.synthverse.stacks.InstructionTranslator;
import com.synthverse.synthscape.core.Agent;

import ec.util.MersenneTwisterFast;

/**
 * 
 * Defines the two most common genetic operators: crossover and mutation.
 * 
 * @author sadat
 * 
 */

public final class GeneticOperator {

	public final static void pointMutate(
			MersenneTwisterFast randomNumberGenerator, Agent parent,
			Agent offspring, double mutationRate) {
		Instruction[] offspringDNA = offspring.getProgram()
				.getInstructionArray();
		parent.getProgram().copyInto(offspringDNA);

		for (int i = 0; i < offspringDNA.length; i++) {
			if (randomNumberGenerator.nextBoolean(mutationRate)) {
				offspringDNA[i] = InstructionTranslator
						.getRandomInstruction(randomNumberGenerator);
			}
		}
	}

	public final static void randomize(Agent offspring) {
		offspring.getProgram().randomizeInstructions();
	}

	public final static void cross(MersenneTwisterFast randomNumberGenerator,
			Agent parentA, Agent parentB, Agent offspring) {
		Instruction[] offspringDNA = offspring.getProgram()
				.getInstructionArray();
		Instruction[] parentA_DNA = parentA.getProgram().getInstructionArray();
		Instruction[] parentB_DNA = parentB.getProgram().getInstructionArray();

		for (int i = 0; i < offspringDNA.length; i++) {
			if (randomNumberGenerator.nextBoolean()) {
				offspringDNA[i] = parentA_DNA[i];
			} else {
				offspringDNA[i] = parentB_DNA[i];
			}
		}

	}

}
