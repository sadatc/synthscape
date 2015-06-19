package com.synthverse.util;

import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.MutableDouble;

import com.synthverse.synthscape.core.ResourceState;

public class GridUtils {

	static class MutableInt implements Cloneable {
		private static final long serialVersionUID = 1;

		public int val;

		public MutableInt() {
			val = 0;
		}

		public MutableInt(int val) {
			this.val = val;
		}

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null; // never happens
			}
		}
	}

	static class MutableLong implements Cloneable {
		private static final long serialVersionUID = 1;

		public long val;

		public MutableLong() {
			val = 0;
		}

		public MutableLong(long val) {
			this.val = val;
		}

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null; // never happens
			}
		}
	}

	static class MutableBoolean implements Cloneable {
		private static final long serialVersionUID = 1;

		public boolean val;

		public MutableBoolean() {
			val = false;
		}

		public MutableBoolean(boolean val) {
			this.val = val;
		}

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null; // never happens
			}
		}
	}

	static class MutableResourceState implements Cloneable {
		private static final long serialVersionUID = 1;

		public ResourceState val;

		public MutableResourceState() {
			val = ResourceState.NULL;
		}

		public MutableResourceState(ResourceState val) {
			this.val = val;
		}

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null; // never happens
			}
		}
	}

	// assumes only one value per location
	public static void set(SparseGrid2D grid, int x, int y, double val) {
		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableDouble mutableObject = (MutableDouble) objects.get(0);
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		} else {
			MutableDouble mutableObject = new MutableDouble();
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		}
		
	}

	public static void set(SparseGrid2D grid, int x, int y, int val) {
		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableInt mutableObject = (MutableInt) objects.get(0);
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		} else {
			MutableInt mutableObject = new MutableInt();
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		}
	}

	public static void set(SparseGrid2D grid, int x, int y, long val) {
		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableLong mutableObject = (MutableLong) objects.get(0);
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		} else {
			MutableLong mutableObject = new MutableLong();
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		}
	}

	public static void set(SparseGrid2D grid, int x, int y, ResourceState val) {
		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableResourceState mutableObject = (MutableResourceState) objects
					.get(0);
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		} else {
			MutableResourceState mutableObject = new MutableResourceState();
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		}
	}

	public static void set(SparseGrid2D grid, int x, int y, boolean val) {
		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableBoolean mutableObject = (MutableBoolean) objects.get(0);
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		} else {
			MutableBoolean mutableObject = new MutableBoolean();
			mutableObject.val = val;
			grid.setObjectLocation(mutableObject, x, y);
		}
	}

	public static double getDouble(SparseGrid2D grid, int x, int y) {
		double result = 0.0;

		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableDouble mutableObject = (MutableDouble) objects.get(0);
			result = mutableObject.val;
		}

		return result;
	}

	public static int getInt(SparseGrid2D grid, int x, int y) {
		int result = 0;

		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableInt mutableObject = (MutableInt) objects.get(0);
			result = mutableObject.val;
		}

		return result;
	}

	public static long getLong(SparseGrid2D grid, int x, int y) {
		long result = 0;

		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableLong mutableObject = (MutableLong) objects.get(0);
			result = mutableObject.val;
		}

		return result;
	}

	public static boolean getBoolean(SparseGrid2D grid, int x, int y) {
		boolean result = false;

		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableBoolean mutableObject = (MutableBoolean) objects.get(0);
			result = mutableObject.val;
		}

		return result;
	}

	public static ResourceState getResourceState(SparseGrid2D grid, int x, int y) {
		ResourceState result = ResourceState.NULL;

		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			MutableResourceState mutableObject = (MutableResourceState) objects
					.get(0);
			result = mutableObject.val;
		}

		return result;
	}

	public static boolean gridHasAnObjectAt(SparseGrid2D grid, int x, int y) {
		boolean result = false;

		Bag objects = grid.getObjectsAtLocation(x, y);
		if (objects != null) {
			result = true;
		}

		return result;
	}

}
