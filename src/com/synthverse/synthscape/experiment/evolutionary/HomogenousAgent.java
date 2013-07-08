/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.evolutionary;

import sim.engine.SimState;

import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.D;

@SuppressWarnings("serial")
public class HomogenousAgent extends Agent {

	boolean carryingResource = false;

	HomogenousAgent(Simulation sim, long generation, long agentId, int energy,
			int maxEnergy, double visionCapability,
			double extractionCapability, double transportationCapability,
			double communicationCapability, int startX, int startY) {

		super(sim, generation, agentId, energy, maxEnergy, visionCapability,
				extractionCapability, transportationCapability,
				communicationCapability, startX, startY);

		virtualMachine = VirtualMachine.Factory.createDefault(sim, this,
				sim.random);
		virtualMachine.setCpuCycles(energy);

		program = Program.Factory.createRandom(sim.random);
		D.p(program.toString());

		virtualMachine.loadProgram(program);

	}

	public double doubleValue() {
		return 0;
	}

	public void stepAction(SimState state) {

		virtualMachine.step();

	}

}
