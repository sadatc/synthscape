/**
 * 
 */
package com.synthverse.synthscape.experiment.dissertation.archipelago;

import java.io.IOException;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.FancySimulationUI;

/**
 * @author sadat
 * 
 */
public class ArchipelagoSimulationFancyUI extends FancySimulationUI {

    public ArchipelagoSimulationFancyUI() throws Exception {
	super(new ArchipelagoSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new ArchipelagoSimulationFancyUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    D.p("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
