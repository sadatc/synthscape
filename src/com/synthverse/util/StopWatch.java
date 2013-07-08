/**
 * @(#)StopWatch.java  1:35:56 PM Sep 1, 2008
 * 
 * Copyright (c) 2004-2009 Sadat Chowdhury (sadatc@gmail.com)
 * 
 * The author reserves all rights to this software. Please contact the author 
 * (sadatc@gmail.com) to obtain permission to use, modify, or redistribute this 
 * software in any form. 
 * 
 * Unless otherwise stated, the author issues no guarantees of success and 
 * offers no warranties against damages of any kind. 
 * 
 * Last Revision : $Rev:: 6                     $: 
 * Last Updated  : $Date:: 2012-10-14 22:50:12 #$:
 * 
 */
package com.synthverse.util;

/**
 * Provides methods to time other methods or processes.
 * 
 * @author sadat
 * 
 */
public final class StopWatch {
	
	private static final double THOUSAND_D = 1000.0f;
	private static final long MILLION_L = 1000000L;
	private static final double MILLION_D = 1000000.0f;
	
	
	private static long tick = System.nanoTime();

	/**
	 * Restricted constructor
	 */
	private StopWatch() {
		throw new AssertionError("StopWatch constructor is restricted");
	}

	/**
	 * Reset timer
	 */
	public final static void reset() {
		tick = System.nanoTime();
	}

	/**
	 * Returns nanoseconds elapsed since last call to {@link #reset()}
	 * 
	 * @return time in ns
	 */
	public final static long getElapsedNanos() {
		long now = System.nanoTime();
		long elapsed = now - tick;
		tick = now;

		return elapsed;
	}

	/**
	 * Returns milliseconds elapsed since last call to {@link #reset()}
	 * 
	 * @return time in ms.
	 */
	public final static long getElapsedMillis() {

		long elapsed = getElapsedNanos();

		return elapsed/MILLION_L;
	}

	
	
	/**
	 * Given the number of operations specified, this method uses time elapsed since last {@link #reset()}
	 * to calculate and return speed of operations performed in Mhz.
	 * @param operations
	 * @return
	 */
	public final static long getSpeedInMhz(long operations) {
		// elapsed time in seconds
		long elapsed = (System.nanoTime() - tick);
		
		long speed =  (long)((double)operations / (double)((double)elapsed/THOUSAND_D));
		return speed;
		
	}
	
	/**
	 * Given the number of operations specified, this method uses time elapsed since last {@link #reset()}
	 * to calculate and return tspeed of operations performed in Khz.
	 * @param operations
	 * @return
	 */
	public final static long getSpeedInKhz(long operations) {
		// elapsed time in seconds
		long elapsed = (System.nanoTime() - tick);
		
		long speed =  (long)((double)operations / (double)((double)elapsed/MILLION_D));
		return speed;
		
	}
}
