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

    private GenotypeInstruction[] genotypeArray = null;
    private int size;

    public MersenneTwisterFast randomNumberGenerator = null;

    public final void randomizeInstructions() {
	for (int i = 0; i < Config.DEFAULT_PROGRAM_ARRAY_SIZE; i++) {
	    genotypeArray[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
	}
    }

    public final GenotypeInstruction[] getGenotypeArray() {
	return genotypeArray;
    }

    public final void setInstructionArray(GenotypeInstruction[] instructionArray) {
	this.genotypeArray = instructionArray;
    }

    public final void setSize(int size) {
	this.size = size;
    }

    private Program() {

    }

    public Program(Program p) {
	this.size = p.size;
	this.randomNumberGenerator = p.randomNumberGenerator;
	if (this.size > 0) {
	    this.genotypeArray = new GenotypeInstruction[this.size];
	    for (int i = 0; i < this.size; i++) {
		this.genotypeArray[i] = p.genotypeArray[i];
	    }
	}
    }

    private Program(MersenneTwisterFast randomNumberGenerator, int maxSize) {
	// restricted, use Factory methods to create programs.
	this.randomNumberGenerator = randomNumberGenerator;
	size = 0;
	genotypeArray = new GenotypeInstruction[maxSize];
    }

    public final void fillWithNOOP() {
	Arrays.fill(genotypeArray, Instruction.NOOP);
    }

    public final void addInstruction(GenotypeInstruction instruction) {
	// addCode(InstructionTranslator.toCode(instruction));
	genotypeArray[size] = instruction;
	size++;
    }

    public final boolean addInstructionSafely(GenotypeInstruction instruction) {
	if (size < getSizeLimit()) {
	    genotypeArray[size] = instruction;
	    size++;
	    return true;
	} else {
	    return false;

	}
    }
    
    public final boolean addInstructionsSafely(String csvString) {
	boolean result = true;
	
	if(csvString!=null) {
	    String[] instructions = csvString.split(",");
	    for(String instruction: instructions) {
		if(!addInstructionSafely(InstructionTranslator.decodeFromString(instruction))) {
		    result = false;
		}
	    }
	}
	
	return result;
    }
    
    
    /*
     * public final void addCode(int code) { genotypeArray[size] =
     * InstructionTranslator.toInstruction(code); size++; }
     */
    public final int getSize() {
	return size;
    }

    public final int getSizeLimit() {
	return genotypeArray.length;
    }

    public final boolean isIPValid(int ipIndex) {
	return (ipIndex >= 0 && ipIndex < size);
    }

    /*
     * public final int getCode(int ipIndex) { return
     * InstructionTranslator.toCode(genotypeArray[ipIndex]); }
     */
    public final void copyInto(GenotypeInstruction[] array) {
	for (int i = 0; i < size; i++) {
	    array[i] = genotypeArray[i];
	}
    }

    public final GenotypeInstruction getInstruction(int ipIndex) {
	return genotypeArray[ipIndex];
    }

    public final int getSignature() {
	int result = 0;
	if (size > 0) {
	    for(int i=0;i<size;i++) {
		result+= genotypeArray[i].getSignature();
	    }
	} else {
	    return 0;
	}
	return result;
    }
    
    
    @Override
    public final String toString() {
	if (size > 0) {
	    return Arrays.toString(genotypeArray);
	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    public final String toTranslatedString() {
	if (size > 0) {
	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(genotypeArray[0]);

	    for (int i = 1; i < genotypeArray.length; i++) {
		buf.append(", ");
		buf.append(genotypeArray[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    public final String toTranslatedString(int n) {
	if (size > 0) {
	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(genotypeArray[0]);

	    for (int i = 1; i < n; i++) {
		buf.append(", ");
		buf.append(genotypeArray[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    /*
     * public long getLongHashCode() { return
     * HashUtils.getLongHash(genotypeArray, size); }
     * 
     * @Override public final int hashCode() { return
     * Arrays.hashCode(genotypeArray); }
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Program))
	    return false;
	Program other = (Program) obj;
	if (!Arrays.equals(genotypeArray, other.genotypeArray))
	    return false;

	if (size != other.size)
	    return false;
	return true;
    }

    public static class Factory {

	public static final Program createDefault(MersenneTwisterFast randomNumberGenerator) {
	    return createRandom(randomNumberGenerator);
	}

	public static final Program createEmpty(MersenneTwisterFast randomNumberGenerator) {
	    Program program = new Program(randomNumberGenerator, Config.DEFAULT_PROGRAM_ARRAY_SIZE);
	    program.size = 0;
	    return program;
	}

	public static final Program createRandom(MersenneTwisterFast randomNumberGenerator) {
	    Program program = null;

	    program = createEmpty(randomNumberGenerator);

	    for (int i = 0; i < Config.DEFAULT_PROGRAM_ARRAY_SIZE; i++) {
		program.genotypeArray[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
	    }
	    program.size = Config.DEFAULT_PROGRAM_ARRAY_SIZE;

	    return program;

	}

	public static final Program createRandom(MersenneTwisterFast randomNumberGenerator, int size) {
	    Program program = null;

	    if (size > 0 && size < Config.DEFAULT_PROGRAM_ARRAY_SIZE) {
		program = createEmpty(randomNumberGenerator);
		program.size = size;

		for (int i = 0; i < size; i++) {
		    program.genotypeArray[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
		}
	    }

	    return program;

	}

    }

}
