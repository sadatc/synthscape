/**
 * 
 */
package com.synthverse.synthscape.experiment.test.manuallycoded;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimulationFancyUI;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationFancyUI extends SimulationFancyUI {

	public ManuallyCodedAgentSimulationFancyUI() {
		super(new ManuallyCodedAgentSimulation(ManuallyCodedAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new ManuallyCodedAgentSimulationFancyUI());
		c.setVisible(true);
	}
}
