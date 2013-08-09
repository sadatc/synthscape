package com.synthverse.synthscape.experiment.dissertation.archipelago;

import java.io.IOException;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Experiment;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class ArchipelagoSimulation extends Simulation {

    public ArchipelagoSimulation(long seed) throws IOException {
	super(seed);

    }

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(ArchipelagoSimulation.class, manualArgs);

	System.exit(0);
    }

    @Override
    public Experiment getExperiment() {

	// name and complexity
	Experiment exp = new Experiment("POPULATION_ISLAND");
	exp.setProblemComplexity(ProblemComplexity.FOUR_SEQUENTIAL_TASKS);
	exp.setEvolver(null);
	
	// demographics
	exp.setGridWidth(WORLD_WIDTH);
	exp.setGridHeight(WORLD_HEIGHT);
	exp.setNumberOfCollectionSites(NUMBER_OF_COLLECTION_SITES);
	exp.setObstacleDensity(OBSTACLE_DENSITY);
	exp.setResourceDensity(RESOURCE_DENSITY);

	// interactions
	exp.addInteractionMechanism(InteractionMechanism.TRAIL);
	//exp.addInteractionMechanism(InteractionMechanism.BROADCAST);
	//exp.addInteractionMechanism(InteractionMechanism.UNICAST_CLOSEST_AGENT);
	//exp.addInteractionMechanism(InteractionMechanism.UNICAST_CLIQUE_MEMBER);

	// species compositions
	exp.setNumberOfAgentsPerSpecies(AGENTS_PER_SPECIES);

	exp.addSpecies(Species.DETECTOR);
	exp.addSpecies(Species.EXTRACTOR);
	exp.addSpecies(Species.PROCESSOR);
	exp.addSpecies(Species.TRANSPORTER);
	
	

	// generations and runs
	exp.setStepsPerSimulation(MAX_STEPS_PER_SIM);
	exp.setSimulationsPerExperiment(SIMS_PER_EXPERIMENT);

	exp.setMaxStepsPerAgent(MAX_STEPS_PER_AGENT);

	exp.setRecordExperiment(true);
	exp.setEventFileName(EVENT_LOG_FILE);

	return exp;
    }

    @Override
    public AgentFactory getAgentFactory() {
	return ArchipelagoAgentFactory.getInstance();
    }

}
