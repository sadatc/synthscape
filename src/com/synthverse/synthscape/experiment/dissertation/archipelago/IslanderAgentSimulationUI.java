/**
 * 
 */
package com.synthverse.synthscape.experiment.dissertation.archipelago;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class IslanderAgentSimulationUI extends SimpleSimulationUI {

	public IslanderAgentSimulationUI() {
		super(new IslanderAgentSimulation(IslanderAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new IslanderAgentSimulationUI());
		c.setVisible(true);
	}
}
