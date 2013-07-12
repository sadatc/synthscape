/**
 * 
 */
package com.synthverse.synthscape.experiment.tests;

import sim.display.Console;
import sim.engine.SimState;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.SimulationUI;

/**
 * @author sadat
 * 
 */
public class ManualSimUI extends SimulationUI {

	public ManualSimUI() {
		super(new ManualSim(Constants.SIMULATION_RNG_SEED));

	}

	public ManualSimUI(SimState state) {
		super(state);

	}

	public static void main(String[] args) {
		Console c = new Console(new ManualSimUI());
		c.setVisible(true);
	}
}
