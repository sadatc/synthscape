package com.synthverse.synthscape.experiment.evolutionary;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class HomoSim extends Simulation {

	public HomoSim(long seed) {
		super(seed);
		this.setNumberOfHomes(0);
		this.setNumberOfObstacles(0);
		this.setNumberOfAgents(50);
		//this.setNumberOfResourceA(50);
		this.setNumberOfResources(50);
		//this.setNumberOfResourceB(0);
		//this.setNumberOfResourceC(0);

	}

	public Agent generateAgent(long generation, long agentId, int x, int y) {
		HomogenousAgent homogenousAgent = new HomogenousAgent(this, generation,
				agentId, 150, 150, 1.0, 1.0, 1.0, 1.0, x, y);
		return homogenousAgent;
	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 5");
		doLoop(HomoSim.class, manualArgs);
		statistics.printExperimentSummary();
		System.exit(0);
	}

}
