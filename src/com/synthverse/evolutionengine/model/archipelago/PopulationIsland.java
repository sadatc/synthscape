package com.synthverse.evolutionengine.model.archipelago;

import java.util.Set;

import com.synthverse.synthscape.core.Trait;

/**
 * A population island maintains a gene pool that produces agents with a specific
 * set of traits. 
 * 
 * @author sadat
 * 
 */
public class PopulationIsland {
	private int populationSize;
	private Set<Trait> traits;

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public Set<Trait> getTraits() {
		return traits;
	}

	public void setTraits(Set<Trait> traits) {
		this.traits = traits;
	}

}
