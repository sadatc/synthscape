package com.synthverse.synthscape.evolutionarymodel.embodied;

import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;
import com.synthverse.synthscape.evolutionarymodel.islands.PopulationIslandEvolver;
import com.synthverse.util.LogUtils;

/**
 * EmbodiedAgentEvolver is responsible for evolving a single agent that within
 * it contains a PopulationIslandEvolver
 * 
 * * An EmbodiedAgent contains within it a Population Island of a particular
 * species. At any point in time, it is running one the individuals of its own
 * island. The only distinction is, during run-time, each embodied agent is
 * running its own instance of an evolution engine, whereas, in island model,
 * there is a centralized evolution engine that coordinates everything
 * 
 * 
 * @author sadat
 * 
 */
public class EmbodiedAgentEvolver extends PopulationIslandEvolver implements
		Constants {

	private static Logger logger = Logger.getLogger(EmbodiedAgentEvolver.class
			.getName());
	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public EmbodiedAgentEvolver(EmbodiedAgent ownerAgent,
			Simulation simulation, AgentFactory agentFactory, Species species)
			throws Exception {
		super(ownerAgent, simulation, agentFactory, species);
	}

	public final void reclaimEmbodiedAgent(final Agent agent) {
		agentFactory.reclaimAgent(agent);
	}

	@Override
	public int evolve() {
		evolutionEngine.generateNextGeneration(simulation.random);
		activeBuffer = evolutionEngine.getActiveBuffer();
		generation++;
		return generation;
	}

	@Override
	public String toString() {
		return "EmbodiedAgentEvolver [generation=" + generation + ", clonesPerSpecies=" + clonesPerSpecies
				+ ", totalPopulation=" + totalPopulation + ", genePoolSize=" + genePoolSize + ", genePoolIndex="
				+ genePoolIndex + ", species=" + species + ", evolutionEngine=" + evolutionEngine + ", requestCounter="
				+ requestCounter + ", cloneCounter=" + cloneCounter + ", activeBuffer=" + activeBuffer
				+ ", computedAlphaDistance=" + computedAlphaDistance + ", simulation=" + simulation + ", agentFactory="
				+ agentFactory + "]";
	}
	
	

}
