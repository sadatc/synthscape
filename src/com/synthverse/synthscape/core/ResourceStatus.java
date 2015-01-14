package com.synthverse.synthscape.core;

final public class ResourceStatus implements Constants {

	public int x;
	public int y;
	public int detectionStep;
	public int extractionStep;
	public int processingStep;
	public int captureStep;

	public int numTimesDetected;
	public int numTimesLoaded;
	public int numTimesUnloaded;
	public ResourceState state;

	public ResourceStatus() {
		clear();
	}

	final public void clear() {
		this.state = ResourceState.NULL;
		this.x = INVALID;
		this.y = INVALID;
		this.detectionStep = INVALID;
		this.extractionStep = INVALID;
		this.processingStep = INVALID;
		this.captureStep = INVALID;
		this.numTimesDetected = 0;
		this.numTimesLoaded = 0;
		this.numTimesUnloaded = 0;
	}

	final public void cloneTo(ResourceStatus status) {
		status.state = this.state;
		status.x = this.x;
		status.y = this.y;
		status.detectionStep = this.detectionStep;
		status.extractionStep = this.extractionStep;
		status.processingStep = this.processingStep;
		status.captureStep = this.captureStep;
		status.numTimesDetected = this.numTimesDetected;
		status.numTimesLoaded = this.numTimesLoaded;
		status.numTimesUnloaded = this.numTimesUnloaded;

	}

	@Override
	public String toString() {
		return "ResourceStatus [x=" + x + ", y=" + y + ", detectionStep="
				+ detectionStep + ", extractionStep=" + extractionStep
				+ ", processingStep=" + processingStep + ", captureStep="
				+ captureStep + ", numTimesDetected=" + numTimesDetected
				+ ", numTimesLoaded=" + numTimesLoaded + ", numTimesUnloaded="
				+ numTimesUnloaded + ", state=" + state + "]";
	}

}
