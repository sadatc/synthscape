/**
 * @(#)InstructionTranslator.java  6:50:08 PM Mar 3, 2009
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

import java.util.HashMap;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.util.LogUtils;

import ec.util.MersenneTwisterFast;

/**
 * This Class translates instructions (see Instruction) to and from its Code
 * (aka OpCode) representation. Since OpCodes are purely numbers, the "compiled"
 * programs are more compact than the "source".
 * 
 * This Class should be used whenever we need to translate Instructions to
 * OpCodes and vice versa.
 * 
 * @author sadat
 * 
 */
public class InstructionTranslator {

    private static Logger logger = Logger.getLogger(InstructionTranslator.class.getName());

    private static Instruction[] instructions = null;

    private static HashMap<String, Instruction> instructionTable = new HashMap<String, Instruction>();

    private static int numInstructions;

    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	instructions = Instruction.values();
	numInstructions = instructions.length;
	setupInstructionTable();
    }

    private static void setupInstructionTable() {
	for (Instruction instruction : instructions) {
	    instructionTable.put(instruction.toString(), instruction);
	}
    }

    private InstructionTranslator() {
	throw new AssertionError("InstructionTranslator constructor is restricted");
    }

    /**
     * Given the numeric code, this returns the instruction. This doesn't
     * perform any validations to make sure that code is valid. If safety is
     * required please use {@link #toInstructionWithValidation(int)}; this is
     * meant to be used only if code is guaranteed to be valid.
     * 
     * @param code
     * @return
     */
    public static final Instruction toInstruction(int code) {
	return instructions[code];
    }

    /**
     * Given an instruction, this returns the numeric code.
     * 
     * @param instruction
     * @return
     */
    public static final int toCode(Instruction instruction) {
	return instruction.ordinal();
    }

    /**
     * This will return a valid random numeric code. Useful for random code
     * generation.
     * 
     * @return
     */
    public static final int getRandomCode(MersenneTwisterFast randomNumberGenerator) {
	return randomNumberGenerator.nextInt(numInstructions);
    }

    private static boolean isNumeric(String string) {

	if (string != null) {
	    string = string.trim();
	    boolean foundDotBefore = false;
	    for (int i = 0; i < string.length(); i++) {
		char c = string.charAt(i);
		if (c == '.') {
		    if (foundDotBefore) {
			return false;
		    } else {
			foundDotBefore = true;
		    }
		} else {
		    if (!Character.isDigit(c)) {
			return false;
		    }
		}
	    }
	    return true;
	}

	return false;
    }

    public static final GenotypeInstruction decodeFromString(String instruction) {
	GenotypeInstruction result = GenotypeInstruction.NOOP;

	if (instruction != null) {
	    instruction = instruction.trim();
	    instruction = instruction.toUpperCase();
	    if (instructionTable.containsKey(instruction)) {
		// result = new
		// GenotypeInstruction(instructionTable.get(instruction));
		result = GenotypeInstruction.fromInstruction(instructionTable.get(instruction));
	    } else if (instruction.equals("TRUE")) {
		result = new GenotypeInstruction(true);
	    } else if (instruction.equals("FALSE")) {
		result = new GenotypeInstruction(false);
	    } else if (isNumeric(instruction)) {
		if (instruction.contains(".")) {
		    result = new GenotypeInstruction(Double.parseDouble(instruction));
		} else {
		    result = new GenotypeInstruction(Integer.parseInt(instruction));
		}
	    }
	}

	return result;
    }

    /**
     * This will return a valid random instruction. Useful for random code
     * generation.
     * 
     * @return
     */
    public static final GenotypeInstruction getRandomInstruction(MersenneTwisterFast randomNumberGenerator) {

	GenotypeInstruction result = GenotypeInstruction.NOOP;
	// instructions, literals (bool, double, int) are all equally likely...

	double dice = randomNumberGenerator.nextDouble();
	if (dice > 0.9) {
	    result = new GenotypeInstruction(randomNumberGenerator.nextDouble());

	} else if (dice > 0.85) {
	    result = new GenotypeInstruction(randomNumberGenerator.nextBoolean());

	} else if (dice > 0.7) {
	    result = new GenotypeInstruction(randomNumberGenerator.nextInt(100));

	} else {
	    // result = new
	    // GenotypeInstruction(instructions[getRandomCode(randomNumberGenerator)]);
	    result = GenotypeInstruction.fromInstruction(instructions[getRandomCode(randomNumberGenerator)]);
	}

	return result;
    }

    public static final void logStatus() {

	StringBuilder sb = new StringBuilder(">>> adding ");
	sb.append(numInstructions);
	sb.append(" instructions to the vocabulary...");
	logger.info(sb.toString());
	/*
	for (int i = 0; i < instructions.length; i++) {
	    sb.setLength(0);
	    sb.append("instruction #");
	    sb.append(i);
	    sb.append(":");
	    sb.append(instructions[i]);
	    logger.info(sb.toString());
	}
	*/
	logger.info("<<< done adding.\n");
    }

    /*
     * public static final GenotypeInstruction guessFromInt(int anyInt) {
     * GenotypeInstruction instruction = GenotypeInstruction.NOOP;
     * 
     * anyInt = (anyInt < 0) ? 0 : anyInt; anyInt = (anyInt < numInstructions) ?
     * anyInt : anyInt % numInstructions;
     * 
     * instruction = toInstruction(anyInt);
     * 
     * return instruction; }
     * 
     * public static final Instruction guessFromFloat(double anyFloat) {
     * Instruction instruction = Instruction.NOOP;
     * 
     * anyFloat = (anyFloat < 0) ? -anyFloat : anyFloat; instruction =
     * toInstruction((anyFloat < numInstructions) ? (int) anyFloat : (int)
     * anyFloat % numInstructions);
     * 
     * return instruction; }
     * 
     * public static final Instruction guessFromBoolean(boolean anyBool) {
     * return (anyBool) ? Instruction.CONST_TRUE : Instruction.CONST_FALSE; }
     */

}
