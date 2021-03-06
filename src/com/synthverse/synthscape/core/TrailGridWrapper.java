package com.synthverse.synthscape.core;

import com.synthverse.util.GridUtils;

import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.MutableDouble;

public class TrailGridWrapper {

	public SparseGrid2D strengthGrid = null;
	public SparseGrid2D objectGrid = null;

	public TrailGridWrapper() {
		//D.p("created new TrailGridWrapper");
	}

	final public void createNew(int gridWidth, int gridHeight) {
		strengthGrid = new SparseGrid2D(gridWidth, gridHeight);
		objectGrid = new SparseGrid2D(gridWidth, gridHeight);
	}

	final public void setTrail(int x, int y, double strengthValue) {
		Trail trail = new Trail(x, y);
		GridUtils.set(strengthGrid, x, y, strengthValue);
		objectGrid.removeObjectsAtLocation(x, y);
		objectGrid.setObjectLocation(trail, x, y);

	}

	final public void markUsed(int x, int y) {
		Bag objects = objectGrid.getObjectsAtLocation(x, y);
		if (objects != null) {
			Trail trail = (Trail) objects.get(0);
			if (trail != null) {
				trail.markReceived();
			} else {
				D.p("puzzling, should never come here!");
			}
		}

	}

	final double getTrailStrengthAt(int x, int y) {
		return GridUtils.getDouble(strengthGrid, x, y);
	}

	final public void sparseGrid2DMultiply(SparseGrid2D mutableDoubleSparseGrid, double multiplier) {
		Bag objects = mutableDoubleSparseGrid.getAllObjects();
		int size = objects.size();
		for (int i = 0; i < size; i++) {
			MutableDouble d = (MutableDouble) (objects.get(i));
			d.val *= multiplier;
			if (d.val < 1.0) {
				d.val = 0.0;
			}
		}
	}

	final public void cleanupZeros(SparseGrid2D mutableDoubleSparseGrid) {
		Bag objects = mutableDoubleSparseGrid.getAllObjects();
		Bag removableObjects = new Bag();

		for (int i = 0; i < objects.size(); i++) {
			MutableDouble d = (MutableDouble) (objects.get(i));
			if (d.val <= 0.0) {
				removableObjects.add(d);
			}
		}

		for (int i = 0; i < removableObjects.size(); i++) {
			mutableDoubleSparseGrid.remove(removableObjects.get(i));
		}
	}

	final public void cleanupNearZeros(SparseGrid2D mutableDoubleSparseGrid) {
		Bag objects = mutableDoubleSparseGrid.getAllObjects();
		Bag removableObjects = new Bag();

		for (int i = 0; i < objects.size(); i++) {
			MutableDouble d = (MutableDouble) (objects.get(i));
			if (d.val <= Constants.TRAIL_LEVEL_MIN) {
				removableObjects.add(d);
			}
		}

		for (int i = 0; i < removableObjects.size(); i++) {
			mutableDoubleSparseGrid.remove(removableObjects.get(i));
		}
	}

	final public String debug() {
		String result = null;
		Bag objects = strengthGrid.getAllObjects();

		String s = "[";
		for (int i = 0; i < objects.size(); i++) {
			MutableDouble d = (MutableDouble) (objects.get(i));
			s += d.val + " ";
		}
		s += "]";
		if (objects.size() > 0) {
			result = s;
		}

		return result;

	}

	final public void fadeTrails() {
		sparseGrid2DMultiply(strengthGrid, Constants.DEFAULT_TRAIL_EVAPORATION_CONSTANT);
		cleanupNearZeros(strengthGrid);
		// FIXME: remove values that are 0s
	}

	final public void clear() {
		strengthGrid.clear();
		objectGrid.clear();
	}

}
