/**
 * 
 */
package com.synthverse.synthscape.experiment.test.basic;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class TestSimUI extends SimpleSimulationUI {

    public TestSimUI() {
	super(new TestSim(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	Console c = new Console(new TestSimUI());
	c.setVisible(true);
    }
}
