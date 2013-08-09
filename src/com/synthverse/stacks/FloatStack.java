/**
 * @(#)FloatStack.java  1:35:56 PM Sep 1, 2008
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

import java.util.Arrays;

import com.synthverse.util.ArrayUtils;

import ec.util.MersenneTwisterFast;

/**
 * Implementation of the Float Stack. For efficiency reason we are not using
 * Java's Generics to implement the stack; we're using the primitive types. With
 * Generics we would have to use objects and incur the expense of creating
 * objects.
 * 
 * The Stacks are defined to have a specific capacity. We can keep pushing
 * values - if the stack has space for it, it will be pushed; no Exceptions are
 * thrown to indicate that push was unsuccessful. Client classes should call
 * isFull() prior to push() in order to determine if push() will be a success.
 * 
 * pop() is similarly defined. However, if the stack is empty a default value is
 * returned. Client classes should call isEmpty prior to pop() to determine if
 * pop() will return a non-stack value.
 * 
 * @author sadat
 * 
 */
public final class FloatStack extends AbstractStack {

    private double defaultValue = Config.DEFAULT_FLOAT_VALUE;

    private double[] stack = null;

    /**
     * Push a value into the stack. Size and index are incremented if there is
     * space left; if stack is full, no operations are performed.
     * 
     * @param value
     */
    public final FloatStack push(double value) {
	if (!isFull()) {
	    index++;
	    size++;
	    stack[index] = value;
	}
	return this;
    }

    /**
     * Pops a value off the stack. Size and index are decremented if an element
     * was actually popped off; otherwise no operations are performed and
     * nothing is decremented.
     * 
     * @return
     */
    public final double pop() {
	double returnValue = defaultValue;

	if (!isEmpty()) {

	    double value = stack[index];
	    index--;
	    size--;
	    returnValue = value;
	}

	return returnValue;
    }

    /**
     * "Glances" at the topmost value on the stack. If the stack is empty, the
     * default value (see {@link #getDefaultValue()}) is returned.
     * 
     * @return
     */
    public final double glance() {
	double returnValue = defaultValue;

	if (size > 0) {
	    returnValue = stack[index];
	}

	return returnValue;
    }

    /**
     * This duplicates the top value without popping anything off the stack
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void dup() {
	if (size > 0) {
	    double topMostValue = stack[index];
	    push(topMostValue);
	}
    }

    public final void reverse() {
	if (size > 1) {
	    ArrayUtils.reverseDoubleArrayInRange(stack, index);
	}
    }

    /**
     * This pops the top two values, compares them and inserts true if they are
     * equal, false otherwise.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final boolean equal() {
	if (size > 1) {
	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (topMostValue == secondTopMostValue) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This swaps the top two values in the stack
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void swap() {
	if (size > 1) {
	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    push(topMostValue);
	    push(secondTopMostValue);
	}
    }

    /**
     * This pulls out the third element and pushes it on top
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void rot() {
	if (size > 2) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    double thirdTopMostValue = pop();

	    push(secondTopMostValue);
	    push(topMostValue);
	    push(thirdTopMostValue);

	}
    }

    /**
     * Pushes a random value into the stack.
     * 
     */
    public final void rand() {
	push(randomNumberGenerator.nextDouble());
    }

