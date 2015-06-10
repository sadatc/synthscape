package com.synthverse.stacks;

import java.util.HashMap;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.util.LogUtils;

public class GenotypeInstruction implements Constants {
	private static Logger logger = Logger.getLogger(GenotypeInstruction.class
			.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public int getSignature() {

		int result = 0;
		if (instructionType == InstructionType.INSTRUCTION) {
			result = instruction.ordinal();
		} else if (instructionType == InstructionType.INT_VALUE) {
			result = intValue;
		} else if (instructionType == InstructionType.BOOL_VALUE) {
			result = booleanValue ? 1 : 0;
		} else if (instructionType == InstructionType.FLOAT_VALUE) {
			result = (int) floatValue;
		} else {
			logger.severe("FATAL error in determining instruction type. Program should have never reached here...");
			System.exit(1);
		}

		return result;

	}

	public char getFingerPrint() {

		char result = '_';
		if (instructionType == InstructionType.INSTRUCTION) {
			result = instruction.getMnemonic().charAt(0);
		} else if (instructionType == InstructionType.INT_VALUE) {
			result = (Integer.toString(intValue)).charAt(0);
		} else if (instructionType == InstructionType.BOOL_VALUE) {
			result = booleanValue ? 'T' : 'F';
		} else if (instructionType == InstructionType.FLOAT_VALUE) {
			result = (Double.toString(floatValue)).charAt(0);
		} else {
			logger.severe("FATAL error in determining instruction type. Program should have never reached here...");
			System.exit(1);
		}

		return result;

	}

	@Override
	public String toString() {

		String result = EMPTY_STRING;
		if (instructionType == InstructionType.INSTRUCTION) {
			result = instruction.toString();
		} else if (instructionType == InstructionType.INT_VALUE) {
			result = Integer.toString(intValue);
		} else if (instructionType == InstructionType.BOOL_VALUE) {
			result = Boolean.toString(booleanValue);
		} else if (instructionType == InstructionType.FLOAT_VALUE) {
			result = Double.toString(floatValue);
		} else {
			logger.info("FATAL error in determining instruction type. Program should have never reached here...");
			System.exit(1);
		}

		return result;
	}

	public static String arrayToString(GenotypeInstruction[] array) {
		if (array == null)
			return "null";

		int iMax = array.length - 1;
		if (iMax == -1)
			return "[]";

		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0;; i++) {
			b.append(String.valueOf(array[i]));
			if (i == iMax)
				return b.append(']').toString();
			b.append("|");
		}
	}

	private InstructionType instructionType;
	private Instruction instruction;
	private int intValue;
	private double floatValue;
	private boolean booleanValue;

	public InstructionType getMetaInstructionType() {
		return instructionType;
	}

	public void setMetaInstructionType(InstructionType instructionType) {
		this.instructionType = instructionType;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public double getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(double floatValue) {
		this.floatValue = floatValue;
	}

	public boolean isBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	static HashMap<Instruction, GenotypeInstruction> cachedMetaInstructions = new HashMap<Instruction, GenotypeInstruction>();

	public static GenotypeInstruction fromInstruction(Instruction instruction) {
		GenotypeInstruction result = cachedMetaInstructions.get(instruction);
		if (result == null) {
			result = new GenotypeInstruction(instruction);
			cachedMetaInstructions.put(instruction, result);
		}
		return result;
	}

	private GenotypeInstruction(Instruction instruction) {
		// logger.info("==> GenotypeInstruction(instruction)");
		setMetaInstructionType(InstructionType.INSTRUCTION);
		setInstruction(instruction);
	}

	public GenotypeInstruction(int intValue) {
		// logger.info("==> GenotypeInstruction(int)");
		setMetaInstructionType(InstructionType.INT_VALUE);
		setIntValue(intValue);
	}

	public GenotypeInstruction(double floatValue) {
		// logger.info("==> GenotypeInstruction(double)");
		setMetaInstructionType(InstructionType.FLOAT_VALUE);
		setFloatValue(floatValue);
	}

	public GenotypeInstruction(boolean booleanValue) {
		// logger.info("==> GenotypeInstruction(boolean)");
		setMetaInstructionType(InstructionType.BOOL_VALUE);
		setBooleanValue(booleanValue);
	}

	public static GenotypeInstruction NOOP = new GenotypeInstruction(
			Instruction.NOOP);
	public static GenotypeInstruction CONST_TRUE = new GenotypeInstruction(
			Instruction.CONST_TRUE);
	public static GenotypeInstruction CONST_FALSE = new GenotypeInstruction(
			Instruction.CONST_FALSE);

	public void execute(VirtualMachine virtualMachine) {
		if (instructionType == InstructionType.INSTRUCTION) {
			instruction.execute(virtualMachine);
		} else if (instructionType == InstructionType.INT_VALUE) {
			virtualMachine.getIntegerStack().push(intValue);
			virtualMachine.incrementIP();
		} else if (instructionType == InstructionType.BOOL_VALUE) {
			virtualMachine.getBooleanStack().push(booleanValue);
			virtualMachine.incrementIP();
		} else if (instructionType == InstructionType.FLOAT_VALUE) {
			virtualMachine.getFloatStack().push(floatValue);
			virtualMachine.incrementIP();
		} else {
			logger.severe("FATAL error in determining instruction type. Program should have never reached here...");
			System.exit(1);
		}
		// logger.info("executing"+this);

	}

}
