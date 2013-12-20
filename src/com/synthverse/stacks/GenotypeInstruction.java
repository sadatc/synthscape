package com.synthverse.stacks;

import java.util.HashMap;

import com.synthverse.synthscape.core.D;

public class GenotypeInstruction {
    
    public int getSignature() {
	
	int result = 0;
	if (instructionType == InstructionType.INSTRUCTION) {
	    result = instruction.ordinal();
	} else if (instructionType == InstructionType.INT_VALUE) {
	    result = intValue;
	} else if (instructionType == InstructionType.BOOL_VALUE) {
	    result = booleanValue?1:0;
	} else if (instructionType == InstructionType.FLOAT_VALUE) {
	    result = (int)floatValue;
	} else {
	    D.p("FATAL error in determining instruction type. Program should have never reached here...");
	    System.exit(1);
	}
	
	
	return result;
	
    }
    

    @Override
    public String toString() {
	
	String result = "";
	if (instructionType == InstructionType.INSTRUCTION) {
	    result = instruction.toString();
	} else if (instructionType == InstructionType.INT_VALUE) {
	    result = ""+intValue;
	} else if (instructionType == InstructionType.BOOL_VALUE) {
	    result = ""+booleanValue;
	} else if (instructionType == InstructionType.FLOAT_VALUE) {
	    result = ""+floatValue;
	} else {
	    D.p("FATAL error in determining instruction type. Program should have never reached here...");
	    System.exit(1);
	}
	
	
	return result;
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
    
    private GenotypeInstruction() {
	throw new AssertionError("Evolver constructor is restricted");
    }
    
    static HashMap<Instruction,GenotypeInstruction> cachedMetaInstructions = new HashMap<Instruction,GenotypeInstruction>();
    
    public static GenotypeInstruction fromInstruction(Instruction instruction) {
	GenotypeInstruction result = cachedMetaInstructions.get(instruction);
	if(result == null) {
	    result = new GenotypeInstruction(instruction);
	    cachedMetaInstructions.put(instruction, result);
	}
	return result;
    }
    
    

    private GenotypeInstruction(Instruction instruction) {
	//D.p("==> GenotypeInstruction(instruction)");
	setMetaInstructionType(InstructionType.INSTRUCTION);
	setInstruction(instruction);
    }

    public GenotypeInstruction(int intValue) {
	//D.p("==> GenotypeInstruction(int)");
	setMetaInstructionType(InstructionType.INT_VALUE);
	setIntValue(intValue);
    }

    public GenotypeInstruction(double floatValue) {
	//D.p("==> GenotypeInstruction(double)");
	setMetaInstructionType(InstructionType.FLOAT_VALUE);
	setFloatValue(floatValue);
    }

    public GenotypeInstruction(boolean booleanValue) {
	//D.p("==> GenotypeInstruction(boolean)");
	setMetaInstructionType(InstructionType.BOOL_VALUE);
	setBooleanValue(booleanValue);
    }

    public static GenotypeInstruction NOOP = new GenotypeInstruction(Instruction.NOOP);
    public static GenotypeInstruction CONST_TRUE = new GenotypeInstruction(Instruction.CONST_TRUE);
    public static GenotypeInstruction CONST_FALSE = new GenotypeInstruction(Instruction.CONST_FALSE);

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
	    D.p("FATAL error in determining instruction type. Program should have never reached here...");
	    System.exit(1);
	}
	//D.p("executing"+this);

    }

}
