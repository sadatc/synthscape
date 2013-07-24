package com.synthverse.synthscape.core;

import sim.util.Valuable;

public enum ResourceState implements Valuable {
	NULL, RAW, EXTRACTED, PROCESSED;

	@Override
	public double doubleValue() {
		return ordinal();
	}
}
