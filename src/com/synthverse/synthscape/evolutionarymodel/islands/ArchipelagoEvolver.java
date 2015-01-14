package com.synthverse.synthscape.evolutionarymodel.islands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Event;
import com.synthverse.synthscape.core.EventStats;
import com.synthverse.synthscape.core.Evolver;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.util.LogUtils;

/**
 * Here is how it works: 1. It creates an evolver for each species 2. when a
 * request comes in, it keeps track of the generation
 * 
 * 
 * @author sadat
 * 
 */
public class ArchipelagoEvolver extends Evolver implements Constants {
	private static Logger logger = Logger.getLogger(ArchipelagoEvolver.class
			.getName());

	private int generation;

	public LinkedHashMap<Species, PopulationIslandEvolver> speciesIslandMap = new LinkedHashMap<Species, PopulationIslandEvolver>();

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public ArchipelagoEvolver(Simulation simulation, AgentFactory agentFactory) {
		super(simulation, agentFactory);
		this.generation = 0;
	}

	public void initPopulationIslands() throws Exception {
		for (Species species : simulation.getSpeciesComposition()) {
			PopulationIslandEvolver island = new PopulationIslandEvolver(
					simulation, agentFactory, species,
					simulation.getClonesPerSpecies());
			speciesIslandMap.put(species, island);
		}
	}

	@Override
	public Agent getAgent(Species species, int x, int y) {
		PopulationIslandEvolver islandEvolver = speciesIslandMap.get(species);

		Agent result = islandEvolver.getAgent(species, x, y);
		return result;
	}

	@Override
	public void init() {
		try {
			initPopulationIslands();
		} catch (Exception e) {
			System.err.println("Exception initializing islands:"
					+ e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void provideFeedback(List<Agent> agents, EventStats simStats) {
		// FITNESS EVALUATION:
		// In this model, for each generation, the number of simulations run
		// corresponds
		// to the size of the gene pool. During each simulation, an individual
		// gene
		// from the pool is taken, cloned (into a team), and fitness evaluated
		// collectively

		double collectiveFitness = computeFitness(simStats, agents);

		// here all agents are forced to have the same fitness
		// all agents here belong to a single genotypical parent
		for (Agent agent : agents) {
			agent.setFitness(collectiveFitness);
			agent.setProvidedFeedback(true);
			Agent archetypeAgent = agent.getArchetypeReference();
			if (archetypeAgent != null) {
				archetypeAgent.setFitness(collectiveFitness);
			}
		}

	}

	private double computeFitness(EventStats simStats, List<Agent> agents) {
		double result = 0.0;

		for (Event event : simStats.getEvents()) {
			result += getEventWeight(event) * simStats.getValue(event);
		}

		return result;
	}

	@SuppressWarnings("incomplete-switch")
	private double getEventWeight(Event event) {

		switch (event) {
		case COLLECTED_RESOURCE:
			return 1.0;
		}
		return 0;

	}

	@Override
	public int evolve() {
		for (Evolver evolver : speciesIslandMap.values()) {
			evolver.evolve();
		}

		generation++;
		return generation;
	}

	@Override
	public int getGeneration() {
		return generation;
	}
}
