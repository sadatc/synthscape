/**
 * 
 */
package com.synthverse.synthscape.experiment.archipleago;

import sim.display.Console;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.SimpleSimulationUI;

/**
 * @author sadat
 * 
 */
public class ArchipelagoUI extends SimpleSimulationUI {

	public ArchipelagoUI() {
		super(new ArchipelagoSimulation("ARCHIPELAGO",
				IslanderAgentFactory.getInstance(),
				ProblemComplexity.FOUR_SEQUENTIAL_TASKS,
				Constants.SIMULATION_RNG_SEED));
	}

	public static void main(String[] args) {
		Console c = new Console(new ArchipelagoUI());
		c.setVisible(true);
	}
}
