package com.synthverse.synthscape.core;

/**
 * An interaction that has a source agent, location, and a signal type
 * 
 * @author sadat
 * 
 */
public class BroadcastSignal {

	private long senderAgentId;
	private SignalType signalType;
	private int x;
	private int y;

	public BroadcastSignal() {
		this(-1, SignalType.GENERIC, -1, -1);
	}

	public BroadcastSignal(long senderAgentId, SignalType signalType, int x,
			int y) {
		this.senderAgentId = senderAgentId;
		this.x = x;
		this.y = y;
		this.signalType = signalType;

	}

	public long getSenderAgentId() {
		return senderAgentId;
	}

	public void setSenderAgentId(long senderAgentId) {
		this.senderAgentId = senderAgentId;
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
	
	

}
