/**
 * 
 */
package com.synthverse.synthscape.evolutionarymodel.embodied;

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
public class EmbodiedEvolutionSimulationFancyUI extends FancySimulationUI {

    private static Logger logger = Logger.getLogger(EmbodiedEvolutionSimulationFancyUI.class.getName());
    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    public EmbodiedEvolutionSimulationFancyUI() throws Exception {
	super(new EmbodiedEvolutionSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new EmbodiedEvolutionSimulationFancyUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    logger.severe("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
