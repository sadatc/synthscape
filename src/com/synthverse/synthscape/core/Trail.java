package com.synthverse.synthscape.core;

import org.apache.lucene.util.OpenBitSet;

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



	static long idCounter = 0;
	static OpenBitSet sent = new OpenBitSet(Constants.DEFAULT_BITSET_SIZE);
	long id;

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
		sent = null;
		sent = new OpenBitSet(Constants.DEFAULT_BITSET_SIZE);
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

	final static public long getUsed() {

		return Trail.idCounter-sent.cardinality();
	}

}
