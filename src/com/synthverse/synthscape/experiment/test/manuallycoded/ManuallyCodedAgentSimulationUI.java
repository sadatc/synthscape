/**
 * 
 */
package com.synthverse.synthscape.experiment.test.manuallycoded;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationUI extends SimpleSimulationUI {

	public ManuallyCodedAgentSimulationUI() {
		super(new ManuallyCodedAgentSimulation("MANUALLY_CODED",
				ManuallyCodedAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new ManuallyCodedAgentSimulationUI());
		c.setVisible(true);
	}
}
