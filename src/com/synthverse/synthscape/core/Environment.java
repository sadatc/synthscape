package com.synthverse.synthscape.core;

public enum Environment {
	RANDOM("r"), DIFFICULT("d"), VERY_DIFFICULT("v");
	String shortName;

	Environment(String shortName) {
		this.shortName = shortName;

	}
}
