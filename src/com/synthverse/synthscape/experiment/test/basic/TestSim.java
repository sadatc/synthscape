package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class TestSim extends Simulation {

	public TestSim(AgentFactory agentFactory, long seed) {
		super(agentFactory, seed);
		this.setNumberOfCollectionSites(1);
		this.setNumberOfObstacles(10);
		this.setNumberOfAgents(10);

	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 1 -seed 2");
		doLoop(TestSim.class, manualArgs);
		statistics.printExperimentSummary();
		System.exit(0);
	}

}
