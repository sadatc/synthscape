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

import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static Logger logger = Logger.getLogger(InstructionTranslator.class
			.getName());

	private static Instruction[] instructions = null;

	private static int numInstructions;

	static {
		LogUtils.applyDefaultSettings(logger, LogUtils.DEFAULT_LOG_LEVEL);
		instructions = Instruction.values();
		numInstructions = instructions.length;
		logStatus();
	}

	private InstructionTranslator() {
		throw new AssertionError(
				"InstructionTranslator constructor is restricted");
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

	/**
	 * This will return a valid random instruction. Useful for random code
	 * generation.
	 * 
	 * @return
	 */
	public static final Instruction getRandomInstruction(MersenneTwisterFast randomNumberGenerator) {
		return instructions[getRandomCode(randomNumberGenerator)];

	}

	public static final void logStatus() {
		if (logger.isLoggable(Level.CONFIG)) {
			StringBuffer sb = new StringBuffer("Num of Instructions: ");
			sb.append(numInstructions);
			sb.append(" - details: ");
			for (int i = 0; i < instructions.length; i++) {
				sb.append(i);
				sb.append(":");
				sb.append(instructions[i]);
				sb.append('\n');
			}

			logger.config(sb.toString());
		}
	}

	public static final Instruction guessFromInt(int anyInt) {
		Instruction instruction = Config.DEFAULT_CODE_VALUE;

		anyInt = (anyInt < 0) ? 0 : anyInt;
		anyInt = (anyInt < numInstructions) ? anyInt : anyInt % numInstructions;

		instruction = toInstruction(anyInt);

		return instruction;
	}

	public static final Instruction guessFromFloat(double anyFloat) {
		Instruction instruction = Config.DEFAULT_CODE_VALUE;

		anyFloat = (anyFloat < 0) ? -anyFloat : anyFloat;
		instruction = toInstruction((anyFloat < numInstructions) ? (int) anyFloat
				: (int) anyFloat % numInstructions);

		return instruction;
	}

	public static final Instruction guessFromBoolean(boolean anyBool) {
		return (anyBool) ? Instruction.CONST_TRUE : Instruction.CONST_FALSE;
	}

}
