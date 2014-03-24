package com.synthverse.synthscape.experiment.test.manuallycoded;

import java.util.HashSet;
import java.util.Set;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.util.StringUtils;

@SuppressWarnings("serial")
public class ManuallyCodedAgentSimulation extends Simulation {
    Settings settings = Settings.getInstance();
    

    public ManuallyCodedAgentSimulation(long seed) throws Exception {
	super(seed);
    }

    public static void main(String[] arg) {
	String[] manualArgs = StringUtils.parseArguments("-repeat 1 -seed 2");
	doLoop(ManuallyCodedAgentSimulation.class, manualArgs);

	System.exit(0);
    }

    @Override
    public Evolver configEvolver() {
	return new ManuallyCodedEvolver(this);
    }

    @Override
    public AgentFactory configAgentFactory() {
	return new ManuallyCodedAgentFactory(this);
    }

    @Override
    public int configGridWidth() {
	return 64;
    }

    @Override
    public int configGridHeight() {
	return 64;
    }

    @Override
    public double configObstacleDensity() {
	//return OBSTACLE_DENSITY;
	return 0.001;
    }

    @Override
    public double configResourceDensity() {
	//return RESOURCE_DENSITY;
	return 0.001;
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
	//return CLONES_PER_SPECIES;
	return 500;
    }

    @Override
    public int configNumberOfCollectionSites() {
	return settings.NUMBER_OF_COLLECTION_SITES;
    }

    @Override
    public int configMaxStepsPerAgent() {
	//return MAX_STEPS_PER_AGENT;
	return 10000;
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
	return "POPULATION_ISLAND_MANUAL";
    }

    @Override
    public int configSimulationsPerExperiment() {
	return settings.SIMS_PER_EXPERIMENT;
    }

    @Override
    public int configStepsPerSimulation() {
	//return MAX_STEPS_PER_AGENT;
	return 10000;
    }

    @Override
    public String configEventFileName() {
	return settings.EVENT_DATA_FILE;
    }
    
    @Override
    public int configGenePoolSize() {
	return settings.EE_DEF_GENE_POOL_SIZE;
    }


}
