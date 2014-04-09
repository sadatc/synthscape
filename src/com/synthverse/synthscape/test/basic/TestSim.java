package com.synthverse.synthscape.test.basic;

import java.util.HashSet;
import java.util.Set;

import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.util.StringUtils;

@SuppressWarnings("serial")
public class TestSim extends Simulation {

    public static void main(String[] arg) {
	String[] manualArgs = StringUtils.parseArguments("-repeat 1 -seed 2");
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
	return settings.WORLD_WIDTH;
    }

    @Override
    public int configGridHeight() {
	return settings.WORLD_HEIGHT;
    }

    @Override
    public double configObstacleDensity() {
	return settings.OBSTACLE_DENSITY;
    }

    @Override
    public double configResourceDensity() {
	return settings.RESOURCE_DENSITY;
    }

    @Override
    public Set<Species> configSpeciesComposition() {
	Set<Species> speciesSet = new HashSet<Species>();
	speciesSet.add(Species.HETERO);
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
	return settings.CLONES_PER_SPECIES;
    }

    @Override
    public int configNumberOfCollectionSites() {
	return settings.NUMBER_OF_COLLECTION_SITES;
    }

    @Override
    public int configMaxStepsPerAgent() {
	return settings.MAX_STEPS_PER_AGENT;
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
	return settings.SIMS_PER_EXPERIMENT;
    }

    @Override
    public int configStepsPerSimulation() {
	return settings.MAX_STEPS_PER_AGENT;
    }

    @Override
    public String configEventFileName() {
	return settings.EVENT_DATA_FILE;
    }

    @Override
    public int configGenePoolSize() {
	return settings.GENE_POOL_SIZE;
    }

}
