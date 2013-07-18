/**
 * 
 */
package com.synthverse.synthscape.experiment.test.basic;

import sim.display.Console;
import sim.engine.SimState;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimulationUI;

/**
 * @author sadat
 * 
 */
public class TestSimUI extends SimulationUI {

	public TestSimUI() {
		super(new TestSim(TestAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new TestSimUI());
		c.setVisible(true);
	}
}
