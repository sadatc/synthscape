package com.synthverse.synthscape.core;

public enum EvolutionaryModel {
	ISLAND_MODEL("i"), EMBODIED_MODEL("e"), ALIFE_MODEL("a");
	public String shortName;

	EvolutionaryModel(String shortName) {
		this.shortName = shortName;
	}

}
