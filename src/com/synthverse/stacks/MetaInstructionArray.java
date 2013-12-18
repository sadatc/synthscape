/**
 * @(#)MetaInstructionArray.java  8:53:55 AM Jul 6, 2009
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

import com.synthverse.synthscape.core.D;
import com.synthverse.util.ArrayUtils;

/**
 * We're using a plain array to hold the DNA (program) for the following
 * reasons: 1. Making it a stack would make it less efficient 2. Real DNA works
 * this way
 * 
 * 
 * @author sadat
 * 
 */
public class MetaInstructionArray {

    private MetaInstruction[] program = null;
    private int size;

    MetaInstructionArray() {
	this.size = Config.DEFAULT_PROGRAM_ARRAY_SIZE;
	this.program = new MetaInstruction[size];
	Arrays.fill(program, MetaInstruction.NOOP);
    }

    public final void copyFromArray(MetaInstruction[] array, int size) {
	for (int i = 0; i < size; i++) {
	    program[i] = array[i];
	}
    }

    public final MetaInstruction[] getInternalArray() {
	return program;
    }

    public final void setInternalArray(MetaInstruction[] array) {
	program = array;
    }

    
    public final int getSize() {
	return size;
    }

    public final void setValue(int index,MetaInstruction instruction) {
	this.program[index] = instruction;
    }

    public final MetaInstruction getValue(int index) {
	return this.program[index];
    }

    public final boolean isValidIndex(int index) {
	return (index >= 0 && index < size);
    }

    public final void loadProgram(Program program2) {
	D.p(""+program2.getSignature());
	D.p(""+getSignature());
	program2.copyInto(this.program);
	D.p(""+getSignature());
    }

    public final void fill(MetaInstruction instruction) {
	Arrays.fill(program, instruction);
    }

    public final void reverse() {
	ArrayUtils.reverseInstructionArray(this.program);
    }

    public final void noop_left() {
	for (int i = 0; i < size; i++) {
	    if (program[i] !=MetaInstruction.NOOP) {
		program[i] =MetaInstruction.NOOP;
		return;
	    }
	}
    }

    public final void noop_right() {
	for (int i = size - 1; i >= 0; i--) {
	    if (program[i] !=MetaInstruction.NOOP) {
		program[i] =MetaInstruction.NOOP;
		return;
	    }
	}
    }

    public final void exchange_left(int index) {
	if (index > 0 && index < size) {
	   MetaInstruction tmp = program[0];
	    program[0] = program[index];
	    program[index] = tmp;
	}
    }

    public final void exchange_right(int index) {
	int lastIndex = size - 1;
	if (index >= 0 && index < lastIndex) {
	   MetaInstruction tmp = program[lastIndex];
	    program[lastIndex] = program[index];
	    program[index] = tmp;
	}
    }

    public final int findNOOPIndex(int startIndex) {
	int i = startIndex;

	while (i < size) {
	    if (program[i] == MetaInstruction.NOOP) {
		return i;
	    }
	    i++;
	}

	return -1;
    }
    
    public final int getSignature() {
   	int result = 0;
   	if (size > 0) {
   	    for(int i=0;i<size;i++) {
   		result+= program[i].getSignature();
   	    }
   	} else {
   	    return 0;
   	}
   	return result;
       }


    @Override
    public int hashCode() {
	return Arrays.hashCode(program);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof MetaInstructionArray))
	    return false;
	MetaInstructionArray other = (MetaInstructionArray) obj;
	if (!Arrays.equals(program, other.program))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	if (size > 0) {

	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(program[0]);

	    for (int i = 1; i < size; i++) {
		buf.append(", ");
		buf.append(program[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }
}