/**
 * 
 */
package com.synthverse.synthscape.experiment.test.manuallycoded;

import java.io.IOException;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.FancySimulationUI;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationFancyUI extends FancySimulationUI {

    public ManuallyCodedAgentSimulationFancyUI() throws IOException {
	super(new ManuallyCodedAgentSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	try {
	    Console c = new Console(new ManuallyCodedAgentSimulationFancyUI());
	    c.setVisible(true);
	} catch (Exception e) {
	    D.p("Exception:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
