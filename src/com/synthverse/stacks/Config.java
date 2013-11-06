/**
 * @(#)Config.java  1:35:56 PM Sep 1, 2008
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

/**
 * 
 * Configurations that affect classes in this package.
 * 
 * @author sadat
 * 
 */
public final class Config {
    
    public final static int DEFAULT_ARRAY_SIZE = 64;
    public final static int DEFAULT_PROGRAM_ARRAY_SIZE = 128;
   
    

    public final static boolean USE_DEFAULT_RANDOM_SEED = true;

    public final static boolean USE_ENTITY_POOL = false;

    public final static int DEFAULT_ENTITY_CPU_CYCLES = 128;

    public final static int DEFAULT_ENTITY_CACHE_SIZE = 8096;

    public final static int DEFAULT_RANDOM_SEED = 123456789;

    public final static int MAX_RETRIES_FOR_ENTITY_FILTERS = 40;

    public final static boolean FILTER_ENTITY_DUPLICATES = false;

    
    
    
    
    //
    // The constants below should not be changed.
    //

    public final static int DEFAULT_CONTAINER_CAPACITY = 16;
    public final static int DEFAULT_STACK_CAPACITY = DEFAULT_CONTAINER_CAPACITY;
    public final static int DEFAULT_INTEGER_STACK_CAPACITY = DEFAULT_STACK_CAPACITY;
    public final static int DEFAULT_BOOLEAN_STACK_CAPACITY = DEFAULT_STACK_CAPACITY;
    public final static int DEFAULT_FLOAT_STACK_CAPACITY = DEFAULT_STACK_CAPACITY;

    
    public final static boolean RECYCLE_EXECUTION_FOR_EXCESSIVE_CPU_CYCLES = true;

    /**
     * CODE_STACK_CAPACITY and PRORGAM_ARRAY_SIZE should be the same
     */
    public final static int DEFAULT_CODE_STACK_CAPACITY = DEFAULT_PROGRAM_ARRAY_SIZE;

    public final static boolean DEFAULT_BOOLEAN_VALUE = false;
    public final static float DEFAULT_FLOAT_VALUE = 0F;
    public final static int DEFAULT_INTEGER_VALUE = 0;
    
    
    public final static String EMPTY_CONTAINER_STRING = "[]";

    public final static String LINE_SEPARATOR = System
	    .getProperty("line.separator");

    private Config() {
	throw new AssertionError("Config constructor is restricted");
    }

}
