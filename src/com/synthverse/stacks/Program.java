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
	private int addIndex = 0;

	public MersenneTwisterFast randomNumberGenerator = null;

	public final void randomizeInstructions() {
		for (int i = 0; i < this.size; i++) {
			genotypeArray[i] = InstructionTranslator
					.getRandomInstruction(randomNumberGenerator);
		}
	}

	public final GenotypeInstruction[] getGenotypeArray() {
		return genotypeArray;
	}

	public final void setInstructionArray(GenotypeInstruction[] instructionArray) {
		this.genotypeArray = instructionArray;
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

	private Program(MersenneTwisterFast randomNumberGenerator, int size) {
		// restricted, use Factory methods to create programs.
		this.randomNumberGenerator = randomNumberGenerator;
		this.size = size;
		genotypeArray = new GenotypeInstruction[size];
	}

	public final void fillWithNOOP() {
		Arrays.fill(genotypeArray, Instruction.NOOP);
	}

	public final void clearAddIndex() {
		this.addIndex = 0;
	}

	public final boolean addInstructionSafely(GenotypeInstruction instruction) {
		if (addIndex < size) {
			genotypeArray[addIndex] = instruction;
			addIndex++;
			return true;
		} else {
			return false;

		}
	}

	public final boolean addInstructionsSafely(String csvString) {
		boolean result = true;

		if (csvString != null) {
			String[] instructions = csvString.split(",");
			for (String instruction : instructions) {
				if (!addInstructionSafely(InstructionTranslator
						.decodeFromString(instruction))) {
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

	public final boolean isIPValid(int ipIndex) {
		return (ipIndex >= 0 && ipIndex < size);
	}

	public final GenotypeInstruction getInstruction(int ipIndex) {
		return genotypeArray[ipIndex];
	}

	public final int getSignature() {
		int result = 0;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				result += genotypeArray[i].getSignature();
			}
		} else {
			return 0;
		}
		return result;
	}

	public final String getFingerPrint() {
		String result = "";
		if (size > 0) {
			for (int i = 0; i < size && i < 50; i++) {
				result += genotypeArray[i].getFingerPrint();
			}
		}
		return result;
	}

	public static double comparePrograms(Program current, Program previous) {
		double result = Double.NaN;

		if (current != null && previous != null) {

			GenotypeInstruction[] curr = current.getGenotypeArray();
			GenotypeInstruction[] prev = previous.getGenotypeArray();

			int largerLen = curr.length;
			int shorterLen = prev.length;
			if (prev.length > largerLen) {
				largerLen = prev.length;
				shorterLen = curr.length;
			}

			int mismatch = 0;
			int comparisons = 0;
			for (int i = 0; i < shorterLen; i++) {

				if (curr[i].getMetaInstructionType() == InstructionType.INSTRUCTION
						&& prev[i].getMetaInstructionType() == InstructionType.INSTRUCTION) {
					comparisons++;
					if (curr[i].getInstruction() != prev[i].getInstruction()) {
						mismatch++;
					}
				}
			}

			result = (double) mismatch / comparisons;
		}

		return result;

	}

	@Override
	public final String toString() {
		if (size > 0) {
			return GenotypeInstruction.arrayToString(genotypeArray);
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

		public static final Program createDefault(
				MersenneTwisterFast randomNumberGenerator) {
			return createRandom(randomNumberGenerator);
		}

		public static final Program createEmpty(
				MersenneTwisterFast randomNumberGenerator, int size) {
			Program program = new Program(randomNumberGenerator, size);
			return program;
		}

		public static final Program createEmpty(
				MersenneTwisterFast randomNumberGenerator) {
			return createEmpty(randomNumberGenerator,
					Config.DEFAULT_PROGRAM_ARRAY_SIZE);
		}

		public static final Program createRandom(
				MersenneTwisterFast randomNumberGenerator, int size) {

			Program program = createEmpty(randomNumberGenerator, size);

			for (int i = 0; i < size; i++) {
				program.genotypeArray[i] = InstructionTranslator
						.getRandomInstruction(randomNumberGenerator);
			}

			return program;

		}

		public static final Program createRandom(
				MersenneTwisterFast randomNumberGenerator) {
			return createRandom(randomNumberGenerator,
					Config.DEFAULT_PROGRAM_ARRAY_SIZE);
		}

	}

}
