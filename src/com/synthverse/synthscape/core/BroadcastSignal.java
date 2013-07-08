package com.synthverse.synthscape.core;

/**
 * Broadcast Signal has a point of origin and a sender id
 * 
 * @author sadat
 *
 */
public class BroadcastSignal {

	public long senderAgentId;
	public int x;
	public int y;
	
	public BroadcastSignal() {
		this(-1,-1,-1);
	}
	
	public BroadcastSignal(long senderAgentId, int x, int y) {
		this.senderAgentId = senderAgentId;
		this.x = x;
		this.y = y;
		
	}
	
}
