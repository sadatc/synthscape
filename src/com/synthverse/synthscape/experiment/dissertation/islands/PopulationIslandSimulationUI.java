/**
 * 
 */
package com.synthverse.synthscape.experiment.dissertation.islands;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class PopulationIslandSimulationUI extends SimpleSimulationUI {

    public PopulationIslandSimulationUI() throws Exception {
	super(new PopulationIslandSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new PopulationIslandSimulationUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    D.p("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
