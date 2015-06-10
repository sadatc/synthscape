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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.ProblemComplexity;
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

	private static ArrayList<Instruction> instructionArray = new ArrayList<Instruction>();

	private static HashMap<String, Instruction> instructionTable = new HashMap<String, Instruction>();

	private static EnumMap<Instruction, Integer> instruction2CodeMap = new EnumMap<Instruction, Integer>(
			Instruction.class);

	private static int numInstructions;

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
		setupInstructionTable();
		numInstructions = instructionArray.size();
		instructions = new Instruction[numInstructions];
		for (int i = 0; i < numInstructions; i++) {
			Instruction theInstruction = instructionArray.get(i);
			instructions[i] = theInstruction;
			instruction2CodeMap.put(theInstruction, i);
		}
	}

	private static void setupInstructionTable() {

		Instruction[] allInstructions = Instruction.values();

		for (Instruction instruction : allInstructions) {

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.THREE_SEQUENTIAL_TASKS) {
				// filter out process instructions for 3 task problems.
				if (instruction.name().contains("PROCESS")) {
					continue;
				}
			}

			// now depending on interactions -- pick and choose instructions
			if (Main.settings.MODEL_INTERACTIONS.toLowerCase().contains("none")) {
				// no instructions -- drop trail, broad, unicast
				if (instruction.name().contains("TRAIL")
						|| instruction.name().contains("BROADCAST")
						|| instruction.name().contains("UNICAST")) {
					continue;
				}
			} else if (Main.settings.MODEL_INTERACTIONS.toLowerCase().contains(
					"trail")) {
				if (instruction.name().contains("BROADCAST")
						|| instruction.name().contains("UNICAST")) {
					continue;
				}
			} else if (Main.settings.MODEL_INTERACTIONS.toLowerCase().contains(
					"broadcast")) {
				if (instruction.name().contains("TRAIL")
						|| instruction.name().contains("UNICAST")) {
					continue;
				}
			} else if (Main.settings.MODEL_INTERACTIONS.toLowerCase().contains(
					"unicast_n")) {
				if (instruction.name().contains("TRAIL")
						|| instruction.name().contains("BROADCAST")) {
					continue;
				}
			}

			instructionTable.put(instruction.toString(), instruction);
			instructionArray.add(instruction);
		}

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
		return instruction2CodeMap.get(instruction);
	}

	/**
	 * This will return a valid random numeric code. Useful for random code
	 * generation.
	 * 
	 * @return
	 */
	public static final int getRandomCode(
			MersenneTwisterFast randomNumberGenerator) {
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
				result = GenotypeInstruction.fromInstruction(instructionTable
						.get(instruction));
			} else if (instruction.equals("TRUE")) {
				result = new GenotypeInstruction(true);
			} else if (instruction.equals("FALSE")) {
				result = new GenotypeInstruction(false);
			} else if (isNumeric(instruction)) {
				if (instruction.contains(".")) {
					result = new GenotypeInstruction(
							Double.parseDouble(instruction));
				} else {
					result = new GenotypeInstruction(
							Integer.parseInt(instruction));
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
	public static final GenotypeInstruction getRandomInstruction(
			MersenneTwisterFast randomNumberGenerator) {

		GenotypeInstruction result = GenotypeInstruction.NOOP;
		// instructions, literals (bool, double, int) are all equally likely...

		double dice = randomNumberGenerator.nextDouble();
		if (dice > 0.9) {
			result = new GenotypeInstruction(randomNumberGenerator.nextDouble());

		} else if (dice > 0.85) {
			result = new GenotypeInstruction(
					randomNumberGenerator.nextBoolean());

		} else if (dice > 0.7) {
			result = new GenotypeInstruction(randomNumberGenerator.nextInt(100));

		} else {

			result = GenotypeInstruction
					.fromInstruction(instructions[getRandomCode(randomNumberGenerator)]);
		}

		return result;
	}

	public static final void logStatus() {

		StringBuilder sb = new StringBuilder(">>> adding ");
		sb.append(numInstructions);
		sb.append(" instructions to the vocabulary...");
		logger.info(sb.toString());

		for (Instruction instruction : instructions) {
			D.p(instruction.toString());
		}

		logger.info("<<< done adding.\n");

	}

}
