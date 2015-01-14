package com.synthverse.synthscape.evolutionarymodel.alife;

import java.util.logging.Logger;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedAgent;
import com.synthverse.synthscape.evolutionarymodel.embodied.EmbodiedEvolutionSimulation;
import com.synthverse.synthscape.evolutionarymodel.islands.IslanderAgent;
import com.synthverse.util.LogUtils;
import com.synthverse.util.StringUtils;

@SuppressWarnings("serial")
public class ALifeEvolutionSimulation extends EmbodiedEvolutionSimulation {

	private static Logger logger = Logger
			.getLogger(ALifeEvolutionSimulation.class.getName());

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	public ALifeEvolutionSimulation(long seed) throws Exception {
		super(seed);
		matingEnabled = true;

	}

	public static void main(String[] arg) {
		String[] manualArgs = StringUtils.parseArguments("-repeat "
				+ settings.REPEAT + " -seed " + settings.SEED);
		doLoop(ALifeEvolutionSimulation.class, manualArgs);
		logger.info("Diagnosis: total # of agents created: "
				+ Agent.get_optimazationTotalAgentsCounters());
		logger.info("Diagnosis: total # of islander agents created: "
				+ IslanderAgent.get_optimizationIslanderAgentCounter());
		logger.info("Diagnosis: total # of embodied agents created: "
				+ EmbodiedAgent.get_optimizationEmbodiedAgentCounter());
		System.exit(0);
	}

}
