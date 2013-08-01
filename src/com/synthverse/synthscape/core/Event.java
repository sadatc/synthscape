package com.synthverse.synthscape.core;

public enum Event {
    DETECTED_RAW_RESOURCE(0),
    DETECTED_PROCESSED_RESOURCE(1),
    DETECTED_EXTRACTED_RESOURCE(2),
    LOADED_RESOURCE(3),
    UNLOADED_RESOURCE(4),
    COLLECTED_RESOURCE(5),
    SENT_RAW_RESOURCE_TRAIL(6),
    RECEIVED_RAW_RESOURCE_TRAIL(7),
    SENT_EXTRACTED_RESOURCE_TRAIL(8),
    RECEIVED_EXTRACTED_RESOURCE_TRAIL(9),
    SENT_PROCESSED_RESOURCE_TRAIL(10),
    RECEIVED_PROCESSED_RESOURCE_TRAIL(11);
    
    private int value;
    
    Event(int value) {
    	this.value = value;
    }
    
    public int getValue() {
    	return this.value;
    }
    
}
