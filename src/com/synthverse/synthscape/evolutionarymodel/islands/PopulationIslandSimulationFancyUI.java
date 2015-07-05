/**
 * 
 */
package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.gui.FancySimulationUI;
import com.synthverse.util.LogUtils;

import sim.display.Console;

/**
 * @author sadat
 * 
 */
public class PopulationIslandSimulationFancyUI extends FancySimulationUI {

	private static Logger logger = Logger.getLogger(PopulationIslandSimulationFancyUI.class.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public PopulationIslandSimulationFancyUI() throws Exception {
		super(new PopulationIslandViewModel(Constants.UI_SIMULATION_RNG_SEED));
		Thread t = new Thread(new PopulationIslandSimulation.CoreSimThread(),
				"CoreSimThread");
		t.start();

	}

	public static void main(String[] args) {
		try {
			Console c = new Console(new PopulationIslandSimulationFancyUI());
			c.setVisible(true);
		} catch (Exception e) {
			logger.severe("Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
}
