package com.synthverse.synthscape.core;

import org.apache.lucene.util.OpenBitSet;

/**
 * An interaction that has a source agent, location, and a signal type
 * 
 * @author sadat
 * 
 */
public class Broadcast {

	static long idCounter = 0;
	static OpenBitSet sent = new OpenBitSet(Constants.DEFAULT_BITSET_SIZE);
	long id;

	private Agent senderAgent;

	private SignalType signalType;
	private int x;
	private int y;
	private int stepClock;

	public Broadcast() {
		this(null, SignalType.SIGNAL_A, -1, -1, 0);
	}

	public Broadcast(Agent senderAgent, SignalType signalType, int x, int y,
			int stepClock) {
		this.senderAgent = senderAgent;
		this.x = x;
		this.y = y;
		this.signalType = signalType;
		this.stepClock = stepClock;
		this.id = idCounter;
		idCounter++;
		sent.set(this.id);
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

	public int getStepClock() {
		return stepClock;
	}

	public void setStepClock(int stepClock) {
		this.stepClock = stepClock;
	}

	final public static void resetSendReceiveCounters() {
		Broadcast.idCounter = 0;
		sent = null;
		sent = new OpenBitSet(Constants.DEFAULT_BITSET_SIZE);
	}

	final public static long getCounter() {
		return Broadcast.idCounter;
	}

	public long getId() {
		return id;
	}

	final public void markReceived() {
		sent.clear(this.id);
	}

	final static public long getUsed() {

		return Broadcast.idCounter-sent.cardinality();
	}

	@Override
	public String toString() {
		return "Broadcast [senderAgent=" + senderAgent.getAgentId()
				+ ", signalType=" + signalType + ", x=" + x + ", y=" + y
				+ ", stepClock=" + stepClock + "]";
	}

}
