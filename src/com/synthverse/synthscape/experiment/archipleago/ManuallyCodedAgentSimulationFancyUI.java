/**
 * 
 */
package com.synthverse.synthscape.experiment.archipleago;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.FancySimulationUI;
import com.synthverse.synthscape.core.ProblemComplexity;

/**
 * @author sadat
 * 
 */
public class ManuallyCodedAgentSimulationFancyUI extends FancySimulationUI {

    public ManuallyCodedAgentSimulationFancyUI() {
	super(new ManuallyCodedAgentSimulation(Constants.SIMULATION_RNG_SEED));
    }

    public static void main(String[] args) {
	Console c = new Console(new ManuallyCodedAgentSimulationFancyUI());
	c.setVisible(true);
    }
}
