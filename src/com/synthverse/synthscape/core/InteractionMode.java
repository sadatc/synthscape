package com.synthverse.synthscape.core;

/**
 * @author sadat
 * All possible interaction modes. An agent can be in one of these modes
 *
 */
public enum InteractionMode {
	NONE, 
	SENDING_TRAIL, RECEIVING_TRAIL, FOLLOWING_TRAIL, 
	SENDING_BROADCAST, RECEIVING_BROADCAST, FOLLOWING_BROADCAST, 
	SENDING_UNICAST, RECEIVING_UNICAST, FOLLOWING_UNICAST;

}
