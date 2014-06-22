package com.synthverse.synthscape.core;

/**
 * An interaction that has a source agent, target agent, location, and a signal
 * type
 * 
 * @author sadat
 * 
 */
public class Unicast {

    private Agent senderAgent;
    private Agent receiverAgent;
    
    private int senderX;
    private int senderY;

    private SignalType signalType;

    private int stepClock;

    public Unicast() {
	this(null, null, SignalType.GENERIC, 0);
    }

    public Unicast(Agent senderAgent, Agent receiverAgent, SignalType signalType, int stepClock) {
	this.senderAgent = senderAgent;
	this.receiverAgent = receiverAgent;
	this.signalType = signalType;
	this.stepClock = stepClock;
	if(senderAgent!=null) {
	    senderX = senderAgent.x;
	    senderY = senderAgent.y;
	}

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
	if(senderAgent!=null) {
	    senderX = senderAgent.x;
	    senderY = senderAgent.y;
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
	return "Unicast [senderAgent=" + senderAgent + ", receiverAgent=" + receiverAgent + ", signalType="
		+ signalType + ", stepClock=" + stepClock + "]";
    }

    
    
    
}
