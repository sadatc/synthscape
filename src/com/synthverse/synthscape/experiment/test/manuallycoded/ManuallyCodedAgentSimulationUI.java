/**
 * 
 */
package com.synthverse.synthscape.experiment.test.manuallycoded;

import sim.display.Console;
import sim.engine.SimState;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimulationUI;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationUI extends SimulationUI {

	public ManuallyCodedAgentSimulationUI() {
		super(new ManuallyCodedAgentSimulation(ManuallyCodedAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new ManuallyCodedAgentSimulationUI());
		c.setVisible(true);
	}
}
