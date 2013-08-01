package com.synthverse.synthscape.core;

public enum Event {
    DETECTED_RAW_RESOURCE(0),
    DETECTED_PROCESSED_RESOURCE(1),
    DETECTED_EXTRACTED_RESOURCE(2),
    EXTRACTED_RESOURCE(3),
    PROCESSED_RESOURCE(4),
    LOADED_RESOURCE(5),
    UNLOADED_RESOURCE(6),
    COLLECTED_RESOURCE(7),
    SENT_RAW_RESOURCE_TRAIL(8),
    RECEIVED_RAW_RESOURCE_TRAIL(9),
    SENT_EXTRACTED_RESOURCE_TRAIL(10),
    RECEIVED_EXTRACTED_RESOURCE_TRAIL(11),
    SENT_PROCESSED_RESOURCE_TRAIL(12),
    RECEIVED_PROCESSED_RESOURCE_TRAIL(13);
    
    private int value;
    
    Event(int value) {
    	this.value = value;
    }
    
    public int getValue() {
    	return this.value;
    }
    
}
