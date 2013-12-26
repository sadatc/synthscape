package com.synthverse.synthscape.experiment.dissertation.islands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sim.util.Int2D;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.evolver.islands.ArchipelagoEvolver;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.InteractionMechanism;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

import ec.util.MersenneTwisterFast;

@SuppressWarnings("serial")
public class PopulationIslandSimulation extends Simulation {

    public PopulationIslandSimulation(long seed) throws Exception {
	super(seed);

    }

    public static void main(String[] arg) {
	String[] manualArgs = parseArguments("-repeat 1 -seed 2");
	doLoop(PopulationIslandSimulation.class, manualArgs);
	D.p("Diagnosis: total # of agents created: "+Agent.get_optimizationAgentCounter());

	System.exit(0);
    }
    
    
    @Override
    protected void initAgents() {
 	// populate with agents
	

 	MersenneTwisterFast random = this.random;
 	if (!RANDOMIZE_ENVIRONMENT_FOR_EACH_SIM) {
 	    random = controlledRandom;
 	    controlledRandom.setSeed(1);
 	}
 	agents.clear();

 	for (Species species : speciesComposition) {
 	    
 	    List<Integer> signatureTest = new ArrayList<Integer>();
 	    
 	    
 	    for (int i = 0; i < clonesPerSpecies; i++) {

 		int randomX = random.nextInt(gridWidth);
 		int randomY = random.nextInt(gridHeight);

 		while (initCollisionGrid.field[randomX][randomY] == PRESENT) {
 		    randomX = random.nextInt(gridWidth);
 		    randomY = random.nextInt(gridHeight);
 		}
 		initCollisionGrid.field[randomX][randomY] = PRESENT;

 		Agent agent = evolver.getAgent(species, randomX, randomY);

 		agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));
 		agents.add(agent);
 		signatureTest.add(agent.getProgram().getSignature());
 		// add agents to the scheduler

 		if (!agent.isScheduled()) {
 		    schedule.scheduleRepeating(agent);

 		    agent.setScheduled(true);
 		}

 	    }
 	    
 	    int sig = signatureTest.get(0);
 	    for(int i=1;i<signatureTest.size();i++) {
 		if(signatureTest.get(i)!=sig) {
 		    D.p("no match!");
 		}
 	    }
 	    
 	}

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
	//speciesSet.add(Species.DETECTOR);
	//speciesSet.add(Species.EXTRACTOR);
	//speciesSet.add(Species.TRANSPORTER);
	
	speciesSet.add(Species.SUPER);
	return speciesSet;
    }

    @Override
    public Set<InteractionMechanism> configInteractionMechanisms() {
	Set<InteractionMechanism> mechanisms = new HashSet<InteractionMechanism>();
	//mechanisms.add(InteractionMechanism.TRAIL);
	//mechanisms.add(InteractionMechanism.BROADCAST);
	//mechanisms.add(InteractionMechanism.UNICAST_CLOSEST_AGENT);
	//mechanisms.add(InteractionMechanism.UNICAST_CLIQUE_MEMBER);
	return mechanisms;
    }

    @Override
    public ProblemComplexity configProblemComplexity() {
	return PROBLEM_COMPLEXITY;
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
	return "POPULATION_ISLAND_MANUAL";
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
    public Evolver configEvolver() {
	
	return new ArchipelagoEvolver(this);
    }

    @Override
    public AgentFactory configAgentFactory() {
	return new IslanderAgentFactory(this);
    }

    @Override
    public int configGenePoolSize() {
	return EE_DEF_GENE_POOL_SIZE;
    }

}
