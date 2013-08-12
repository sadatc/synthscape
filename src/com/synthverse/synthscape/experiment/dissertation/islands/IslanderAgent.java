/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.dissertation.islands;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class IslanderAgent extends Agent {

    public IslanderAgent(Simulation simulation, Species species) {
	super(simulation, species);

    }

    public IslanderAgent(Simulation sim, Species species,
	    int generationNumber, int maxSteps, int startX, int startY) {
	super(sim, species, generationNumber, maxSteps, startX, startY);

    }

    public void stepAction(SimState state) {
	Simulation theSim = (Simulation) state;
	this.operationRandomMove();

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
