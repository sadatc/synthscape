/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.dissertation.islands;

import sim.engine.SimState;

import com.synthverse.stacks.Instruction;
import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.D;
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
	//this.program = Program.Factory.createEmpty(sim.random);
	//this.program.addInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE);
	logger.info(this.program.toTranslatedString()); 
	
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
