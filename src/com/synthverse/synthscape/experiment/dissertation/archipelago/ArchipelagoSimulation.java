package com.synthverse.synthscape.experiment.dissertation.archipelago;

import java.io.IOException;
import java.util.Set;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class ArchipelagoSimulation extends Simulation {

    public ArchipelagoSimulation(long seed) throws Exception {
	super(seed);

    }

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(ArchipelagoSimulation.class, manualArgs);

	System.exit(0);
    }

    @Override
    public int configGridWidth() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int configGridHeight() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public double configObstacleDensity() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public double configResourceDensity() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public Set<Species> configSpeciesComposition() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Set<InteractionMechanism> configInteractionMechanisms() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ProblemComplexity configProblemComplexity() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int configNumberOfAgentsPerSpecies() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int configNumberOfCollectionSites() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int configMaxStepsPerAgent() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean configIsRecordExperiment() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public String configExperimentName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int configSimulationsPerExperiment() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int configStepsPerSimulation() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public String configEventFileName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Evolver configEvolver() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AgentFactory configAgentFactory() {
	// TODO Auto-generated method stub
	return null;
    }

}
