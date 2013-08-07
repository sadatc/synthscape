package com.synthverse.synthscape.core;

public enum InteractionMechanism {
    NONE(0, "none"), TRAIL(1, "trail"), BROADCAST(2, "broadcast"), UNICAST_CLOSEST_AGENT(3,
	    "unicast_closest_agent"), UNICAST_CLIQUE_MEMBER(4, "unicast_clique");

    private int id;
    private String name;

    InteractionMechanism(int id, String name) {
	this.id = id;
	this.name = name;
    }

    public int getId() {
	return id;
    }

    public String getName() {
	return name;
    }

}
