package com.synthverse.evolver.core;

import com.synthverse.stacks.GenotypeInstruction;
import com.synthverse.stacks.InstructionTranslator;
import com.synthverse.stacks.Program;
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

    public final static void pointMutate(MersenneTwisterFast randomNumberGenerator, Agent parent, Agent offspring,
	    double mutationRate) {
	GenotypeInstruction[] offspringDNA = offspring.getProgram().getGenotypeArray();
	Program parentProgram = parent.getProgram();
	for(int i=0;i<parentProgram.getSize();i++) {
	    offspringDNA[i] = parentProgram.getInstruction(i);
	}
	

	for (int i = 0; i < offspringDNA.length; i++) {
	    if (randomNumberGenerator.nextBoolean(mutationRate)) {
		offspringDNA[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
	    }
	}
    }

    public final static void randomize(Agent offspring) {
	offspring.getProgram().randomizeInstructions();
    }

    public final static void cross(MersenneTwisterFast randomNumberGenerator, Agent parentA, Agent parentB,
	    Agent offspring) {
	GenotypeInstruction[] offspringDNA = offspring.getProgram().getGenotypeArray();
	GenotypeInstruction[] parentA_DNA = parentA.getProgram().getGenotypeArray();
	GenotypeInstruction[] parentB_DNA = parentB.getProgram().getGenotypeArray();

	for (int i = 0; i < offspringDNA.length; i++) {
	    if (randomNumberGenerator.nextBoolean()) {
		offspringDNA[i] = parentA_DNA[i];
	    } else {
		offspringDNA[i] = parentB_DNA[i];
	    }
	}

    }

}
