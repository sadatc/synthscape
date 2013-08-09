/**
 * 
 */
package com.synthverse.synthscape.experiment.dissertation.archipelago;

import java.io.IOException;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class ArchipelagoSimulationUI extends SimpleSimulationUI {

    public ArchipelagoSimulationUI() throws IOException {
	super(new ArchipelagoSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new ArchipelagoSimulationUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    D.p("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
