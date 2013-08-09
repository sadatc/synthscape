/**
 * @(#)Program.java  4:02:22 PM Feb 3, 2009
 * 
 * Copyright (c) 2004-2009 Sadat Chowdhury (sadatc@gmail.com)
 * 
 * The author reserves all rights to this software. Please contact the author 
 * (sadatc@gmail.com) to obtain permission to use, modify, or redistribute this 
 * software in any form. 
 * 
 * Unless otherwise stated, the author issues no guarantees of success and 
 * offers no warranties against damages of any kind. 
 * 
 * Last Revision : $Rev:: 6                     $: 
 * Last Updated  : $Date:: 2012-10-14 22:50:12 #$:
 * 
 */

package com.synthverse.stacks;

import java.util.Arrays;

import com.synthverse.util.HashUtils;

import ec.util.MersenneTwisterFast;

/**
 * This represents a program that is ready for execution. Programs are
 * internally represented as arrays of codes returned by {@link
 * com.synthverse.stacks.InstructionTranslator.#toCode(Instruction)}
 * 
 * Note: These programs are not able to contain values. For programs that
 * 
 * @author sadat
 * 
 */
public class Program {

    private Instruction[] instructionArray = null;
    private int size;

    public MersenneTwisterFast randomNumberGenerator = null;

    public final void randomizeInstructions() {
	for (int i = 0; i < Config.DEFAULT_PROGRAM_ARRAY_SIZE; i++) {
	    instructionArray[i] = InstructionTranslator
		    .getRandomInstruction(randomNumberGenerator);
	}
    }

    public final Instruction[] getInstructionArray() {
	return instructionArray;
    }

    public final void setInstructionArray(Instruction[] instructionArray) {
	this.instructionArray = instructionArray;
    }

    public final void setSize(int size) {
	this.size = size;
    }

    private Program() {

    }

    private Program(MersenneTwisterFast randomNumberGenerator, int maxSize) {
	// restricted, use Factory methods to create programs.
	this.randomNumberGenerator = randomNumberGenerator;
	size = 0;
	instructionArray = new Instruction[maxSize];
    }

    public final void fillWithNOOP() {
	Arrays.fill(instructionArray, Instruction.NOOP);
    }

    public final void addInstruction(Instruction instruction) {
	addCode(InstructionTranslator.toCode(instruction));
    }

    public final void addCode(int code) {
	instructionArray[size] = InstructionTranslator.toInstruction(code);
	size++;
    }

    public final int getSize() {
	return size;
    }

    public final int getSizeLimit() {
	return instructionArray.length;
    }

    public final boolean isIPValid(int ipIndex) {
	return (ipIndex >= 0 && ipIndex < size);
    }

    public final int getCode(int ipIndex) {
	return InstructionTranslator.toCode(instructionArray[ipIndex]);
    }

    public final void copyInto(Instruction[] array) {
	for (int i = 0; i < size; i++) {
	    array[i] = instructionArray[i];
	}
    }

    public final Instruction getInstruction(int ipIndex) {
	return instructionArray[ipIndex];
    }

    @Override
    public final String toString() {
	if (size > 0) {
	    return Arrays.toString(instructionArray);
	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    public final String toTranslatedString() {
	if (size > 0) {
	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(instructionArray[0]);

	    for (int i = 1; i < instructionArray.length; i++) {
		buf.append(", ");
		buf.append(instructionArray[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    public long getLongHashCode() {
	return HashUtils.getLongHash(instructionArray, size);
    }

    @Override
    public final int hashCode() {
	return Arrays.hashCode(instructionArray);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Program))
	    return false;
	Program other = (Program) obj;
	if (!Arrays.equals(instructionArray, other.instructionArray))
	    return false;

	if (size != other.size)
	    return false;
	return true;
    }

    public static class Factory {

	public static final Program createDefault(
		MersenneTwisterFast randomNumberGenerator) {
	    return createRandom(randomNumberGenerator);
	}

	public static final Program createEmpty(
		MersenneTwisterFast randomNumberGenerator) {
	    Program program = new Program(randomNumberGenerator,
		    Config.DEFAULT_PROGRAM_ARRAY_SIZE);
	    program.size = 0;
	    return program;
	}

	public static final Program createRandom(
		MersenneTwisterFast randomNumberGenerator) {
	    Program program = null;

	    program = createEmpty(randomNumberGenerator);

	    for (int i = 0; i < Config.DEFAULT_PROGRAM_ARRAY_SIZE; i++) {
		program.instructionArray[i] = InstructionTranslator
			.getRandomInstruction(randomNumberGenerator);
	    }
	    program.size = Config.DEFAULT_PROGRAM_ARRAY_SIZE;

	    return program;

	}

	public static final Program createRandom(
		MersenneTwisterFast randomNumberGenerator, int size) {
	    Program program = null;

	    if (size > 0 && size < Config.DEFAULT_PROGRAM_ARRAY_SIZE) {
		program = createEmpty(randomNumberGenerator);
		program.size = size;

		for (int i = 0; i < size; i++) {
		    program.instructionArray[i] = InstructionTranslator
			    .getRandomInstruction(randomNumberGenerator);
		}
	    }

	    return program;

	}

    }

}
