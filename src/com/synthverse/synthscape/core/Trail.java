package com.synthverse.synthscape.core;

/**
 * An interaction that has a location and a signal type
 * @author sadat
 *
 */
public class Trail {
	private SignalType signalType;
	private int x;
	private int y;
	
	public SignalType getSignalType() {
		return signalType;
	}
	public void setSignalType(SignalType signalType) {
		this.signalType = signalType;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Trail(SignalType signalType, int x, int y) {
		super();
		this.signalType = signalType;
		this.x = x;
		this.y = y;
	}
	
	public Trail() {
		this(SignalType.GENERIC,-1,-1);
	}
	
	

}
