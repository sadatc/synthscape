/**
 * 
 */
package com.synthverse.synthscape.experiment.test.manuallycoded;

import java.util.logging.Logger;

import sim.display.Console;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.SimpleSimulationUI;
import com.synthverse.util.LogUtils;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationUI extends SimpleSimulationUI {
    
    private static Logger logger = Logger.getLogger(ManuallyCodedAgentSimulationUI.class.getName());
    static {
    	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }



    public ManuallyCodedAgentSimulationUI() throws Exception {
	super(new ManuallyCodedAgentSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new ManuallyCodedAgentSimulationUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    logger.severe("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
