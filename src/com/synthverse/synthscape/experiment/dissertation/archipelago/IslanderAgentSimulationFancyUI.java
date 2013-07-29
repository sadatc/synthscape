/**
 * 
 */
package com.synthverse.synthscape.experiment.dissertation.archipelago;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.FancySimulationUI;
import com.synthverse.synthscape.core.ProblemComplexity;

/**
 * @author sadat
 * 
 */
public class IslanderAgentSimulationFancyUI extends FancySimulationUI {

	public IslanderAgentSimulationFancyUI() {
		super(new IslanderAgentSimulation(IslanderAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new IslanderAgentSimulationFancyUI());
		c.setVisible(true);
	}
}
