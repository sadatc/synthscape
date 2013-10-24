package com.synthverse.stacks;

import com.synthverse.synthscape.core.D;

public class MetaInstruction {

    @Override
    public String toString() {
	
	String result = "";
	if (metaInstructionType == MetaInstructionType.INSTRUCTION) {
	    result = instruction.toString();
	} else if (metaInstructionType == MetaInstructionType.INT_VALUE) {
	    result = ""+intValue;
	} else if (metaInstructionType == MetaInstructionType.BOOL_VALUE) {
	    result = ""+booleanValue;
	} else if (metaInstructionType == MetaInstructionType.FLOAT_VALUE) {
	    result = ""+floatValue;
	} else {
	    D.p("FATAL error in determining instruction type. Program should have never reached here...");
	    System.exit(1);
	}
	
	
	return result;
    }

    private MetaInstructionType metaInstructionType;
    private Instruction instruction;
    private int intValue;
    private double floatValue;
    private boolean booleanValue;

    public MetaInstructionType getMetaInstructionType() {
	return metaInstructionType;
    }

    public void setMetaInstructionType(MetaInstructionType metaInstructionType) {
	this.metaInstructionType = metaInstructionType;
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

    public MetaInstruction(Instruction instruction) {
	setMetaInstructionType(MetaInstructionType.INSTRUCTION);
	setInstruction(instruction);
    }

    public MetaInstruction(int intValue) {
	setMetaInstructionType(MetaInstructionType.INT_VALUE);
	setIntValue(intValue);
    }

    public MetaInstruction(double floatValue) {
	setMetaInstructionType(MetaInstructionType.FLOAT_VALUE);
	setFloatValue(floatValue);
    }

    public MetaInstruction(boolean booleanValue) {
	setMetaInstructionType(MetaInstructionType.BOOL_VALUE);
	setBooleanValue(booleanValue);
    }

    public static MetaInstruction NOOP = new MetaInstruction(Instruction.NOOP);
    public static MetaInstruction CONST_TRUE = new MetaInstruction(Instruction.CONST_TRUE);
    public static MetaInstruction CONST_FALSE = new MetaInstruction(Instruction.CONST_FALSE);

    public void execute(VirtualMachine virtualMachine) {
	if (metaInstructionType == MetaInstructionType.INSTRUCTION) {
	    instruction.execute(virtualMachine);
	} else if (metaInstructionType == MetaInstructionType.INT_VALUE) {
	    virtualMachine.getIntegerStack().push(intValue);
	    virtualMachine.incrementIP();
	} else if (metaInstructionType == MetaInstructionType.BOOL_VALUE) {
	    virtualMachine.getBooleanStack().push(booleanValue);
	    virtualMachine.incrementIP();
	} else if (metaInstructionType == MetaInstructionType.FLOAT_VALUE) {
	    virtualMachine.getFloatStack().push(floatValue);
	    virtualMachine.incrementIP();
	} else {
	    D.p("FATAL error in determining instruction type. Program should have never reached here...");
	    System.exit(1);
	}
	//D.p("executing"+this);

    }

}
