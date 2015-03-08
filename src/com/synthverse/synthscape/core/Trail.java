package com.synthverse.synthscape.core;

import java.util.BitSet;

/**
 * An interaction that has a location and a signal type
 * 
 * @author sadat
 *
 */
public class Trail {
	private SignalType signalType;
	private int x;
	private int y;



	static int idCounter = 0;
	static BitSet sent = new BitSet(Constants.DEFAULT_BITSET_SIZE);
	int id;

	public Trail(SignalType signalType, int x, int y) {
		super();
		this.signalType = signalType;
		this.x = x;
		this.y = y;
		this.id = idCounter;
		idCounter++;
		sent.set(this.id);

	}

	public Trail() {
		this(SignalType.SIGNAL_A, -1, -1);
	}

	public Trail(int x, int y) {
		this(SignalType.SIGNAL_A, x, y);
	}

	final public static void resetSendReceiveCounters() {
		Trail.idCounter = 0;
		sent.clear();
	}

	final public static long getCounter() {
		return Trail.idCounter;
	}

	public long getId() {
		return id;
	}

	final public void markReceived() {
		sent.clear(this.id);
	}

	final static public int getUsed() {

		return sent.cardinality();
	}

}
