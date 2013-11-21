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

	this.program = Program.Factory.createEmpty(sim.random);
	this.program.addInstructionsSafely("NOOP, BOOLEAN_AND, INTEGER_LESS_THAN, INTEGER_DIV, FLOAT_DUP, ACTION_RESOURCE_UNLOAD, FLOAT_DIV, 46, CONST_FLOAT_PI, BOOLEAN_YANKDUP, 44, 20, ACTION_RESOURCE_LOAD, ACTION_MOVE_RANDOM, 92, INTEGER_LESS_THAN_EQUALS, INTEGER_IS_ZERO, 0.5094370480905185, FLOAT_CBRT, 57, 59, ACTION_RESOURCE_PROCESS, FLOAT_TANH, CODE_COPY_FROM_IA, ACTION_RESOURCE_LOAD, ACTION_MOVE_N, ACTION_MOVE_N, ACTION_RESOURCE_LOAD, BOOLEAN_FROM_INTEGER, FLOAT_HYPOT, true, false, true, INTEGER_ADD, FLOAT_SIN, 0.6712607732566452, ACTION_MOVE_W, ACTION_RESOURCE_LOAD, 0.12429709590328752, 85, ACTION_MOVE_N, ACTION_RESOURCE_PROCESS, INTEGER_MOD, INTEGER_ROT, INTEGER_MOD, CONST_FLOAT_PI, INTEGER_ROT, ACTION_MOVE_N, 69, ACTION_RESOURCE_LOAD, ACTION_MOVE_CONDITIONALLY_NENW, 0.3458541539501294, ACTION_MOVE_TO_CLOSEST_HOME, ACTION_MOVE_TO_CLOSEST_HOME, INTEGER_DUP, true, FLOAT_ATAN2, FLOAT_FLOOR, FLOAT_EXP, FLOAT_EXP, INTEGER_MUL, 0.06022151786439711, ACTION_RESOURCE_UNLOAD, INSTRUCTION_REVERSE, FLOAT_ROT, ACTION_RESOURCE_EXTRACT, INTEGER_IS_POSITIVE, INTEGER_FROM_FLOAT, false, FLOAT_MAX, ACTION_RESOURCE_EXTRACT, ACTION_RESOURCE_LOAD, ACTION_MOVE_TO_CLOSEST_HOME, CODE_SWAP, INSTRUCTION_EXIT, ACTION_MOVE_NW, CONST_INT_ZERO, 0.7076314954307354, 76, 98, 40, 65, 0.5873084570258965, false, INTEGER_GREATER_THAN_EQUALS, FLOAT_COS, ACTION_FOLLOW_TRAIL, 40, 0.7370026640223912, 6, 27, FLOAT_LOG10, INTEGER_SIZE, FLOAT_SIZE, true, FLOAT_TAN, BOOLEAN_SWAP, CODE_DUP, ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE, ACTION_LEAVE_TRAIL, INTEGER_DUP, 0.6651684294509244, 42, 57, FLOAT_SUB, FLOAT_CBRT, ACTION_IS_CARRYING_PROCESSED_RESOURCE, CODE_YANKDUP, ACTION_MOVE_CONDITIONALLY_SESW, FLOAT_SIZE, CONST_TRUE, FLOAT_SQRT, INTEGER_SIZE, INTEGER_DUP, 0.9827098382985192, INTEGER_LESS_THAN, 43, 87, INTEGER_RAND, 13, ACTION_DETECT_RAW_RESOURCE, 76, FLOAT_ADD, INTEGER_FROM_FLOAT, INTEGER_IS_POSITIVE, 57, FLOAT_SUB, 46");
	VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
	vm.loadProgram(this.program);
	vm.setCpuCycles(sim.getMaxStepsPerAgent());
	this.setVirtualMachine(vm);
	
	
	/*
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
	*/

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
