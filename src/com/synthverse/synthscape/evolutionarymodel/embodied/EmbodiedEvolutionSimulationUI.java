/**
 * 
 */
package com.synthverse.synthscape.evolutionarymodel.embodied;

import java.util.logging.Logger;

import sim.display.Console;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.gui.SimpleSimulationUI;
import com.synthverse.util.LogUtils;

/**
 * @author sadat
 * 
 */
public class EmbodiedEvolutionSimulationUI extends SimpleSimulationUI {
	private static Logger logger = Logger
			.getLogger(EmbodiedEvolutionSimulationUI.class.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public EmbodiedEvolutionSimulationUI() throws Exception {
		super(new EmbodiedEvolutionSimulation(Constants.UI_SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		try {
			Console c = new Console(new EmbodiedEvolutionSimulationUI());
			c.setVisible(true);
		} catch (Exception e) {
			logger.severe("Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
}
