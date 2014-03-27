/**
 * 
 */
package com.synthverse.synthscape.evolutionarymodel.embodied;

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
public class PopulationIslandSimulationUI extends SimpleSimulationUI {
    private static Logger logger = Logger.getLogger(PopulationIslandSimulationUI.class.getName());
    static {
    	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }



    public PopulationIslandSimulationUI() throws Exception {
	super(new EmbodiedEvolutionSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new PopulationIslandSimulationUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    logger.severe("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
