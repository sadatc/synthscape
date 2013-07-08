package com.synthverse.synthscape.core;

public interface Constants {

	public static final int GRID_WIDTH = 50;

	public static final int GRID_HEIGHT = 50;

	public static final int PRIMARY_HOME_X = (int) (GRID_WIDTH * 0.90);

	public static final int PRIMARY_HOME_Y = (int) (GRID_HEIGHT * 0.90);

	public static final int OBSTACLE_TRUE = 1;

	public static final int OBSTACLE_FALSE = 0;

	public static final int RESOURCE_MAX = 1;

	public static final int RESOURCE_EMPTY = 0;

	public static final int HOME_TRUE = 1;

	public static final int HOME_FALSE = 0;

	public static final int TRAIL_LEVEL_MAX = 100;

	public static final int SIGNAL_LEVEL_MAX = 100;

	public static final int TRAIL_LEVEL_MIN = 0;

	public static final boolean ROLE_BASED_ENERGY_ACCOUNTING = true;

	public static final boolean CAPABILITY_BASED_OPERATIONS_ONLY = true;

	public static final double UNSKILLED_ENERGY_DRAIN = 10;

	public static final double GENERIC_ENERGY_DRAIN = 1;

	public static final boolean DEFAULT_IS_TOROIDAL = true;

	public static final int DEFAULT_SIGNAL_LEVEL_A = 0;

	public static final int DEFAULT_SIGNAL_LEVEL_B = 0;

	public static final int DEFAULT_SIGNAL_LEVEL_C = 0;

	public static final int DEFAULT_NUMBER_OF_AGENTS = 50;

	public static final int DEFAULT_NUMBER_OF_OBSTACLES = 1000;

	public static final int DEFAULT_NUMBER_OF_RESOURCES_A = 10;

	public static final int DEFAULT_NUMBER_OF_RESOURCES_B = 10;

	public static final int DEFAULT_NUMBER_OF_RESOURCES_C = 10;

	public static final int DEFAULT_NUMBER_OF_HOMES = 0;

	public static final double DEFAULT_BROADCAST_EVAPORATION_CONSTANT = 0;

	public static final double DEFAULT_TRAIL_A_EVAPORATION_CONSTANT = 0.85;

	public static final double DEFAULT_TRAIL_B_EVAPORATION_CONSTANT = 0.85;

	public static final double DEFAULT_TRAIL_C_EVAPORATION_CONSTANT = 0.85;

	public static final boolean SINGLE_RESOURCE_MODEL = false;

	public static final boolean SINGLE_TRAIL_MODEL = false;

	public static final boolean SINGLE_BROADCAST_MODEL = false;

	public static final long SEED_GENERATION = -1;
	
	public static final long SIMULATION_RNG_SEED = 2;
	
	public static final boolean USE_ENERGY_ACCOUNTING = false;

}
