package com.synthverse.synthscape.core.gui;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;

@SuppressWarnings("serial")
public class BridgeState extends SimState {

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

	public BridgeState(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		super.start();
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {

			@Override
			public void step(SimState state) {
				System.out.println("I am here");

			}

		});
	}

}
