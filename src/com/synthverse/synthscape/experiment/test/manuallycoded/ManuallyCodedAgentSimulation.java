package com.synthverse.synthscape.experiment.test.manuallycoded;

import java.io.IOException;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Experiment;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.core.Trait;

@SuppressWarnings("serial")
public class ManuallyCodedAgentSimulation extends Simulation {

    public ManuallyCodedAgentSimulation(long seed) throws IOException {
	super(seed);

    }

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(ManuallyCodedAgentSimulation.class, manualArgs);

	System.exit(0);
    }

    @Override
    public Experiment getExperiment() {

	// name and complexity
	Experiment myExperiment = new Experiment("POPULATION_ISLAND_MANUAL");
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
	myExperiment.setStepsPerSimulation(MAX_STEPS_PER_SIM);
	myExperiment.setSimulationsPerExperiment(SIMS_PER_EXPERIMENT);

	myExperiment.setMaxStepsPerAgent(MAX_STEPS_PER_AGENT);

	myExperiment.setRecordExperiment(true);
	myExperiment.setEventFileName(EVENT_LOG_FILE);
	myExperiment.setFlushAlways(false);

	return myExperiment;
    }

    @Override
    public AgentFactory getAgentFactory() {
	return ManuallyCodedAgentFactory.getInstance();
    }

}
