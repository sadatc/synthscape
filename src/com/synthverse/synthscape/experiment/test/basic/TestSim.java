package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class TestSim extends Simulation {

	public TestSim(long seed) {
		super(seed);
		this.setNumberOfCollectionSites(1);
		this.setNumberOfObstacles(10);
		this.setNumberOfAgents(10);

	}

	public Agent generateAgent(long generation, long agentId, int x, int y) {
		TestAgent agent = new TestAgent(this, agentId, 200, x, y);

		return agent;
	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 1 -seed 2");
		doLoop(TestSim.class, manualArgs);
		statistics.printExperimentSummary();
		System.exit(0);
	}

}
