package com.synthverse.synthscape.core;

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

}
