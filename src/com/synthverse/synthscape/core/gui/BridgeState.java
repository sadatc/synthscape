package com.synthverse.synthscape.core.gui;

import com.synthverse.Main;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;

@SuppressWarnings("serial")
public class BridgeState extends SimState {

	public SparseGrid2D agentGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);
	public SparseGrid2D obstacleGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);
	public SparseGrid2D benchmarkObstacleGrid =new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);

	public SparseGrid2D collectionSiteGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);
	public SparseGrid2D benchmarkCollectionSiteGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);

	protected SparseGrid2D initCollisionGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);
	protected SparseGrid2D benchmarkInitCollisionGrid =new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);

	public SparseGrid2D resourceGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);
	
	public SparseGrid2D trailGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH,Main.settings.WORLD_HEIGHT);

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
