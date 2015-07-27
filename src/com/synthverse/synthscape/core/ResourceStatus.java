package com.synthverse.synthscape.core;

final public class ResourceStatus implements Constants, Cloneable {

	// these are where the resource is NOW (because it can be moved around)
	public int x;
	public int y;

	// these are where the resource was ORIGINALLY seeded
	public int originX;
	public int originY;

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
		this.originX = INVALID;
		this.originY = INVALID;
		this.detectionStep = INVALID;
		this.extractionStep = INVALID;
		this.processingStep = INVALID;
		this.captureStep = INVALID;
		this.numTimesDetected = 0;
		this.numTimesLoaded = 0;
		this.numTimesUnloaded = 0;
	}

	public ResourceStatus clone() throws CloneNotSupportedException {
		ResourceStatus cloned = (ResourceStatus) super.clone();
		cloned.state = this.state;
		cloned.x = this.x;
		cloned.y = this.y;
		cloned.originX = this.originX;
		cloned.originY = this.originY;
		cloned.detectionStep = this.detectionStep;
		cloned.extractionStep = this.extractionStep;
		cloned.processingStep = this.processingStep;
		cloned.captureStep = this.captureStep;
		cloned.numTimesDetected = this.numTimesDetected;
		cloned.numTimesLoaded = this.numTimesLoaded;
		cloned.numTimesUnloaded = this.numTimesUnloaded;
		return cloned;
	}

	final public void cloneTo(ResourceStatus status) {
		status.state = this.state;
		status.x = this.x;
		status.y = this.y;
		status.originX = this.originX;
		status.originY = this.originY;
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
		return "ResourceStatus [o.x=" + originX + ", o.y=" + originY + ", x=" + x + ", y=" + y + ", detectionStep="
				+ detectionStep + ", extractionStep=" + extractionStep + ", processingStep=" + processingStep
				+ ", captureStep=" + captureStep + ", numTimesDetected=" + numTimesDetected + ", numTimesLoaded="
				+ numTimesLoaded + ", numTimesUnloaded=" + numTimesUnloaded + ", state=" + state + "]";
	}

}
