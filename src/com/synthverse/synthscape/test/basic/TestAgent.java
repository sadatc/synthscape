/**
 * Test Agent
 */
package com.synthverse.synthscape.test.basic;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class TestAgent extends Agent {

    public TestAgent(Simulation simulation, Species species, int generation,
	    int maxSteps, int x, int y) {
	super(simulation, species, generation, maxSteps, x, y);
    }

    public TestAgent(Simulation simulation, Species species) {
	super(simulation, species);
    }

    @Override
    public void stepAction(SimState state) {
	this.operationRandomMove();
    }

    @Override
    public double doubleValue() {
	// TODO: value will determine color
	return 0;
    }

}
