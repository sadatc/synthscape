package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.List;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.EventStats;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedAgent;
import com.synthverse.util.LogUtils;

/**
 * A population island maintains eventStats gene pool that produces agents of
 * eventStats specific species
 * 
 * @author sadat
 * 
 */
public class PopulationIslandEvolver extends Evolver implements Constants {

	private EmbodiedAgent ownerAgent = null;

	public int generation;

	protected static Logger logger = Logger
			.getLogger(PopulationIslandEvolver.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	protected int clonesPerSpecies;
	protected int totalPopulation;
	protected int genePoolSize;
	protected int genePoolIndex;
	protected Species species;

	public EvolutionEngine evolutionEngine;
	protected int requestCounter = 1;
	protected int cloneCounter = 1;
	public List<Agent> activeBuffer;

	public double computedAlphaDistance;

	public PopulationIslandEvolver(Simulation simulation,
			AgentFactory agentFactory, Species species, int clonesPerSpecies)
			throws Exception {
		this(null, simulation, agentFactory, species, clonesPerSpecies);
	}

	public PopulationIslandEvolver(EmbodiedAgent ownerAgent,
			Simulation simulation, AgentFactory agentFactory, Species species)
			throws Exception {
		this(ownerAgent, simulation, agentFactory, species, 1);
	}

	public PopulationIslandEvolver(Simulation simulation,
			AgentFactory agentFactory, Species species) throws Exception {
		this(null, simulation, agentFactory, species, 1);
	}

	public PopulationIslandEvolver(EmbodiedAgent ownerAgent,
			Simulation simulation, AgentFactory agentFactory, Species species,
			int clonesPerSpecies) throws Exception {
		super(simulation, agentFactory);
		if (clonesPerSpecies < 1) {
			throw new Exception("clonesPerSpecies can't be <1");
		}

		setOwnerAgent(ownerAgent);

		this.generation = 0;
		this.species = species;
		evolutionEngine = new EvolutionEngine(ownerAgent, agentFactory, species);
		this.clonesPerSpecies = clonesPerSpecies;
		genePoolSize = evolutionEngine.getGenePoolSize();
		totalPopulation = clonesPerSpecies * genePoolSize;
		activeBuffer = evolutionEngine.getActiveBuffer();
		this.requestCounter = 1;
		this.genePoolIndex = 0;
	}

	@Override
	public Agent getAgent(Species species, int x, int y) {
		Agent returnAgent = null;

		if (requestCounter > totalPopulation) {
			requestCounter = 1;
			genePoolIndex = 0;
			cloneCounter = 1;
		}

		if (cloneCounter > clonesPerSpecies) {
			cloneCounter = 1;
			genePoolIndex++;
		}

		// get next agent from the gene-pool and clone it
		activeBuffer = evolutionEngine.getActiveBuffer();
		Agent archetype = activeBuffer.get(genePoolIndex);
		returnAgent = agentFactory.getNewFactoryAgent(species);
		returnAgent.cloneAndLinkGeneArcheType(archetype);
		returnAgent.getVirtualMachine().resetAll();
		returnAgent.reset();
		returnAgent.setGeneration(generation);
		returnAgent.setMaxSteps(simulation.getMaxStepsPerAgent());
		returnAgent.setX(x);
		returnAgent.setY(y);
		requestCounter++;
		cloneCounter++;
		return returnAgent;
	}

	@Override
	public void init() {

	}

	@Override
	public void provideFeedback(List<Agent> agents, EventStats simStats) {
		// do nothing, we don't need it.

	}

	@Override
	public int evolve() {
		evolutionEngine.generateNextGeneration(simulation.random);

		activeBuffer = evolutionEngine.getActiveBuffer();
		generation++;
		return generation;
	}

	@Override
	public int getGeneration() {

		return generation;
	}

	public EmbodiedAgent getOwnerAgent() {
		return ownerAgent;
	}

	public void setOwnerAgent(EmbodiedAgent ownerAgent) {
		this.ownerAgent = ownerAgent;
	}

}
