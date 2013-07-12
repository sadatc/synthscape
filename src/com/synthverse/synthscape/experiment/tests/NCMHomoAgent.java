/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.tests;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

import sim.engine.SimState;

@SuppressWarnings("serial")
public class NCMHomoAgent extends Agent {

	boolean carryingResource = false;

	NCMHomoAgent(Simulation sim, long generation, long agentId, int energy,
			int maxEnergy, double visionCapability,
			double extractionCapability, double transportationCapability,
			double communicationCapability, int startX, int startY) {
		super(sim, generation, agentId, energy, maxEnergy, visionCapability,
				extractionCapability, transportationCapability,
				communicationCapability, startX, startY);

	}

	public double doubleValue() {
		return 0;
	}

	// 
	// if it's carrying resources, carry it back to home
	// if it's not carrying resources, go find extracted resources
	// if found, carry it, else go find resources to extract
	// else just randomly move
	// NO COMMUNICATION
	//
	public void stepAction(SimState state) {

		if (!carryingResource) {
			if (this.operationDetectExtractedResource()) {
				this.operationLoadResource();
				carryingResource = true;
			} else if (this.operationDetectResource()) {
				this.operationExtractResource();
			}

			else {
				this.operationRandomMove();
			}
		} else {
			if (this.operationDetectHome()) {
				this.operationUnLoadResource();
				carryingResource = false;
				this.operationRandomMove();
			} else {
				this.operationMoveToClosestCollectionSite();
			}
		}

	}

}
