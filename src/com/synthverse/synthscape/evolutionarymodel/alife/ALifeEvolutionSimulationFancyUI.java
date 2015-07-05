/**
 * 
 */
package com.synthverse.synthscape.evolutionarymodel.alife;

import java.util.logging.Logger;

import sim.display.Console;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.gui.FancySimulationUI;
import com.synthverse.util.LogUtils;

/**
 * @author sadat
 * 
 */
public class ALifeEvolutionSimulationFancyUI extends FancySimulationUI {

	private static Logger logger = Logger
			.getLogger(ALifeEvolutionSimulationFancyUI.class.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public ALifeEvolutionSimulationFancyUI() throws Exception {
		super(new ALifeEvolutionSimulation(Constants.UI_SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		try {
			Console c = new Console(new ALifeEvolutionSimulationFancyUI());
			c.setVisible(true);
		} catch (Exception e) {
			logger.severe("Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
}
