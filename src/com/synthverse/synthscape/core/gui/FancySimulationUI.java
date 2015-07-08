/**
 * 
 */
package com.synthverse.synthscape.core.gui;

import java.awt.Color;

import javax.swing.ImageIcon;

import com.synthverse.Main;

import sim.display.Controller;
import sim.display.Display2D;
import sim.field.grid.IntGrid2D;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

public class FancySimulationUI extends SimulationUI {

	public FancySimulationUI() {
		super();

	}

	public FancySimulationUI(BridgeState state) {
		super(state);
	}

	BridgeState theState;
	SparseGridPortrayal2D collectionSitePortrayal;
	SparseGridPortrayal2D resourcePortrayal;
	SparseGridPortrayal2D obstaclesPortrayal;
	SparseGridPortrayal2D trailPortrayal;
	SparseGridPortrayal2D agentPortrayal;

	protected ValueGridPortrayal2D worldPortrayal;

	protected void initStructures() {
		collectionSitePortrayal = new SparseGridPortrayal2D();
		resourcePortrayal = new SparseGridPortrayal2D();
		obstaclesPortrayal = new SparseGridPortrayal2D();
		agentPortrayal = new SparseGridPortrayal2D();
		trailPortrayal = new SparseGridPortrayal2D();
		worldPortrayal = new ValueGridPortrayal2D();
	}

	public void init(Controller controller) {

		super.init(controller);

		display = new Display2D(600, 600, this);
		display.setScale(1.0);
		display.setClipping(false);

		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);
		displayFrame.setVisible(true);

		// attach all the portrayals
		display.attach(worldPortrayal, "World");

		display.attach(obstaclesPortrayal, "Obstacles");
		display.attach(collectionSitePortrayal, "Collection Sites");
		display.attach(resourcePortrayal, "Resources");
		display.attach(trailPortrayal, "Trails");
		display.attach(agentPortrayal, "Agents");

	}

	
	public void initPortrayals() {
		BridgeState theState = (BridgeState) state;

		// collection sites
		collectionSitePortrayal.setField(theState.collectionSiteGrid);
		collectionSitePortrayal.setPortrayalForAll(
				new ImagePortrayal2D(new ImageIcon(GRID_ICON_COLLECTION_SITE), GRID_ICON_SCALE_FACTOR));

		// resources -- they can be in different states
		resourcePortrayal.setField(theState.resourceGrid);
		resourcePortrayal.setPortrayalForAll(
				new FacetedPortrayal2D(new SimplePortrayal2D[]{new RectanglePortrayal2D(Color.TRANSLUCENT, true),
						new ImagePortrayal2D(new ImageIcon(GRID_ICON_RAW_RESOURCE), GRID_ICON_SCALE_FACTOR),
						new ImagePortrayal2D(new ImageIcon(GRID_ICON_EXTRACTED_RESOURCE), GRID_ICON_SCALE_FACTOR),
						new ImagePortrayal2D(new ImageIcon(GRID_ICON_PROCESSED_RESOURCE), GRID_ICON_SCALE_FACTOR)

		}

		));

		// obstacles sites
		obstaclesPortrayal.setField(theState.obstacleGrid);
		obstaclesPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.BLACK, true));

		// trails
		trailPortrayal.setField(theState.trailGrid);
		trailPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.YELLOW, 0.65, true));

		/*
		 * trailPortrayal.setMap(new
		 * sim.util.gui.SimpleColorMap(TRAIL_LEVEL_MIN, TRAIL_LEVEL_MAX, new
		 * Color(255, 255, 255, 0), Color.YELLOW) { public double
		 * filterLevel(double level) { return Math.sqrt(Math.sqrt(level)); } });
		 */

		/*
		 * initPortrayal(obstaclesPortrayal, theState.obstacleGrid, new
		 * sim.util.gui.SimpleColorMap(ABSENT, PRESENT, new Color(0, 0, 0, 0),
		 * Color.BLACK));
		 * 
		 * initPortrayal(trailPortrayal, theState.trailGridWrapper.grid, }
		 * 
		 * });
		 */
		agentPortrayal.setField(theState.agentGrid);

		agentPortrayal
				.setPortrayalForAll(new FacetedPortrayal2D(new SimplePortrayal2D[]{

				new ImagePortrayal2D(new ImageIcon(GRID_ICON_AGENT), GRID_ICON_SCALE_FACTOR),
						new ImagePortrayal2D(new ImageIcon(GRID_ICON_LOADED_AGENT), GRID_ICON_SCALE_FACTOR)

		}));

		// this draws the grid lines
		IntGrid2D worldGrid = new IntGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT, 1);
		worldPortrayal.setField(worldGrid);
		worldPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.LIGHT_GRAY, 0.99, false));

		display.reset();
		display.repaint();
	}

	public static void main(Thread coreSimThread, String[] args) {
		BridgeState bridgeState = new BridgeState(1);
		Main.settings.__showGraphics = true;
		FancySimulationUI simUI = new FancySimulationUI(bridgeState);

		simUI.createController();
		coreSimThread.start();
	}

}
