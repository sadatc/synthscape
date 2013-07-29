package com.synthverse.synthscape.core;

/**
 * An interaction that has a source agent, location, and a signal type
 * 
 * @author sadat
 * 
 */
public class Broadcast {

	private Agent senderAgent;

	private SignalType signalType;
	private int x;
	private int y;

	public Broadcast() {
		this(null, SignalType.GENERIC, -1, -1);
	}

	public Broadcast(Agent senderAgent, SignalType signalType, int x, int y) {
		this.senderAgent = senderAgent;
		this.x = x;
		this.y = y;
		this.signalType = signalType;

	}

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

	public Agent getSenderAgent() {
		return senderAgent;
	}

	public void setSenderAgent(Agent senderAgent) {
		this.senderAgent = senderAgent;
	}
	

}
