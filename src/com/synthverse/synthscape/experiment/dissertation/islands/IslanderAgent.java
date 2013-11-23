/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.dissertation.islands;

import sim.engine.SimState;

import com.synthverse.stacks.Instruction;
import com.synthverse.stacks.MetaInstruction;
import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class IslanderAgent extends Agent {

    public IslanderAgent(Simulation simulation, Species species) {
	super(simulation, species);

    }

    public IslanderAgent(Simulation sim, Species species, int generationNumber, int maxSteps, int startX, int startY) {
	super(sim, species, generationNumber, maxSteps, startX, startY);

    }

    public void stepAction(SimState state) {
	this.getVirtualMachine().step();

    }

    @Override
    public double doubleValue() {
	double result = 0.0; // normal agent
	if (this.isCarryingResource) {
	    result = 1.0;
	}
	return result;

    }

    @Override
    protected void initGenotype() {
	super.initGenotype();

	/*
	this.program = Program.Factory.createEmpty(sim.random);
	this.program.addInstructionsSafely("ACTION_IS_CARRYING_PROCESSED_RESOURCE, CODE_SIZE, ACTION_MOVE_TO_CLOSEST_HOME, FLOAT_EXPM1, 64, ACTION_RESOURCE_UNLOAD, false, CONST_INT_ONE, ACTION_IS_CARRYING_RAW_RESOURCE, FLOAT_TANH, CODE_DUP, 0.007506131533814742, ACTION_RESOURCE_LOAD, ACTION_MOVE_RANDOM, ACTION_RESOURCE_LOAD, ACTION_MOVE_CONDITIONALLY_NENW, 80, true, 39, ACTION_MOVE_NE, 0.42871611997489056, ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE, FLOAT_SWAP, FLOAT_ATAN, ACTION_RESOURCE_LOAD, INTEGER_SUB, ACTION_MOVE_N, ACTION_RESOURCE_LOAD, ACTION_MOVE_E, ACTION_RESOURCE_LOAD, ACTION_MOVE_NW, 0.42755467635439226, INTEGER_IS_EVEN, CONST_TRUE, INTEGER_MAX, false, CONST_FLOAT_ONE, ACTION_RESOURCE_LOAD, ACTION_MOVE_E, ACTION_MOVE_NW, CODE_COPY_FROM_IA, ACTION_RESOURCE_PROCESS, BOOLEAN_FROM_INTEGER, INTEGER_LESS_THAN, CONST_FLOAT_ONE, FLOAT_CEIL, FLOAT_TANH, ACTION_IS_CARRYING_RAW_RESOURCE, 0.010949368693251027, ACTION_RESOURCE_LOAD, ACTION_MOVE_CONDITIONALLY_NENW, ACTION_RESOURCE_LOAD, ACTION_MOVE_TO_CLOSEST_HOME, ACTION_MOVE_TO_CLOSEST_HOME, FLOAT_ATAN2, INTEGER_MOD, CONST_INT_ONE, 0.5737076924227684, 0.44005631522792676, 0.27630329922301256, true, CODE_ROT, true, INTEGER_MOD, INTEGER_RAND, FLOAT_LOG, ACTION_RESOURCE_UNLOAD, INTEGER_SUB, FLOAT_LOG, INTEGER_RAND, ACTION_RESOURCE_EXTRACT, ACTION_RESOURCE_LOAD, ACTION_MOVE_TO_CLOSEST_HOME, 0.023449384210227575, INSTRUCTION_EXIT, ACTION_MOVE_W, ACTION_RESOURCE_UNLOAD, FLOAT_ACOS, INSTRUCTION_JUMP_BEGIN, BOOLEAN_FLUSH, true, FLOAT_EQUAL, FLOAT_DUP, 94, 41, 0.037110542976764305, INTEGER_RAND, FLOAT_YANK, NOOP, 0.11292256924622235, CODE_SIZE, FLOAT_FROM_INTEGER, BOOLEAN_FLUSH, 67, false, false, ACTION_DETECT_HOME, INSTRUCTION_HOP_LEFT, ACTION_DETECT_HOME, FLOAT_FLOOR, ACTION_MOVE_S, FLOAT_TANH, FLOAT_MIN, 17, FLOAT_SQRT, FLOAT_CBRT, CODE_DUP, INTEGER_ADD, INSTRUCTION_IF_FALSE, INTEGER_MAX, 6, 98, FLOAT_SIZE, 0.7994447245659463, 0.8125995902471375, 63, INTEGER_GREATER_THAN_EQUALS, 0.22493791488017656, 61, true, INTEGER_ROT, FLOAT_EXPM1, 90, FLOAT_COS, FLOAT_MOD, ACTION_DETECT_TRAIL, INTEGER_INCREMENT, 41");
	VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
	vm.loadProgram(this.program);
	vm.setCpuCycles(sim.getMaxStepsPerAgent());
	this.setVirtualMachine(vm);
	*/
	
	
	this.program = Program.Factory.createEmpty(sim.random);
	boolean spaceLeft = true;

	do {
	    this.program
		    .addInstructionSafely(new MetaInstruction(Instruction.ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_DETECT_HOME));
	    this.program
		    .addInstructionSafely(new MetaInstruction(Instruction.ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_DETECT_TRAIL));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_FOLLOW_TRAIL));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_IS_CARRYING_EXTRACTED_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_IS_CARRYING_PROCESSED_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_IS_CARRYING_RAW_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_IS_CARRYING_RESOURCE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_E));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_W));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_NE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_NW));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_SE));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_SW));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_MOVE_TO_CLOSEST_HOME));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_RESOURCE_EXTRACT));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_RESOURCE_LOAD));
	    this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_RESOURCE_PROCESS));
	    spaceLeft = this.program.addInstructionSafely(new MetaInstruction(Instruction.ACTION_RESOURCE_UNLOAD));
	} while (spaceLeft);

	VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
	vm.loadProgram(this.program);
	vm.setCpuCycles(sim.getMaxStepsPerAgent());
	this.setVirtualMachine(vm);
	

	//
	// this.program.addInstructionSafely(Instruction.ACTION_DETECT_RAW_RESOURCE);
	// logger.info(this.program.toTranslatedString());

	// this.program = Program.Factory.createEmpty(sim.random);
	// this.program.addInstructionSafely(Instruction.ACTION_DETECT_RAW_RESOURCE);
	// this.program.addInstructionSafely(Instruction.ACTION_DETECT_RAW_RESOURCE);
	// start here tomorrow morning

	/*
	 * this.program = Program.Factory.createRandom(sim.random);
	 * VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this,
	 * sim.random); vm.loadProgram(this.program);
	 * vm.setCpuCycles(sim.getMaxStepsPerAgent());
	 * this.setVirtualMachine(vm);
	 */
    }

}
