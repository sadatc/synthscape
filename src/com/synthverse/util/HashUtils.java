/**
 * @(#)HashUtils.java  7:42:32 PM Jul 14, 2009
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

import java.nio.ByteBuffer;

import com.synthverse.stacks.Config;
import com.synthverse.stacks.Instruction;

/**
 * @author sadat
 *
 */
public class HashUtils {
	private HashUtils() {
		throw new AssertionError("HashUtils constructor is restricted");
	}
	
	
	
	public static final long getLongHash(byte[] data) {
		long hash = (long) MurmurHash.hash(data, Config.DEFAULT_RANDOM_SEED);
		hash  = hash << 32;
		hash += (long) JenkinsHash.getInstance().hash(data, data.length, Config.DEFAULT_RANDOM_SEED);
		return hash;
	}
	
	public static final long getLongHash(int[] data) {		
		ByteBuffer byteBuffer = ByteBuffer.allocate(4*data.length);
		for(int i=0;i<data.length;i++)
			byteBuffer.putInt(data[i]);
		byteBuffer.compact();
		return getLongHash(byteBuffer.array());
		
	}
	
	public static final long getLongHash(Instruction[] data, int size) {		
		ByteBuffer byteBuffer = ByteBuffer.allocate(4*size);
		for(int i=0;i<size;i++)
			byteBuffer.putInt(data[i].ordinal());
		byteBuffer.compact();
		return getLongHash(byteBuffer.array());
		
	}

}
