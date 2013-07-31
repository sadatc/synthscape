package com.synthverse.synthscape.core;

import java.util.HashSet;
import java.util.Set;

public class Species {
    public Set<Trait> traits = new HashSet<Trait>();

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
