/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.dissertation.archipelago;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class ArchipelagoAgent extends Agent {

    public ArchipelagoAgent(Simulation simulation, Species species) {
	super(simulation, species);

    }

    public ArchipelagoAgent(Simulation sim, Species species,
	    int generationNumber, int agentId, int maxSteps, int startX,
	    int startY) {
	super(sim, species, generationNumber, agentId, maxSteps, startX, startY);

    }

    public void stepAction(SimState state) {
	Simulation theSim = (Simulation) state;

    }

    @Override
    public double doubleValue() {
	double result = 0.0; // normal agent
	if (this.isCarryingResource) {
	    result = 1.0;
	}
	return result;

    }

}
