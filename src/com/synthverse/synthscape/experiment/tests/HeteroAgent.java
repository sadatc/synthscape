/**
 * Agents can have specific capabilities: transporter, extractor
 * 
 */
package com.synthverse.synthscape.experiment.tests;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

import sim.engine.SimState;

@SuppressWarnings("serial")
public class HeteroAgent extends Agent {

	boolean carryingResource = false;

	HeteroAgent(Simulation sim, long generation, long agentId, int energy,
			int maxEnergy, double visionCapability,
			double extractionCapability, double transportationCapability,
			double communicationCapability, int startX, int startY) {
		super(sim, generation, agentId, energy, maxEnergy, visionCapability,
				extractionCapability, transportationCapability,
				communicationCapability, startX, startY);

	}

	public double doubleValue() {

		if (transportationCapability > 0.0) {
			if (!carryingResource) {
				return 0.0;
			} else {
				return 1.0;
			}
		} else {
			return 2.0;
		}

	}

	public void nonCommExtractor() {
		if (this.operationDetectResource()) {
			this.operationExtractResource();
		}

		else {
			this.operationRandomMove();
		}

	}

	public void nonCommTransporter() {
		if (!carryingResource) {
			if (this.operationDetectExtractedResource()) {
				this.operationLoadResource();
				carryingResource = true;
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

	public void stepAction(SimState state) { 

		if (this.transportationCapability == 1.0) {
			nonCommTransporter();
		}

		if (this.extractionCapability == 1.0) {
			nonCommExtractor();
		}

	}

}
