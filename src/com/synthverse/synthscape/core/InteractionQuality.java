package com.synthverse.synthscape.core;

public enum InteractionQuality {
	HIGHEST(1.0), HIGH(0.75), MEDIUM(0.5), POOR(0.25);

	private double level;

	InteractionQuality(double level) {
		this.level = level;
	}
}
