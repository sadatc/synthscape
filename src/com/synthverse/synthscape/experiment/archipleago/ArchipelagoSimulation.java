package com.synthverse.synthscape.experiment.archipleago;

import com.synthverse.evolutionengine.model.archipelago.ArchipelagoManager;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class ArchipelagoSimulation extends Simulation {

	ArchipelagoManager archipelagoManager = new ArchipelagoManager();

	public ArchipelagoSimulation(String experimentName,
			AgentFactory agentFactory, ProblemComplexity problemComplexity,
			long seed) {
		super(experimentName, agentFactory, problemComplexity, seed);
		this.setNumberOfCollectionSites(5);
		this.setNumberOfObstacles(50);
		this.setNumberOfAgents(15);

	}

	public static void main(String[] arg) {
		String[] manualArgs = parseArguments("-repeat 1 -seed 2");
		doLoop(ArchipelagoSimulation.class, manualArgs);

		System.exit(0);
	}

}
