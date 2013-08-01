/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.test.basic;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;

@SuppressWarnings("serial")
public class TestAgent extends Agent {

    public TestAgent() {
	super();

    }

    public TestAgent(Simulation sim, int generationNumber, int agentId, int maxSteps, int startX,
	    int startY) {
	super();

	// set the basic stuff:
	setSim(sim);
	setAgentId(agentId);
	setMaxSteps(maxSteps);
	setX(startX);
	setY(startY);
	setGeneration(generationNumber);

	// set the species/traits:
	setSpecies(sim.getExperiment().getSpeciesComposition().iterator().next());

	// set the interaction mechanisms:
	setInteractionMechanisms(sim.getExperiment().getInteractionMechanisms());

    }

    public void stepAction(SimState state) {
	this.operationRandomMove();
    }

    @Override
    public double doubleValue() {
	// TODO: value will determine color
	return 0;
    }

    @Override
    public void step(SimState state) {
	// TODO Auto-generated method stub

    }

}