    /**
     * This moves an item deep from the stack and pushes it on the top. The
     * index of the item appears on top of the INTEGER stack. If the index is
     * less than 0, it is set to 0; if it is more than the number of elements
     * then its set to the last item's index.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void yank(int yankIndex) {
	if (size > 0) {

	    if (yankIndex < 0) {
		yankIndex = 0;
	    }

	    // the arrayYankIndex is the actual element that needs to be yanked
	    int arrayYankIndex = size - yankIndex - 1;

	    // sanity
	    if (arrayYankIndex < 0) {
		arrayYankIndex = 0;
	    }

	    // save value
	    double yankValue = stack[arrayYankIndex];

	    // now move everything from right to left
	    for (int i = 0; i < (size - arrayYankIndex - 1); i++) {
		stack[arrayYankIndex + i] = stack[arrayYankIndex + i + 1];
	    }
	    stack[size - 1] = yankValue;

	}
    }

    /**
     * This copies an item deep from the stack and pushes it on the top. The
     * index of the item appears on top of the INTEGER stack. If the index is
     * less than 0, it is set to 0; if it is more than the number of elements
     * then its set to the last item's index.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void yankdup(int yankIndex) {
	if (size > 0) {

	    if (yankIndex < 0) {
		yankIndex = 0;
	    }

	    // the arrayYankIndex is the actual element that needs to be yanked
	    int arrayYankIndex = size - yankIndex - 1;

	    // sanity
	    if (arrayYankIndex < 0) {
		arrayYankIndex = 0;
	    }

	    // save value
	    double yankValue = stack[arrayYankIndex];

	    push(yankValue);

	}
    }

    /**
     * Pushes 1 if the top of the boolean stack is true; false, otherwise
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void fromBoolean(boolean booleanValue) {
	if (booleanValue) {
	    push(1.0f);
	} else {
	    push(0.0f);
	}
    }

    /**
     * Pushes the converted value of an integer
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void fromInteger(int intValue) {
	push((double) intValue);
    }

    public final void fromCode(Instruction instruction) {
	push(InstructionTranslator.toCode(instruction));
    }

    /**
     * Pushes 2nd item modulo 1st item in stack. The values are popped first.
     * Does a NOOP if 1st item is 0.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void mod() {
	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (topMostValue > 0) {
		push(secondTopMostValue % topMostValue);
	    }
	}
    }

    /**
     * Pushes 2nd item * 1st item in stack. The values are popped first.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void mul() {
	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    push(secondTopMostValue * topMostValue);
	}
    }

    /**
     * Pushes 2nd item / 1st item in stack. The values are popped first. Does a
     * NOOP if 1st item is 0.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void div() {
	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (topMostValue > 0) {
		push(secondTopMostValue / topMostValue);
	    }
	}
    }

    /**
     * Pushes 2nd item - 1st item in stack. The values are popped first.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void sub() {
	if (size > 1) {
	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    push(secondTopMostValue - topMostValue);
	}
    }

    /**
     * Pushes 2nd item + 1st item in stack. The values are popped first.
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void add() {
	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    push(secondTopMostValue + topMostValue);
	}
    }

    /**
     * This pops the top two values, compares them and inserts true in the
     * boolean stack if the 2nd item is less than the first; false, otherwise
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final boolean lessThan() {

	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (secondTopMostValue < topMostValue) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This pops the top two values, compares them and inserts true in the
     * boolean stack if the 2nd item is <= 1st; false, otherwise
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final boolean lessThanEquals() {

	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (secondTopMostValue <= topMostValue) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This pops the top two values, compares them and inserts true in the
     * boolean stack if the 2nd item is greater than the first; false, otherwise
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final boolean greaterThan() {

	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (secondTopMostValue > topMostValue) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This pops the top two values, compares them and inserts true in the
     * boolean stack if the 2nd item is >= the first; false, otherwise
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final boolean greaterThanEquals() {

	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (secondTopMostValue >= topMostValue) {
		return true;
	    }
	}
	return false;
    }

    /**
     * This pops the top two values, compares them and pushes the max of the two
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void max() {

	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (secondTopMostValue > topMostValue) {
		push(secondTopMostValue);
	    } else {
		push(topMostValue);
	    }
	}
    }

    /**
     * This pops the top two values, compares them and pushes the min of the two
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void min() {

	if (size > 1) {

	    double topMostValue = pop();
	    double secondTopMostValue = pop();
	    if (secondTopMostValue > topMostValue) {
		push(topMostValue);
	    } else {
		push(secondTopMostValue);
	    }
	}
    }

    public final boolean isZero() {
	if (size > 0) {

	    double topMostValue = pop();
	    if (topMostValue == 0.0f) {
		return true;
	    }
	}
	return false;
    }

    public final boolean isPositive() {
	if (size > 0) {

	    double topMostValue = pop();
	    if (topMostValue >= 0.0f) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Pushes COS of top value
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void cos() {
	if (size > 0) {

	    double topMostValue = pop();
	    push(Math.cos(topMostValue));
	}
    }

    public final void cosh() {
	if (size > 0) {
	    push(Math.cosh(pop()));
	}
    }

    public final void acos() {
	if (size > 0) {
	    push(Math.acos(pop()));
	}
    }

    /**
     * Pushes SIN of top value
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void sin() {
	if (size > 0) {

	    double topMostValue = pop();
	    push(Math.sin(topMostValue));

	}
    }

    public final void asin() {
	if (size > 0) {
	    push(Math.asin(pop()));
	}
    }

    public final void sinh() {
	if (size > 0) {
	    push(Math.sinh(pop()));
	}
    }

    /**
     * Pushes TAN of top value
     * 
     * Does a NOOP if appropriate parameters are missing from the stack.
     */
    public final void tan() {
	if (size > 0) {
	    push(Math.tan(pop()));

	}
    }

    public final void atan() {
	if (size > 1) {
	    push(Math.atan(pop()));

	}
    }

    public final void atan2() {
	if (size > 1) {
	    push(Math.atan2(pop(), pop()));

	}
    }

    public final void tanh() {
	if (size > 0) {
	    push(Math.tanh(pop()));

	}
    }

    public final void cbrt() {
	if (size > 0) {
	    push(Math.cbrt(pop()));

	}
    }

    public final void sqrt() {
	if (size > 0) {
	    push(Math.sqrt(pop()));

	}
    }

