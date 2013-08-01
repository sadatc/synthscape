/**
 * Test Agent
 */
package com.synthverse.synthscape.experiment.archipleago;

import java.util.HashSet;
import java.util.Set;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Trait;

@SuppressWarnings("serial")
public class IslanderAgent extends Agent {

	public IslanderAgent() {
		super();

	}

	public IslanderAgent(Simulation sim, int generationNumber, int agentId,
			int maxSteps, int startX, int startY) {
		super();

		// set the basic stuff:
		setSim(sim);
		setAgentId(agentId);
		setMaxSteps(maxSteps);
		setX(startX);
		setY(startY);
		setGeneration(generationNumber);

		// set the species/traits:
		Species species = new Species();
		species.traits.add(Trait.DETECTION);
		species.traits.add(Trait.EXTRACTION);
		species.traits.add(Trait.HOMING);
		species.traits.add(Trait.PROCESSING);
		species.traits.add(Trait.TRANSPORTATION);
		species.traits.add(Trait.FLOCKING);
		setSpecies(species);

		// set the interaction mechanisms:
		Set<InteractionMechanism> interactionMechanisms = new HashSet<InteractionMechanism>();
		interactionMechanisms.add(InteractionMechanism.BROADCAST);
		interactionMechanisms.add(InteractionMechanism.TRAIL);
		interactionMechanisms.add(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
		interactionMechanisms.add(InteractionMechanism.UNICAST_CLOSEST_AGENT);
		setInteractionMechanisms(interactionMechanisms);

	}

	public void stepAction(SimState state) {
		// the main idea is as follows
		// the agent is in a particular state
		// given it's state, it can only execute
		// an operation and move to a newer state
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
