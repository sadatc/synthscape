package com.synthverse.synthscape.experiment.test.manuallycoded;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class ManuallyCodedAgentSimulation extends Simulation {

	public ManuallyCodedAgentSimulation(AgentFactory agentFactory,
			ProblemComplexity problemComplexity, long seed) {
		super(agentFactory, problemComplexity, seed);
		this.setNumberOfCollectionSites(1);
		this.setNumberOfObstacles(10);
		this.setNumberOfAgents(10);

	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 1 -seed 2");
		doLoop(ManuallyCodedAgentSimulation.class, manualArgs);
		statistics.printExperimentSummary();
		System.exit(0);
	}

}
