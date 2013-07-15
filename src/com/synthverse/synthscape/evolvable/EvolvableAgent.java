package com.synthverse.synthscape.evolvable;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

public abstract class EvolvableAgent extends Agent {

	/*
	protected EvolvableAgent(Simulation sim, long generation, long agentId,
			int energy, int maxEnergy, double visionCapability,
			double extractionCapability, double transportationCapability,
			double communicationCapability, int startX, int startY) {
		super(sim, generation, agentId, energy, maxEnergy, visionCapability,
				extractionCapability, transportationCapability,
				communicationCapability, startX, startY);
	}
	*/
	public double getFitness() {
		return stats.numberOfCellsDiscovered;
	}

}
