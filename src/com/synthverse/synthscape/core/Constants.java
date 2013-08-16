package com.synthverse.synthscape.core;

public interface Constants {

    public static final int WORLD_WIDTH = 16;

    public static final int WORLD_HEIGHT = 16;

    public static final int PRIMARY_COLLECTION_SITE_X = (int) (WORLD_WIDTH * 0.90);

    public static final int PRIMARY_COLLECTION_SITE_Y = (int) (WORLD_HEIGHT * 0.90);

    public static final int PRESENT = 1;

    public static final int ABSENT = 0;

    public static final String NA = "-1";

    public static final int TRAIL_LEVEL_MAX = 100;

    public static final int SIGNAL_LEVEL_MAX = 100;

    public static final int TRAIL_LEVEL_MIN = 0;

    public static final boolean TOROIDAL_FLAG = true;

    public static final int AGENTS_PER_SPECIES = 10;

    public static final double OBSTACLE_DENSITY = 0.05;

    public static final double RESOURCE_DENSITY = 0.05;

    public static final int NUMBER_OF_COLLECTION_SITES = 5;

    public static final int MAX_STEPS_PER_AGENT = 500;

    public static final int MAX_STEPS_PER_SIM = MAX_STEPS_PER_AGENT;

    public static final int SIMS_PER_EXPERIMENT = 200;

    public static final double DEFAULT_TRAIL_EVAPORATION_CONSTANT = 0.85;

    public static final int SEED_GENERATION_NUMBER = 0;

    public static final long SIMULATION_RNG_SEED = 2;

    public static final int INVALID = -1;

    public static final double GRID_ICON_SCALE_FACTOR = 0.70;

    public static final String GRID_ICON_AGENT = "images/agent.png";

    public static final String GRID_ICON_LOADED_AGENT = "images/loaded_agent.png";

    public static final String GRID_ICON_COLLECTION_SITE = "images/collection_site.png";

    public static final String GRID_ICON_RAW_RESOURCE = "images/raw_resource.png";

    public static final String GRID_ICON_EXTRACTED_RESOURCE = "images/extracted_resource.png";

    public static final String GRID_ICON_PROCESSED_RESOURCE = "images/processed_resource.png";

    public static final int REPORT_WRITER_BUFFER_SIZE = 8192;

    public static final String EVENT_LOG_FILE = "/tmp/event_log.csv";

    public static final boolean DEFAULT_FLUSH_ALWAYS_FLAG = true;

    public static final boolean INCLUDE_EXPERIMENT_META_DATA = true;
    
    public static final int AGENT_FACTORY_CACHE_SIZE = 5000;

}
