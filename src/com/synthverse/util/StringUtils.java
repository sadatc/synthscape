package com.synthverse.util;

public class StringUtils {
    
    public static String[] parseArguments(String string) {
	String[] array = string.split(" ");
	return array;
    }


}
