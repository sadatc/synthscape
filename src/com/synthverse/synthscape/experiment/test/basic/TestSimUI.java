/**
 * 
 */
package com.synthverse.synthscape.experiment.test.basic;

import sim.display.Console;
import sim.engine.SimState;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.SimulationUI;

/**
 * @author sadat
 * 
 */
public class TestSimUI extends SimulationUI {

	public TestSimUI() {
		super(new TestSim(Constants.SIMULATION_RNG_SEED));

	}

	public TestSimUI(SimState state) {
		super(state);

	}

	public static void main(String[] args) {
		Console c = new Console(new TestSimUI());
		c.setVisible(true);
	}
}
