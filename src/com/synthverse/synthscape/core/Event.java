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
    RECEIVED_PROCESSED_RESOURCE_TRAIL(13),
    SENT_GENERIC_TRAIL(14),
    RECEIVED_GENERIC_TRAIL(15),
    
    SENT_RAW_RESOURCE_BROADCAST(16),
    RECEIVED_RAW_RESOURCE_BROADCAST(17),
    SENT_EXTRACTED_RESOURCE_BROADCAST(18),
    RECEIVED_EXTRACTED_RESOURCE_BROADCAST(19),
    SENT_PROCESSED_RESOURCE_BROADCAST(20),
    RECEIVED_PROCESSED_RESOURCE_BROADCAST(21),
    SENT_GENERIC_BROADCAST(22),
    RECEIVED_GENERIC_BROADCAST(23),
    
    SENT_RAW_RESOURCE_UNICAST_CLOSEST(24),
    RECEIVED_RAW_RESOURCE_UNICAST_CLOSEST(25),
    SENT_EXTRACTED_RESOURCE_UNICAST_CLOSEST(26),
    RECEIVED_EXTRACTED_RESOURCE_UNICAST_CLOSEST(27),
    SENT_PROCESSED_RESOURCE_UNICAST_CLOSEST(28),
    RECEIVED_PROCESSED_RESOURCE_UNICAST_CLOSEST(29),
    SENT_GENERIC_UNICAST_CLOSEST(30),
    RECEIVED_GENERIC_UNICAST_CLOSEST(31),
    
    SENT_RAW_RESOURCE_UNICAST_CLIQUE(32),
    RECEIVED_RAW_RESOURCE_UNICAST_CLIQUE(33),
    SENT_EXTRACTED_RESOURCE_UNICAST_CLIQUE(34),
    RECEIVED_EXTRACTED_RESOURCE_UNICAST_CLIQUE(35),
    SENT_PROCESSED_RESOURCE_UNICAST_CLIQUE(36),
    RECEIVED_PROCESSED_RESOURCE_UNICAST_CLIQUE(37),
    SENT_GENERIC_UNICAST_CLIQUE(38),
    RECEIVED_GENERIC_UNICAST_CLIQUE(39),
    
    MOVE_TO_CLOSEST_AGENT(40),
    MOVE_TO_CLOSEST_COLLECTION_SITE(41),
    MOVE_TO_PRIMARY_COLLECTION_SITE(42),
    
    SEARCHED_GENERIC_TRAIL(43);
    
    
    
    private int id;
    
    Event(int id) {
    	this.id = id;
    }
    
    public int getId() {
    	return this.id;
    }
    
}
