package com.synthverse.synthscape.core;

public interface Constants {

	public static final int WORLD_WIDTH = 20;

	public static final int WORLD_HEIGHT = 20;

	public static final int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);

	public static final int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);

	public static final int PRESENT = 1;

	public static final int ABSENT = 0;

	public static final int TRAIL_LEVEL_MAX = 100;

	public static final int SIGNAL_LEVEL_MAX = 100;

	public static final int TRAIL_LEVEL_MIN = 0;

	public static final boolean DEFAULT_IS_TOROIDAL = true;

	public static final int DEFAULT_NUMBER_OF_AGENTS = 50;

	public static final int DEFAULT_NUMBER_OF_OBSTACLES = 50;

	public static final int DEFAULT_NUMBER_OF_RESOURCES = 10;

	public static final int DEFAULT_NUMBER_OF_HOMES = 0;

	public static final double DEFAULT_TRAIL_EVAPORATION_CONSTANT = 0.85;

	public static final long SEED_GENERATION_NUMBER = 0;

	public static final long SIMULATION_RNG_SEED = 2;

	public static final int INVALID = -1;

	public static final double GRID_ICON_SCALE_FACTOR = 0.70;

	public static final String GRID_ICON_AGENT = "images/agent.png";

	public static final String GRID_ICON_LOADED_AGENT = "images/loaded_agent.png";

	public static final String GRID_ICON_COLLECTION_SITE = "images/collection_site.png";

	public static final String GRID_ICON_RAW_RESOURCE = "images/raw_resource.png";

	public static final String GRID_ICON_EXTRACTED_RESOURCE = "images/extracted_resource.png";

	public static final String GRID_ICON_PROCESSED_RESOURCE = "images/processed_resource.png";

}
