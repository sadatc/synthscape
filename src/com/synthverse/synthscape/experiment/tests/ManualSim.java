package com.synthverse.synthscape.experiment.tests;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class ManualSim extends Simulation {

	public ManualSim(long seed) {
		super(seed);
		this.setNumberOfHomes(0);
		this.setNumberOfObstacles(0);
		this.setNumberOfAgents(50);
		this.setNumberOfResourceA(50);
		this.setNumberOfResourceB(0);
		this.setNumberOfResourceC(0);

	}

	public Agent generateAgent(long generation, long agentId, int x, int y) {
		NCMHomoAgent nCMHomoAgent = new NCMHomoAgent(this, generation,
				agentId, 150, 150, 1.0, 1.0, 1.0, 1.0, x, y);
		return nCMHomoAgent;
	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 1 -seed 2");
		doLoop(ManualSim.class, manualArgs);
		statistics.printExperimentSummary();
		System.exit(0);
	}

}
