package com.synthverse.synthscape.core;

import sim.field.grid.DoubleGrid2D;
import sim.field.grid.ObjectGrid2D;

public class TrailGridWrapper {

	public DoubleGrid2D grid = null;
	public ObjectGrid2D objectGrid = null;

	public TrailGridWrapper() {
		D.p("created new TrailGridWrapper");
	}

	final public void createNew(int gridWidth, int gridHeight, int startValue) {
		grid = new DoubleGrid2D(gridWidth, gridHeight, startValue);
		objectGrid = new ObjectGrid2D(gridWidth, gridHeight);

	}

	final public void setTrail(int x, int y, double strength) {
		Trail trail = new Trail(x, y);
		grid.field[x][y] = strength;
		objectGrid.field[x][y] = trail;
	}
	
	final public void fadeTrails() {
		grid.lowerBound(0.0);
		grid.multiply(Constants.DEFAULT_TRAIL_EVAPORATION_CONSTANT);
	}
	
	final public void markUsed(int x, int y) {
		Trail trail = (Trail)objectGrid.field[x][y];
		if(trail!=null) {
			trail.markReceived();
		} else {
			D.p("puzzling, should never come here!");
		}
	}

}
