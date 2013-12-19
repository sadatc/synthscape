/**
 * @(#)Genotype.java  8:53:55 AM Jul 6, 2009
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
 * We're using a plain array to hold the DNA (genotypeArray) for the following
 * reasons: 1. Making it a stack would make it less efficient 2. Real DNA works
 * this way
 * 
 * 
 * @author sadat
 * 
 */
public class Genotype {

    private GenotypeInstruction[] genotypeArray = null;
    private int size;

    Genotype() {
	this.size = Config.DEFAULT_PROGRAM_ARRAY_SIZE;
	this.genotypeArray = new GenotypeInstruction[size];
	Arrays.fill(genotypeArray, GenotypeInstruction.NOOP);
    }

    public final void copyFromArray(GenotypeInstruction[] array, int size) {
	for (int i = 0; i < size; i++) {
	    genotypeArray[i] = array[i];
	}
    }

    public final GenotypeInstruction[] getInternalArray() {
	return genotypeArray;
    }

    public final void setInternalArray(GenotypeInstruction[] array) {
	genotypeArray = array;
    }

    
    public final int getSize() {
	return size;
    }

    public final void setValue(int index,GenotypeInstruction instruction) {
	this.genotypeArray[index] = instruction;
    }

    public final GenotypeInstruction getValue(int index) {
	return this.genotypeArray[index];
    }

    public final boolean isValidIndex(int index) {
	return (index >= 0 && index < size);
    }

    public final void overwriteWith(Program program2) {
	D.p(""+program2.getSignature());
	D.p(""+getSignature());
	program2.copyInto(this.genotypeArray);
	D.p(""+getSignature());
    }

    public final void fill(GenotypeInstruction instruction) {
	Arrays.fill(genotypeArray, instruction);
    }

    public final void reverse() {
	ArrayUtils.reverseInstructionArray(this.genotypeArray);
    }

    public final void noop_left() {
	for (int i = 0; i < size; i++) {
	    if (genotypeArray[i] !=GenotypeInstruction.NOOP) {
		genotypeArray[i] =GenotypeInstruction.NOOP;
		return;
	    }
	}
    }

    public final void noop_right() {
	for (int i = size - 1; i >= 0; i--) {
	    if (genotypeArray[i] !=GenotypeInstruction.NOOP) {
		genotypeArray[i] =GenotypeInstruction.NOOP;
		return;
	    }
	}
    }

    public final void exchange_left(int index) {
	if (index > 0 && index < size) {
	   GenotypeInstruction tmp = genotypeArray[0];
	    genotypeArray[0] = genotypeArray[index];
	    genotypeArray[index] = tmp;
	}
    }

    public final void exchange_right(int index) {
	int lastIndex = size - 1;
	if (index >= 0 && index < lastIndex) {
	   GenotypeInstruction tmp = genotypeArray[lastIndex];
	    genotypeArray[lastIndex] = genotypeArray[index];
	    genotypeArray[index] = tmp;
	}
    }

    public final int findNOOPIndex(int startIndex) {
	int i = startIndex;

	while (i < size) {
	    if (genotypeArray[i] == GenotypeInstruction.NOOP) {
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
   		result+= genotypeArray[i].getSignature();
   	    }
   	} else {
   	    return 0;
   	}
   	return result;
       }


    @Override
    public int hashCode() {
	return Arrays.hashCode(genotypeArray);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof Genotype))
	    return false;
	Genotype other = (Genotype) obj;
	if (!Arrays.equals(genotypeArray, other.genotypeArray))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	if (size > 0) {

	    StringBuilder buf = new StringBuilder();
	    buf.append('[');
	    buf.append(genotypeArray[0]);

	    for (int i = 1; i < size; i++) {
		buf.append(", ");
		buf.append(genotypeArray[i]);
	    }

	    buf.append("]");
	    return buf.toString();

	} else {
	    return Config.EMPTY_CONTAINER_STRING;
	}
    }
}
