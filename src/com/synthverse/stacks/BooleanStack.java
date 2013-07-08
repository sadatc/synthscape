/**
 * @(#)BooleanStack.java  1:35:56 PM Sep 1, 2008
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
 * Implementation of the Boolean Stack. For efficiency reason we are not using
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
public final class BooleanStack extends AbstractStack {

	private boolean defaultValue = Config.DEFAULT_BOOLEAN_VALUE;

	private boolean[] stack = null;

	/**
	 * Push a value into the stack. Size and index are incremented if there is
	 * space left; if stack is full, no operations are performed.
	 * @param value
	 * @return
	 */
	public final BooleanStack push(boolean value) {
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
	public final boolean pop() {
		boolean returnValue = defaultValue;

		if (!isEmpty()) {

			boolean value = stack[index];
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
	public final boolean glance() {
		boolean returnValue = defaultValue;

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
			boolean topMostValue = stack[index];
			push(topMostValue);
		}
	}

	public final void reverse() {
		if (size > 1) {
			ArrayUtils.reverseBooleanArrayInRange(stack,index);
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
			boolean topMostValue = pop();
			boolean secondTopMostValue = pop();
			if (topMostValue == secondTopMostValue) {
				push(true);
				return true;
			}
		}
		push(false);
		return false;

	}

	/**
	 * This swaps the top two values in the stack
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void swap() {
		if (size > 1) {
			boolean topMostValue = pop();
			boolean secondTopMostValue = pop();
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

			boolean topMostValue = pop();
			boolean secondTopMostValue = pop();
			boolean thirdTopMostValue = pop();

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
		push(randomNumberGenerator.nextBoolean());
	}

	/**
	 * This moves an item deep from the stack and pushes it on the top. The
	 * index of the item appears on top of the INTEGER stack. If the index is
	 * less than 0, it is set to 0; if it is more than the number of elements
	 * then its set to the last item's index.
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public void yank(int yankIndex) {
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
			boolean yankValue = stack[arrayYankIndex];

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
			boolean yankValue = stack[arrayYankIndex];

			push(yankValue);

		}
	}

	/**
	 * Pushes false if top of INTEGER stack is 0; true, otherwise.
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void fromInteger(int intValue) {
		if (intValue == 0) {
			push(false);
		} else {
			push(true);
		}

	}

	/**
	 * Pushes false if top of FLOAT stack is 0; true, otherwise.
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void fromFloat(double floatValue) {
		if (floatValue == 0) {
			push(false);
		} else {
			push(true);
		}
	}
	
	public final void fromCode(Instruction instruction) {
		push(InstructionTranslator.toCode(instruction)>0?true:false);
	}

	/**
	 * This pops the top two values, and pushes the ANDed values
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void and() {
		if (size > 1) {
			boolean topMostValue = pop();
			boolean secondTopMostValue = pop();
			push(topMostValue & secondTopMostValue);
		}
	}

	/**
	 * This pops the top two values, and pushes the ORed values
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void or() {
		if (size > 1) {
			boolean topMostValue = pop();
			boolean secondTopMostValue = pop();
			push(topMostValue | secondTopMostValue);
		}
	}

	/**
	 * This pops the top two values, and pushes the XORed values
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void xor() {
		if (size > 1) {
			boolean topMostValue = pop();
			boolean secondTopMostValue = pop();
			push(topMostValue ^ secondTopMostValue);
		}
	}

	/**
	 * This pops the top value, and pushes the NOT of that.
	 * 
	 * Does a NOOP if appropriate parameters are missing from the stack.
	 */
	public final void not() {
		if (size > 0) {
			boolean topMostValue = pop();
			push(!topMostValue);
		}
	}

	/**
	 * Creates an empty Boolean Stack with a default capacity defined by
	 * {@link Config#DEFAULT_BOOLEAN_STACK_CAPACITY}.
	 * 
	 */
	public BooleanStack(MersenneTwisterFast randomNumberGenerator) {
		super(randomNumberGenerator);
		capacity = Config.DEFAULT_BOOLEAN_STACK_CAPACITY;
		stack = new boolean[capacity];
		index = AbstractStack.BOTTOM_INDEX;
		size = 0;
	}

	/**
	 * Sets the default value to return when {@link #pop()} is called on an
	 * empty Stack (see {@link #isEmpty()}).
	 * 
	 * @param value
	 */
	public final void setDefaultValue(boolean value) {
		defaultValue = value;
	}

	/**
	 * Returns the default value (see {@link #setDefaultValue(boolean)}).
	 * 
	 * @return
	 */
	public final boolean getDefaultValue() {
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
		boolean[] newStack = new boolean[newCapacity];

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
		if (!(obj instanceof BooleanStack))
			return false;
		BooleanStack other = (BooleanStack) obj;
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
