package com.synthverse.synthscape.core.gui;

import com.synthverse.Main;
import com.synthverse.synthscape.core.D;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;

/**
 * Class that is used by the renderer. The state is updated by the core
 * simulator
 * 
 * @author sadat
 *
 */
@SuppressWarnings("serial")
public class BridgeState extends SimState {

	long stepCounter = 0;

	public SparseGrid2D agentGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT);
	public SparseGrid2D obstacleGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT);
	public SparseGrid2D collectionSiteGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT);
	public SparseGrid2D resourceGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT);
	public SparseGrid2D trailGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT);
	public SparseGrid2D collectedResourceLocationGrid = new SparseGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT);

	public BridgeState(long seed) {
		super(seed);
		Main.settings.__bridgeState = this;
	}

	@Override
	public void start() {
		super.start();
		Main.settings.__guiStarted = true;
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {

			@Override
			public void step(SimState state) {
			    stepCounter++;

			    	// wait for model to do it's update
				while (Main.settings.__renderStageLock != 1) {
					Thread.yield();
				}
				
				try {
					Thread.sleep(Main.settings.__animationDelay);
					if(Main.settings.__animationDelay>5) {
					    Main.settings.__animationDelay=5; 
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// this tells the model to keep updating
				Main.settings.__renderStageLock = 2;

			}

		});
	}

}
