/**
 * Agents can have specific capabilities and can leave trail
 */
package com.synthverse.synthscape.experiment.nonevolutionary;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

import sim.engine.SimState;

@SuppressWarnings("serial")
public class HeteroTrailAgent extends Agent {

	boolean carryingResource = false;
	int trailDropCounter = 0;
	int MAX_TRAIL_DROP_COUNTER = 10;

	HeteroTrailAgent(Simulation sim, long generation, long agentId, int energy,
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

	public void trailExtractor() {
		if (this.operationDetectResource()) {
			this.operationExtractResource();
			this.trailDropCounter = MAX_TRAIL_DROP_COUNTER;
			this.operationRandomMove();
		}

		if (this.operationDetectExtractedResource()) {
			this.trailDropCounter = MAX_TRAIL_DROP_COUNTER;
			this.operationRandomMove();
		}

		else {
			this.operationRandomMove();
		}
		dropTrail();

	}

	private void dropTrail() {
		if (this.trailDropCounter > 0) {
			this.operationLeaveTrail();
			this.trailDropCounter--;
		}

	}

	public void trailTransporter() {
		if (!carryingResource) {
			if (this.operationDetectExtractedResource()) {
				this.operationLoadResource();
				carryingResource = true;
			}

			else {
				this.operationFollowTrail();
			}
		} else {
			if (this.operationDetectHome()) {
				this.operationUnLoadResource();
				carryingResource = false;
				this.operationFollowTrail();
			} else {
				this.operationMoveToClosestCollectionSite();
			}
		}

	}

	public void stepAction(SimState state) {

		if (this.transportationCapability == 1.0) {
			trailTransporter();
		}

		if (this.extractionCapability == 1.0) {
			trailExtractor();
		}

	}

}
