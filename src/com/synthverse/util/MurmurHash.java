/**
 * @(#)MurmurHash.java  7:39:02 PM Jul 14, 2009
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
 * This is a very fast, non-cryptographic hash suitable for general hash-based
 * lookup. See http://murmurhash.googlepages.com/ for more details.
 * 
 * <p>
 * The C version of MurmurHash 2.0 found at that site was ported to Java by
 * Andrzej Bialecki (ab at getopt org).
 * </p>
 */
public class MurmurHash {
    public static int hash(byte[] data, int seed) {
	int m = 0x5bd1e995;
	int r = 24;

	int h = seed ^ data.length;

	int len = data.length;
	int len_4 = len >> 2;

	for (int i = 0; i < len_4; i++) {
	    int i_4 = i << 2;
	    int k = data[i_4 + 3];
	    k = k << 8;
	    k = k | (data[i_4 + 2] & 0xff);
	    k = k << 8;
	    k = k | (data[i_4 + 1] & 0xff);
	    k = k << 8;
	    k = k | (data[i_4 + 0] & 0xff);
	    k *= m;
	    k ^= k >>> r;
	    k *= m;
	    h *= m;
	    h ^= k;
	}

	int len_m = len_4 << 2;
	int left = len - len_m;

	if (left != 0) {
	    if (left >= 3) {
		h ^= (int) data[len - 3] << 16;
	    }
	    if (left >= 2) {
		h ^= (int) data[len - 2] << 8;
	    }
	    if (left >= 1) {
		h ^= (int) data[len - 1];
	    }

	    h *= m;
	}

	h ^= h >>> 13;
	h *= m;
	h ^= h >>> 15;

	return h;
    }

    /*
     * Testing ... static int NUM = 1000;
     * 
     * public static void main(String[] args) { byte[] bytes = new byte[4]; for
     * (int i = 0; i < NUM; i++) { bytes[0] = (byte)(i & 0xff); bytes[1] =
     * (byte)((i & 0xff00) >> 8); bytes[2] = (byte)((i & 0xff0000) >> 16);
     * bytes[3] = (byte)((i & 0xff000000) >> 24);
     * System.out.println(Integer.toHexString(i) + " " +
     * Integer.toHexString(hash(bytes, 1))); } }
     */
}
