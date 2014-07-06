/**
 * 
 */
package com.synthverse.synthscape.test.manuallycoded;

import java.util.logging.Logger;

import sim.display.Console;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.FancySimulationUI;
import com.synthverse.util.LogUtils;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationFancyUI extends FancySimulationUI {
    
    private static Logger logger = Logger.getLogger(ManuallyCodedAgentSimulationFancyUI.class.getName());
    static {
    	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }



    public ManuallyCodedAgentSimulationFancyUI() throws Exception {
	super(new ManuallyCodedAgentSimulation(Constants.UI_SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new ManuallyCodedAgentSimulationFancyUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    logger.severe("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