    public final void ceil() {
	if (size > 0) {
	    push(Math.ceil(pop()));

	}
    }

    public final void floor() {
	if (size > 0) {
	    push(Math.floor(pop()));

	}
    }

    public final void exp() {
	if (size > 0) {
	    push(Math.exp(pop()));

	}
    }

    public final void expm1() {
	if (size > 0) {
	    push(Math.expm1(pop()));

	}
    }

    public final void log() {
	if (size > 0) {
	    push(Math.log(pop()));

	}
    }

    public final void log10() {
	if (size > 0) {
	    push(Math.log10(pop()));

	}
    }

    public final void log1p() {
	if (size > 0) {
	    push(Math.log1p(pop()));

	}
    }

    public final void hypot() {
	if (size > 0) {
	    push(Math.hypot(pop(), pop()));

	}
    }

    public final void pow() {
	if (size > 0) {
	    push(Math.pow(pop(), pop()));

	}
    }

    public final void rad2deg() {
	if (size > 0) {
	    push(Math.toDegrees(pop()));
	}
    }

    public final void deg2rad() {
	if (size > 0) {
	    push(Math.toRadians(pop()));
	}
    }

    public final void increment() {
	if (size > 0) {
	    stack[index]++;
	} else {
	    push(getDefaultValue());
	}
    }

    public final void decrement() {
	if (size > 0) {
	    stack[index]--;
	}
    }

    public final void abs() {
	if (size > 0) {
	    stack[index] = Math.abs(stack[index]);
	}
    }

    /**
     * Creates an empty stack with a default capacity defined by
     * {@link Config#DEFAULT_FLOAT_STACK_CAPACITY}.
     * 
     */
    public FloatStack(MersenneTwisterFast randomNumberGenerator) {
	super(randomNumberGenerator);
	capacity = Config.DEFAULT_FLOAT_STACK_CAPACITY;
	stack = new double[capacity];
	index = AbstractStack.BOTTOM_INDEX;
	size = 0;
    }

    /**
     * Sets the default value to return when {@link #pop()} is called on an
     * empty Stack (see {@link #isEmpty()}).
     * 
     * @param value
     */
    public final void setDefaultValue(double value) {
	defaultValue = value;
    }

    /**
     * Returns the default value (see {@link #setDefaultValue(double)}).
     * 
     * @return
     */
    public final double getDefaultValue() {
	return defaultValue;
    }

    /**
     * Sets the capacity of the Stack. Here are the rules: (1) Does nothing if
     * newCapacity<=0 or newCapacity == current capacity, (2) Retains all
     * content if newCapacity>capacity, (3) Retains as much as possible if
     * newCapacity<capacity. Size and Index is adjusted accordingly: i.e. for
     * newCapacity>capacity size and index remain same but for
     * newCapacity<capacity size and index are changed to newCapacity-1
     * 
     * @param newCapacity
     * @param retainContent
     *            true if we wish to retain the content of the old old stack
     */
    public final void setCapacity(int newCapacity, boolean retainContent) {

	if (newCapacity <= 0) {
	    // invalid new capacity, do nothing and just return.
	    return;
	}

	if (newCapacity == capacity) {
	    // no change - so just return.
	    return;
	}

	// create the new stack
	double[] newStack = new double[newCapacity];

	// change capacity
	capacity = newCapacity;

	// we need to copy array content if we're retaining content.
	if (retainContent) {
	    // retain old contents

	    // do memory management depending on the new size
	    if (newCapacity > capacity) {
		// we can retain old content completely.
		// size need not change.
		// index need not change.
		System.arraycopy(stack, 0, newStack, 0, size);

		stack = null; // garbage collect old stack!
	    } else {
		// we cannot retain old content completely. we'll
		// retain as much as we can.
		// size will change to newCapacity.
		// index will change to newCapacity-1, effectively this will
		// be a full stack.
		System.arraycopy(stack, 0, newStack, 0, newCapacity);

		stack = null; // garbage collect old stack!

		size = newCapacity;
		index = newCapacity - 1;
	    }

	    stack = newStack;

	} else {
	    // DO NOT retain old content
	    stack = null; // this garbage collects old content
	    stack = newStack;
	    size = 0;
	    index = AbstractStack.BOTTOM_INDEX;
	}

    }

    /**
     * Returns the current capacity of the stack
     * 
     * @return
     */
    public final int getCapacity() {
	return capacity;
    }

    @Override
    public int hashCode() {
	return Arrays.hashCode(stack);

    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof FloatStack))
	    return false;
	FloatStack other = (FloatStack) obj;
	if (!Arrays.equals(stack, other.stack))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	if (size > 0) {

	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(stack[0]);

	    for (int i = 1; i < size; i++) {
		buf.append(", ");
		buf.append(stack[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }

}
