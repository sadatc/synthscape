package com.synthverse.synthscape.core;

import java.util.LinkedHashSet;
import java.util.Set;

public class Species {
    private Set<Trait> traits = new LinkedHashSet<Trait>();

    public void addTrait(Trait trait) {
	traits.add(trait);
    }

    public Set<Trait> getTraits() {
	return traits;
    }

    public String getAbbreviation() {
	String result = null;
	StringBuffer buffer = new StringBuffer();

	for (Trait trait : traits) {
	    buffer.append(trait.toString().charAt(0));
	}

	result = buffer.toString();

	return result;
    }

}
