package com.synthverse.synthscape.experiment.test.basic;

import java.util.HashSet;
import java.util.Set;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class TestSim extends Simulation {

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(TestSim.class, manualArgs);

	System.exit(0);
    }

    public TestSim(long seed) throws Exception {
	super(seed);
    }

    @Override
    public Evolver configEvolver() {
	return new TestAgentEvolver(this);
    }

    @Override
    public AgentFactory configAgentFactory() {
	return new TestAgentFactory(this);
    }

    @Override
    public int configGridWidth() {
	return WORLD_WIDTH;
    }

    @Override
    public int configGridHeight() {
	return WORLD_HEIGHT;
    }

    @Override
    public double configObstacleDensity() {
	return OBSTACLE_DENSITY;
    }

    @Override
    public double configResourceDensity() {
	return RESOURCE_DENSITY;
    }

    @Override
    public Set<Species> configSpeciesComposition() {
	Set<Species> speciesSet = new HashSet<Species>();
	speciesSet.add(Species.SUPER);
	return speciesSet;
    }

    @Override
    public Set<InteractionMechanism> configInteractionMechanisms() {
	Set<InteractionMechanism> mechanisms = new HashSet<InteractionMechanism>();
	mechanisms.add(InteractionMechanism.TRAIL);
	mechanisms.add(InteractionMechanism.BROADCAST);
	mechanisms.add(InteractionMechanism.UNICAST_CLOSEST_AGENT);
	mechanisms.add(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
	return mechanisms;
    }

    @Override
    public ProblemComplexity configProblemComplexity() {
	return ProblemComplexity.FOUR_SEQUENTIAL_TASKS;
    }

    @Override
    public int configClonesPerSpecies() {
	return CLONES_PER_SPECIES;
    }

    @Override
    public int configNumberOfCollectionSites() {
	return NUMBER_OF_COLLECTION_SITES;
    }

    @Override
    public int configMaxStepsPerAgent() {
	return MAX_STEPS_PER_AGENT;
    }

    @Override
    public boolean configIsReportEvents() {
	return REPORT_EVENTS;
    }
    
    @Override
    public boolean configIsReportPerformance() {
	return REPORT_PERFORMANCE;
    }

    @Override
    public String configExperimentName() {
	return "BasicTest";
    }

    @Override
    public int configSimulationsPerExperiment() {
	return SIMS_PER_EXPERIMENT;
    }

    @Override
    public int configStepsPerSimulation() {
	return MAX_STEPS_PER_AGENT;
    }

    @Override
    public String configEventFileName() {
	return EVENT_LOG_FILE;
    }
    
    @Override
    public int configGenePoolSize() {
	return EE_DEF_GENE_POOL_SIZE;
    }


}
