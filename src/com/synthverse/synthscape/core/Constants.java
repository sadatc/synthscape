package com.synthverse.synthscape.core;

public interface Constants {

	// MORE frequently changed constants appear here...

	public static final int NUMBER_OF_STEPS_FOR_BROADCASTED_SIGNAL = 2;

	// EVOLUTION ENGINE RELATED CONSTANTS

	public static final double EE_DEF_PERCENT_TOP = 0.3;

	public static final double EE_DEF_PERCENT_TOP_X_TOP = 0.25;

	public static final double EE_DEF_PERCENT_TOP_MUTANT = 0.20;

	public static final double EE_DEF_PERCENT_TOP_X_BOTTOM = 0.05;

	public static final double EE_DEF_PERCENT_BOTTOM = 0.05;

	public static final double EE_DEF_PERCENT_BOTTOM_MUTANT = 0.05;

	public static final double EE_DEF_PERCENT_BOTTOM_X_BOTTOM = 0.05;

	public static final double EE_DEF_PERCENT_RANDOM = 0.05;

	public static final int EE_DEF_MAX_ENTITY_SIZE = 64;

	public static final double EE_DEF_MAX_MUTATION_RATE = 0.05;

	public static final String EE_DEF_EVOLUTION_PROGRESS_LOG = "/tmp/progress.log";

	// SIMULATION RELATED CONSTANTS

	public static final int PRESENT = 1;

	public static final int ABSENT = 0;

	public static final int NA = -1;

	public static final int TRAIL_LEVEL_MAX = 100;

	public static final int REWARD_LEVEL_MAX = 3;

	public static final int SIGNAL_LEVEL_MAX = 100;

	public static final int TRAIL_LEVEL_MIN = 1;

	public static final boolean TOROIDAL_FLAG = true;

	public static final double DEFAULT_TRAIL_EVAPORATION_CONSTANT = 0.913;
	// 0.60 => trail stays for 10 steps, 0.79=20 steps, 0.795=> 21 steps
	// 0.913=>51

	public static final double DEFAULT_REWARD_EVAPORATION_CONSTANT = 0.50;

	public static final long UI_SIMULATION_RNG_SEED = 2;

	public static final int INVALID = -1;

	public static final double GRID_ICON_SCALE_FACTOR = 0.70;

	public static final String GRID_ICON_AGENT = "images/agent.png";

	public static final String GRID_ICON_LOADED_AGENT = "images/loaded_agent.png";

	public static final String GRID_ICON_COLLECTION_SITE = "images/collection_site.png";

	public static final String GRID_ICON_RAW_RESOURCE = "images/raw_resource.png";

	public static final String GRID_ICON_EXTRACTED_RESOURCE = "images/extracted_resource.png";

	public static final String GRID_ICON_PROCESSED_RESOURCE = "images/processed_resource.png";

	public static final String GRID_ICON_COLLECTED_RESOURCE = "images/collected_resource.png";

	public static final int FILE_IO_BUFFER_SIZE = 8192;

	public static final boolean REPORT_EVENTS = false;

	public static final boolean REPORT_PERFORMANCE = true;

	public static final boolean DEFAULT_FLUSH_ALWAYS_FLAG = true;

	public static final boolean INCLUDE_EXPERIMENT_META_DATA = true;

	public static final int UNASSIGNED_AGENT_ID = -99;

	public static final int DEFAULT_BITSET_SIZE = 1000000;

	public static final int CSV_DOUBLE_FORMAT = 6;
	
	public static final int CSV_INT_DIGITS = 15;

	public static final String EMPTY_STRING = "";

	public static final int MAX_TRIES_TO_FIND_EMPTY_MOVE_LOCATION = 1000;

	public static final long SHORT_PAUSE = 1;

	public static final long LONG_PAUSE = 5;

	public static final String EXPERIMENT_MAP_FILE = "experiment_map.txt";

}
