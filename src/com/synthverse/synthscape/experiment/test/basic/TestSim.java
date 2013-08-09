package com.synthverse.synthscape.experiment.test.basic;

import java.io.IOException;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Experiment;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class TestSim extends Simulation {

    public TestSim(long seed) throws IOException {
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
	myExperiment.addInteractionMechanism(InteractionMechanism.TRAIL);
	myExperiment.addInteractionMechanism(InteractionMechanism.BROADCAST);	
	myExperiment.addInteractionMechanism(InteractionMechanism.UNICAST_CLOSEST_AGENT);
	myExperiment.addInteractionMechanism(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
	

	// species compositions
	myExperiment.setNumberOfAgentsPerSpecies(AGENTS_PER_SPECIES);

	myExperiment.addSpecies(Species.SUPER);

	// steps and simulations...
	myExperiment.setStepsPerSimulation(MAX_STEPS_PER_SIM);
	myExperiment.setSimulationsPerExperiment(SIMS_PER_EXPERIMENT);

	return myExperiment;
    }

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(TestSim.class, manualArgs);

	System.exit(0);
    }

}
