/**
 * 
 */
package com.synthverse.synthscape.experiment.dissertation.islands;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.FancySimulationUI;

/**
 * @author sadat
 * 
 */
public class PopulationIslandSimulationFancyUI extends FancySimulationUI {

    public PopulationIslandSimulationFancyUI() throws Exception {
	super(new PopulationIslandSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new PopulationIslandSimulationFancyUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    D.p("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
