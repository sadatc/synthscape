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

    private MetaInstruction[] metaInstructionArray = null;
    private int size;

    public MersenneTwisterFast randomNumberGenerator = null;

    public final void randomizeInstructions() {
	for (int i = 0; i < Config.DEFAULT_PROGRAM_ARRAY_SIZE; i++) {
	    metaInstructionArray[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
	}
    }

    public final MetaInstruction[] getMetaInstructionArray() {
	return metaInstructionArray;
    }

    public final void setInstructionArray(MetaInstruction[] instructionArray) {
	this.metaInstructionArray = instructionArray;
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
	    this.metaInstructionArray = new MetaInstruction[this.size];
	    for (int i = 0; i < this.size; i++) {
		this.metaInstructionArray[i] = p.metaInstructionArray[i];
	    }
	}
    }

    private Program(MersenneTwisterFast randomNumberGenerator, int maxSize) {
	// restricted, use Factory methods to create programs.
	this.randomNumberGenerator = randomNumberGenerator;
	size = 0;
	metaInstructionArray = new MetaInstruction[maxSize];
    }

    public final void fillWithNOOP() {
	Arrays.fill(metaInstructionArray, Instruction.NOOP);
    }

    public final void addInstruction(MetaInstruction instruction) {
	// addCode(InstructionTranslator.toCode(instruction));
	metaInstructionArray[size] = instruction;
	size++;
    }

    public final boolean addInstructionSafely(MetaInstruction instruction) {
	if (size < getSizeLimit()) {
	    metaInstructionArray[size] = instruction;
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
     * public final void addCode(int code) { metaInstructionArray[size] =
     * InstructionTranslator.toInstruction(code); size++; }
     */
    public final int getSize() {
	return size;
    }

    public final int getSizeLimit() {
	return metaInstructionArray.length;
    }

    public final boolean isIPValid(int ipIndex) {
	return (ipIndex >= 0 && ipIndex < size);
    }

    /*
     * public final int getCode(int ipIndex) { return
     * InstructionTranslator.toCode(metaInstructionArray[ipIndex]); }
     */
    public final void copyInto(MetaInstruction[] array) {
	for (int i = 0; i < size; i++) {
	    array[i] = metaInstructionArray[i];
	}
    }

    public final MetaInstruction getInstruction(int ipIndex) {
	return metaInstructionArray[ipIndex];
    }

    public final int getSignature() {
	int result = 0;
	if (size > 0) {
	    for(int i=0;i<size;i++) {
		result+= metaInstructionArray[i].getSignature();
	    }
	} else {
	    return 0;
	}
	return result;
    }
    
    
    @Override
    public final String toString() {
	if (size > 0) {
	    return Arrays.toString(metaInstructionArray);
	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    public final String toTranslatedString() {
	if (size > 0) {
	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(metaInstructionArray[0]);

	    for (int i = 1; i < metaInstructionArray.length; i++) {
		buf.append(", ");
		buf.append(metaInstructionArray[i]);
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
	    buf.append(metaInstructionArray[0]);

	    for (int i = 1; i < n; i++) {
		buf.append(", ");
		buf.append(metaInstructionArray[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

    /*
     * public long getLongHashCode() { return
     * HashUtils.getLongHash(metaInstructionArray, size); }
     * 
     * @Override public final int hashCode() { return
     * Arrays.hashCode(metaInstructionArray); }
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
	if (!Arrays.equals(metaInstructionArray, other.metaInstructionArray))
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
		program.metaInstructionArray[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
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
		    program.metaInstructionArray[i] = InstructionTranslator.getRandomInstruction(randomNumberGenerator);
		}
	    }

	    return program;

	}

    }

}
