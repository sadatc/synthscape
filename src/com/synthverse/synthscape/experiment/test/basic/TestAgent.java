/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.test.basic;

import java.util.Set;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class TestAgent extends Agent {

    public TestAgent(Simulation simulation, int generation, int agentId, int maxSteps, int x, int y) {
	super(simulation, generation, agentId, maxSteps, x, y);
    }

    public TestAgent() {
	super();
    }

    @Override
    protected Species getSpecies() {
	// or pick a specific one...
	return getSim().getExperiment().getSpeciesComposition().iterator().next();
    }

    @Override
    protected Set<InteractionMechanism> getInteractionMechanisms() {
	// or pick a specific one...
	return getSim().getExperiment().getInteractionMechanisms();
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
