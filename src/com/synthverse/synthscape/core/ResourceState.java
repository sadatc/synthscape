package com.synthverse.synthscape.core;

import sim.util.Valuable;

public enum ResourceState implements Valuable {
	NULL(0), RAW(1), EXTRACTED(2), PROCESSED(3);

	@SuppressWarnings("unused")
	private int value;

	ResourceState(int value) {
		this.value = value;

	}

	@Override
	public double doubleValue() {
		return ordinal();
	}
}
