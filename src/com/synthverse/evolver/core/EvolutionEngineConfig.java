package com.synthverse.evolver.core;

/**
 * Defines the configuration parameters for evolutionary algorithms
 * 
 * @author sadat
 */
public interface EvolutionEngineConfig {

    public static final double EE_DEF_PERCENT_TOP = 0.3;

    public static final double EE_DEF_PERCENT_TOP_X_TOP = 0.25;

    public static final double EE_DEF_PERCENT_TOP_MUTANT = 0.20;

    public static final double EE_DEF_PERCENT_TOP_X_BOTTOM = 0.05;

    public static final double EE_DEF_PERCENT_BOTTOM = 0.05;

    public static final double EE_DEF_PERCENT_BOTTOM_MUTANT = 0.05;

    public static final double EE_DEF_PERCENT_BOTTOM_X_BOTTOM = 0.05;

    public static final double EE_DEF_PERCENT_RANDOM = 0.05;

    public static final int EE_DEF_MAX_ENTITY_SIZE = 32;

    public static final int EE_DEF_GENE_POOL_SIZE = 100;

    public static final double EE_DEF_MAX_MUTATION_RATE = 0.05;

    public static final String EE_DEF_EVOLUTION_PROGRESS_LOG = "/tmp/progress.log";

}
