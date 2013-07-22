package com.synthverse.synthscape.experiment.test.manuallycoded;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class ManuallyCodedAgentSimulation extends Simulation {

	public ManuallyCodedAgentSimulation(AgentFactory agentFactory,
			ProblemComplexity problemComplexity, long seed) {
		super(agentFactory, problemComplexity, seed);
		this.setNumberOfCollectionSites(5);
		this.setNumberOfObstacles(50);
		this.setNumberOfAgents(15);

	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 1 -seed 2");
		doLoop(ManuallyCodedAgentSimulation.class, manualArgs);
		statistics.printExperimentSummary();
		System.exit(0);
	}

}
