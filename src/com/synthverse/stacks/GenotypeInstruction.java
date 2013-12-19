package com.synthverse.stacks;

import java.util.HashMap;

import com.synthverse.synthscape.core.D;

public class GenotypeInstruction {
    
    public int getSignature() {
	
	int result = 0;
	if (metaInstructionType == MetaInstructionType.INSTRUCTION) {
	    result = instruction.ordinal();
	} else if (metaInstructionType == MetaInstructionType.INT_VALUE) {
	    result = intValue;
	} else if (metaInstructionType == MetaInstructionType.BOOL_VALUE) {
	    result = booleanValue?1:0;
	} else if (metaInstructionType == MetaInstructionType.FLOAT_VALUE) {
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
	setMetaInstructionType(MetaInstructionType.INSTRUCTION);
	setInstruction(instruction);
    }

    public GenotypeInstruction(int intValue) {
	//D.p("==> GenotypeInstruction(int)");
	setMetaInstructionType(MetaInstructionType.INT_VALUE);
	setIntValue(intValue);
    }

    public GenotypeInstruction(double floatValue) {
	//D.p("==> GenotypeInstruction(double)");
	setMetaInstructionType(MetaInstructionType.FLOAT_VALUE);
	setFloatValue(floatValue);
    }

    public GenotypeInstruction(boolean booleanValue) {
	//D.p("==> GenotypeInstruction(boolean)");
	setMetaInstructionType(MetaInstructionType.BOOL_VALUE);
	setBooleanValue(booleanValue);
    }

    public static GenotypeInstruction NOOP = new GenotypeInstruction(Instruction.NOOP);
    public static GenotypeInstruction CONST_TRUE = new GenotypeInstruction(Instruction.CONST_TRUE);
    public static GenotypeInstruction CONST_FALSE = new GenotypeInstruction(Instruction.CONST_FALSE);

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
