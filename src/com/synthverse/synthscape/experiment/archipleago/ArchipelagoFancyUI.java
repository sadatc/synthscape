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
public class ArchipelagoFancyUI extends FancySimulationUI {

	public ArchipelagoFancyUI() {
		super(new ArchipelagoSimulation(IslanderAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new ArchipelagoFancyUI());
		c.setVisible(true);
	}
}
