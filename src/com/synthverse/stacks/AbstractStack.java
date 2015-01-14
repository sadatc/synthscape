/**
 * @(#)AbstractStack.java  1:26:25 AM Feb 26, 2009
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

package com.synthverse.stacks;

import ec.util.MersenneTwisterFast;

/**
 * The Stack superclass. Subclasses need not override most of the fields and
 * this has been written merely to save typing them in the subclasses.
 * 
 * Subclasses should provide a means to create the stack
 * 
 * @author sadat
 * 
 */
public abstract class AbstractStack {

	/**
	 * Subclasses can view this - but need not redefine it. This is a constant
	 * defining the most bottom index (i.e. empty stack position).
	 */
	protected static final int BOTTOM_INDEX = -1;

	/**
	 * A Subclasses can use a different value for this - if needed.
	 */
	protected int capacity = Config.DEFAULT_STACK_CAPACITY;

	/**
	 * We start with the empty index. Subclasses should not override this: but
	 * increment/decrement it as required. If stack is empty, index should
	 * BOTTOM_INDEX. If stack is full, index should be (capacity-1)
	 */
	protected int index = BOTTOM_INDEX;

	/**
	 * We start with an empty stack. Subclasses should definitely increment and
	 * decrement this depending on the number of elements in the stack. This
	 * should reflect the number of elements in the stack.
	 */
	protected int size = 0;

	public MersenneTwisterFast randomNumberGenerator = null;

	/**
	 * True if stack is empty. Subclasses can not override this.
	 * 
	 * @return true if empty
	 */
	public final boolean isEmpty() {
		return (index == BOTTOM_INDEX);
	}

	/**
	 * True if stack is full.
	 * 
	 * @return
	 */
	public final boolean isFull() {
		return (index == (capacity - 1));
	}

	/**
	 * Returns the number of elements contained within the stack.
	 * 
	 * @return
	 */
	public final int size() {
		return size;
	}

	/**
	 * This empties the stack. For efficiency, it doesn't go item by item and
	 * actually garbage collect or delete individually, but rather sets the
	 * index to {@link #BOTTOM_INDEX} signifying that it's effectively empty
	 */
	public final void flush() {
		index = BOTTOM_INDEX;
		size = 0;

	}

	public MersenneTwisterFast getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

	public void setRandomNumberGenerator(
			MersenneTwisterFast randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	public AbstractStack(MersenneTwisterFast randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}
}
