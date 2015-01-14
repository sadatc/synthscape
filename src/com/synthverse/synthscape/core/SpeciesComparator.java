package com.synthverse.synthscape.core;

import java.util.Comparator;

public class SpeciesComparator implements Comparator<Species> {

	@Override
	public int compare(Species s1, Species s2) {
		int result = 0;

		if (s1 != null && s2 != null) {
			result = s1.getName().compareToIgnoreCase(s2.getName());
		}

		return result;
	}

}
