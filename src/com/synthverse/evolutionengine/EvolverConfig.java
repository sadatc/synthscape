package com.synthverse.evolutionengine;

/**
 * Defines the configuration parameters for evolutionary algorithms
 * 
 * @author sadat
 */
public interface EvolverConfig {

	public static final double DEFAULT_PERCENT_TOP = 0.3;

	public static final double DEFAULT_PERCENT_TOP_X_TOP = 0.25;

	public static final double DEFAULT_PERCENT_TOP_MUTANT = 0.20;

	public static final double DEFAULT_PERCENT_TOP_X_BOTTOM = 0.05;

	public static final double DEFAULT_PERCENT_BOTTOM = 0.05;

	public static final double DEFAULT_PERCENT_BOTTOM_MUTANT = 0.05;

	public static final double DEFAULT_PERCENT_BOTTOM_X_BOTTOM = 0.05;

	public static final double DEFAULT_PERCENT_RANDOM = 0.05;

	public static final int DEFAULT_MAX_ENTITY_SIZE = 32;

	public static final int DEFAULT_MAX_POPULATION_SIZE = 20;

	public static final int DEFAULT_MAX_GENERATIONS = 10;

	public static final double DEFAULT_MAX_MUTATION_RATE = 0.05;

	public static final String DEFAULT_EVOLUTION_PROGRESS_LOG = "/tmp/progress.log";

}
