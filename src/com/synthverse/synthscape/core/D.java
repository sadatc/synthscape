package com.synthverse.synthscape.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class D {
    public static void p(String debug) {
	System.out.println(debug);
    }

    public static void p() {
	System.out.println();
    }

    public static void debugArray(String string, int[][] array, int xs, int ys) {
	D.p(string);
	int nonZeroCounter = 0;
	for (int x = 0; x < xs; x++) {
	    for (int y = 0; y < ys; y++) {
		int value = array[x][y];
		System.out.print(value);
		if (value > 0) {
		    nonZeroCounter++;
		}
	    }
	    D.p();
	}
	D.p("total:" + nonZeroCounter);

    }
    
    public static void check(boolean condition, String message) {
	if(!condition) {
	    p(message);
	    p("Exiting Program!!");
	    System.exit(1);
	}
    }
    
    public static void pause() {
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	try {
	    stdin.readLine();
	} catch (IOException e) {
	    
	} 
    }
    

}
