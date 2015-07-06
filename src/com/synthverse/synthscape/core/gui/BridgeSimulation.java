package com.synthverse.synthscape.core.gui;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

@SuppressWarnings("serial")
public class BridgeSimulation extends SimState {
	


	public SparseGrid2D agentGrid;
	public SparseGrid2D obstacleGrid;
	public SparseGrid2D benchmarkObstacleGrid;

	public SparseGrid2D collectionSiteGrid;
	public SparseGrid2D benchmarkCollectionSiteGrid;


	protected SparseGrid2D initCollisionGrid;
	protected SparseGrid2D benchmarkInitCollisionGrid;

	public SparseGrid2D resourceGrid;
	public SparseGrid2D benchmarkResourceGrid;
	public SparseGrid2D trailGrid;	

	public BridgeSimulation(long seed) {
		super(seed);
	}

}
