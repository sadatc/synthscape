/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.test.basic;

import java.util.HashSet;
import java.util.Set;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Trait;

@SuppressWarnings("serial")
public class TestAgent extends Agent {

	boolean carryingResource = false;

	TestAgent(Simulation sim, long agentId, int maxSteps, int startX, int startY) {
		super();
		
		// set the basic stuff:
		setSim(sim);
		setAgentId(agentId);
		setMaxSteps(maxSteps);
		setX(startX);
		setY(startY);
		
		// set the traits:		
		Set<Trait> traits = new HashSet<Trait>();
		traits.add(Trait.DETECTION);
		traits.add(Trait.EXTRACTION);
		traits.add(Trait.HOMING);
		traits.add(Trait.PROCESSING);
		traits.add(Trait.TRANSPORTATION);
		traits.add(Trait.FLOCKING);
		setTraits(traits);
		
		// set the interaction mechanisms:
		Set<InteractionMechanism> interactionMechanisms = new HashSet<InteractionMechanism>();
		interactionMechanisms.add(InteractionMechanism.BROADCAST);
		interactionMechanisms.add(InteractionMechanism.TRAIL);
		interactionMechanisms.add(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
		interactionMechanisms.add(InteractionMechanism.UNICAST_CLOSEST_AGENT);		
		setInteractionMechanisms(interactionMechanisms);
		

	}

	public void stepAction(SimState state) {
		this.operationRandomMove();
	}

	@Override
	public double doubleValue() {
		// TODO: value will determine color
		return 0;
	}

}
