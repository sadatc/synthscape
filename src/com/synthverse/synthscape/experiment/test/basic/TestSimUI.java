/**
 * 
 */
package com.synthverse.synthscape.experiment.test.basic;

import java.io.IOException;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class TestSimUI extends SimpleSimulationUI {

    public TestSimUI() throws IOException {
	super(new TestSim(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new TestSimUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    D.p("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
