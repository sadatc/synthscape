/**
 * 
 */
package com.synthverse.synthscape.experiment.test.basic;

import java.util.logging.Level;
import java.util.logging.Logger;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.SimpleSimulationUI;
import com.synthverse.util.LogUtils;

/**
 * @author sadat
 * 
 */
public class TestSimUI extends SimpleSimulationUI {
    private static Logger logger = Logger.getLogger(TestSimUI.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Level.ALL);
    }

    public TestSimUI() throws Exception {
	super(new TestSim(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new TestSimUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    logger.severe("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
