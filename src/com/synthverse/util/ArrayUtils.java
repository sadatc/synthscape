/**
 * @(#)ArrayUtils.java  9:27:04 AM Jul 6, 2009
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

import com.synthverse.stacks.Instruction;

public class ArrayUtils {

    private ArrayUtils() {
	throw new AssertionError("ArrayUtils constructor is restricted");
    }

    public static final void reverseBooleanArrayInRange(boolean[] b,
	    int rightIndex) {
	for (int left = 0, right = rightIndex; left < right; left++, right--) {
	    boolean temp = b[left];
	    b[left] = b[right];
	    b[right] = temp;
	}
    }

    public static final void reverseIntArray(int[] b) {
	for (int left = 0, right = b.length - 1; left < right; left++, right--) {
	    int temp = b[left];
	    b[left] = b[right];
	    b[right] = temp;
	}
    }

    public static final void reverseDoubleArrayInRange(double[] b,
	    int rightIndex) {
	for (int left = 0, right = rightIndex; left < right; left++, right--) {
	    double temp = b[left];
	    b[left] = b[right];
	    b[right] = temp;
	}
    }

    public static final void reverseIntArrayInRange(int[] b, int rightIndex) {
	for (int left = 0, right = rightIndex; left < right; left++, right--) {
	    int temp = b[left];
	    b[left] = b[right];
	    b[right] = temp;
	}
    }

    public static final void reverseInstructionArray(Instruction[] b) {
	for (int left = 0, right = b.length - 1; left < right; left++, right--) {
	    Instruction temp = b[left];
	    b[left] = b[right];
	    b[right] = temp;
	}
    }

    public static final void reverseInstructionArrayInRange(Instruction[] b,
	    int rightIndex) {
	for (int left = 0, right = rightIndex; left < right; left++, right--) {
	    Instruction temp = b[left];
	    b[left] = b[right];
	    b[right] = temp;
	}
    }

}
