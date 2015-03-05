package com.synthverse.synthscape.core;

import java.util.BitSet;

/**
 * An interaction that has a source agent, target agent, location, and a signal
 * type
 * 
 * @author sadat
 * 
 */
public class Unicast {

	static int idCounter = 0;
	static BitSet sent = new BitSet(1000000);
	int id;

	private Agent senderAgent;
	private Agent receiverAgent;

	private int senderX;
	private int senderY;

	private SignalType signalType;

	private int stepClock;

	public Unicast() {
		this(null, null, SignalType.SIGNAL_A, 0);
	}

	public Unicast(SignalType st) {
		this(null, null, st, 0);
	}

	public Unicast(Agent senderAgent, Agent receiverAgent,
			SignalType signalType, int stepClock) {
		this.senderAgent = senderAgent;
		this.receiverAgent = receiverAgent;
		this.signalType = signalType;
		this.stepClock = stepClock;
		if (senderAgent != null) {
			senderX = senderAgent.x;
			senderY = senderAgent.y;
		}
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

	public Agent getSenderAgent() {
		return senderAgent;
	}

	public void setSenderAgent(Agent senderAgent) {
		this.senderAgent = senderAgent;
		if (senderAgent != null) {
			this.senderX = senderAgent.x;
			this.senderY = senderAgent.y;
		}
	}

	public int getStepClock() {
		return stepClock;
	}

	public void setStepClock(int stepClock) {
		this.stepClock = stepClock;
	}

	public Agent getReceiverAgent() {
		return receiverAgent;
	}

	public void setReceiverAgent(Agent receiverAgent) {
		this.receiverAgent = receiverAgent;
	}

	@Override
	public String toString() {
		return "Unicast [senderAgent=" + senderAgent.getAgentId()
				+ ", receiverAgent=" + receiverAgent.getAgentId()
				+ ", signalType=" + signalType + ", stepClock=" + stepClock
				+ "]";
	}

	public int getSenderX() {
		return senderX;
	}

	public void setSenderX(int senderX) {
		this.senderX = senderX;
	}

	public int getSenderY() {
		return senderY;
	}

	public void setSenderY(int senderY) {
		this.senderY = senderY;
	}

	final public void markReceived() {
		sent.clear(this.id);
	}

	final static public int getUsed() {

		return sent.cardinality();
	}

	final public static void resetSendReceiveCounters() {
		Unicast.idCounter = 0;
		sent.clear();
	}
	
	final public static long getCounter() {
		return Unicast.idCounter;
	}

}
