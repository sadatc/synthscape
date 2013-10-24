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
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_DETECT_HOME));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_DETECT_TRAIL));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_FOLLOW_TRAIL));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_IS_CARRYING_EXTRACTED_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_IS_CARRYING_PROCESSED_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_IS_CARRYING_RAW_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_IS_CARRYING_RESOURCE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_E));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_W));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_NE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_NW));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_SE));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_SW));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_MOVE_TO_CLOSEST_HOME));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_RESOURCE_EXTRACT));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_RESOURCE_LOAD));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_RESOURCE_PROCESS));
	this.program.addInstruction(new MetaInstruction(Instruction.ACTION_RESOURCE_UNLOAD));
	
	VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
	vm.loadProgram(this.program);
	vm.setCpuCycles(sim.getMaxStepsPerAgent());
	this.setVirtualMachine(vm);
	
	
	
	
	//
	//this.program.addInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE);
	//logger.info(this.program.toTranslatedString()); 
	
	//this.program = Program.Factory.createEmpty(sim.random);
	//this.program.addInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE);
	//this.program.addInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE);
	// start here tomorrow morning
	
	/*
	this.program = Program.Factory.createRandom(sim.random);
	VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
	vm.loadProgram(this.program);
	vm.setCpuCycles(sim.getMaxStepsPerAgent());
	this.setVirtualMachine(vm);
	*/
    }

}
