package com.synthverse.synthscape.experiment.test.basic;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Experiment;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Trait;

@SuppressWarnings("serial")
public class TestSim extends Simulation {

    public TestSim(long seed) {
	super(seed);

    }

    @Override
    public AgentFactory getAgentFactory() {
	return TestAgentFactory.getInstance();
    }

    @Override
    public Experiment getExperiment() {

	// name and complexity
	Experiment myExperiment = new Experiment("Basic.Test");
	myExperiment.setProblemComplexity(ProblemComplexity.FOUR_SEQUENTIAL_TASKS);

	// demographics
	myExperiment.setGridWidth(WORLD_WIDTH);
	myExperiment.setGridHeight(WORLD_HEIGHT);
	myExperiment.setNumberOfCollectionSites(NUMBER_OF_COLLECTION_SITES);
	myExperiment.setObstacleDensity(OBSTACLE_DENSITY);
	myExperiment.setResourceDensity(RESOURCE_DENSITY);

	// interactions
	myExperiment.addInteractionMechanism(InteractionMechanism.BROADCAST);
	myExperiment.addInteractionMechanism(InteractionMechanism.TRAIL);
	myExperiment.addInteractionMechanism(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
	myExperiment.addInteractionMechanism(InteractionMechanism.UNICAST_CLOSEST_AGENT);

	// species compositions
	myExperiment.setNumberOfAgentsPerSpecies(AGENTS_PER_SPECIES);
	Species species1 = new Species();
	species1.addTrait(Trait.DETECTION);
	species1.addTrait(Trait.EXTRACTION);
	species1.addTrait(Trait.FLOCKING);
	species1.addTrait(Trait.HOMING);
	species1.addTrait(Trait.PROCESSING);
	species1.addTrait(Trait.TRANSPORTATION);
	myExperiment.addSpecies(species1);
	
	// generations and runs
	myExperiment.setGenerationsPerSimulation(GENERATIONS_PER_SIM);
	myExperiment.setNumberOfSimulations(SIMS_PER_EXPERIMENT);
	
	
	return myExperiment;
    }

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(TestSim.class, manualArgs);

	System.exit(0);
    }

}
