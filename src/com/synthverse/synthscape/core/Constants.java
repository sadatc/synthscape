package com.synthverse.synthscape.core;

public interface Constants {

	public static final int WORLD_WIDTH = 50;

	public static final int WORLD_HEIGHT = 50;

	public static final int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);

	public static final int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);

	public static final int PRESENT = 1;

	public static final int ABSENT = 0;

	public static final int TRAIL_LEVEL_MAX = 100;

	public static final int SIGNAL_LEVEL_MAX = 100;

	public static final int TRAIL_LEVEL_MIN = 0;

	public static final boolean DEFAULT_IS_TOROIDAL = true;

	public static final int DEFAULT_NUMBER_OF_AGENTS = 50;

	public static final int DEFAULT_NUMBER_OF_OBSTACLES = 1000;

	public static final int DEFAULT_NUMBER_OF_RESOURCES = 10;

	public static final int DEFAULT_NUMBER_OF_HOMES = 0;

	public static final double DEFAULT_TRAIL_EVAPORATION_CONSTANT = 0.85;

	public static final long SEED_GENERATION_NUMBER = 0;

	public static final long SIMULATION_RNG_SEED = 2;

	public static final int INVALID = -1;

}
