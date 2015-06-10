/**
 * 
 */
package com.synthverse.synthscape.evolutionarymodel.alife;

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
public class ALifeEvolutionSimulationUI extends SimpleSimulationUI {
	private static Logger logger = Logger
			.getLogger(ALifeEvolutionSimulationUI.class.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public ALifeEvolutionSimulationUI() throws Exception {
		super(new ALifeEvolutionSimulation(Constants.UI_SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		try {
			Console c = new Console(new ALifeEvolutionSimulationUI());
			c.setVisible(true);
		} catch (Exception e) {
			logger.severe("Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
}