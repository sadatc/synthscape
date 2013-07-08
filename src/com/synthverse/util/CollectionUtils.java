/**
 * @(#)CollectionUtils.java  9:21:44 AM Jul 24, 2009
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

import java.util.ArrayList;
import java.util.Collection;

import ec.util.MersenneTwisterFast;

/**
 * @author sadat
 *
 */
public class CollectionUtils {


	private CollectionUtils() {
		throw new AssertionError("CollectionUtils constructor is restricted");
	}
	
	@SuppressWarnings("unused") 
	public static void clearDeeply(Collection<?> c) {
		for(Object object: c) {
			object = null;
		}
		c.clear();
	}

	public static <T> T pickRandomFromList(MersenneTwisterFast randomNumberGenerator, ArrayList<T> l) {
		return l.get(randomNumberGenerator.nextInt(l.size()));
	}
}
