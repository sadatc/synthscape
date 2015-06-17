/**
 * 
 */
package com.synthverse.synthscape.core;

import java.awt.Color;

import javax.swing.ImageIcon;

import sim.display.Controller;
import sim.display.Display2D;
import sim.engine.SimState;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

public class FancySimulationUI extends SimulationUI {

	public FancySimulationUI() {
		super();
	}

	public FancySimulationUI(SimState state) {
		super(state);
	}

	protected ValueGridPortrayal2D worldPortrayal;

	protected void initStructures() {
		collectionSitePortrayal = new ValueGridPortrayal2D("CollectionSite");
		resourcePortrayal = new ObjectGridPortrayal2D();
		obstaclesPortrayal = new FastValueGridPortrayal2D("Obstacle");
		// above, true = immutable

		agentPortrayal = new SparseGridPortrayal2D();
		trailPortrayal = new FastValueGridPortrayal2D("Trail");
		worldPortrayal = new ValueGridPortrayal2D("World");
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

		display.attach(obstaclesPortrayal, "Obstacles");
		display.attach(collectionSitePortrayal, "Collection Sites");
		display.attach(resourcePortrayal, "Resources");
		display.attach(trailPortrayal, "Trails");
		display.attach(agentPortrayal, "Agents");
		// display.detatchAll();
	}

	public void initPortrayals() {
		Simulation theState = (Simulation) state;

		// image based portrayal
		collectionSitePortrayal.setField(theState.collectionSiteGrid);

		collectionSitePortrayal.setPortrayalForAll(new FacetedPortrayal2D(
				new SimplePortrayal2D[] {
						new RectanglePortrayal2D(Color.TRANSLUCENT, false),
						new ImagePortrayal2D(new ImageIcon(
								GRID_ICON_COLLECTION_SITE),
								GRID_ICON_SCALE_FACTOR) }));

		// image based portrayal
		resourcePortrayal.setField(theState.resourceGrid);
		resourcePortrayal
				.setPortrayalForAll(new FacetedPortrayal2D(
						new SimplePortrayal2D[] {
								new RectanglePortrayal2D(Color.TRANSLUCENT,
										false),
								new ImagePortrayal2D(new ImageIcon(
										GRID_ICON_RAW_RESOURCE),
										GRID_ICON_SCALE_FACTOR),
								new ImagePortrayal2D(new ImageIcon(
										GRID_ICON_EXTRACTED_RESOURCE),
										GRID_ICON_SCALE_FACTOR),
								new ImagePortrayal2D(new ImageIcon(
										GRID_ICON_PROCESSED_RESOURCE),
										GRID_ICON_SCALE_FACTOR)

						}

				));

		/*
		initPortrayal(obstaclesPortrayal, theState.obstacleGrid,
				new sim.util.gui.SimpleColorMap(NO, YES, new Color(0,
						0, 0, 0), Color.BLACK));

		initPortrayal(trailPortrayal, theState.trailGridWrapper.strengthGrid,
				new sim.util.gui.SimpleColorMap(TRAIL_LEVEL_MIN,
						TRAIL_LEVEL_MAX, new Color(255, 255, 255, 0),
						Color.YELLOW) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

*/
		agentPortrayal.setField(theState.agentGrid);

		agentPortrayal
				.setPortrayalForAll(new FacetedPortrayal2D(
						new SimplePortrayal2D[] {

								new ImagePortrayal2D(new ImageIcon(
										GRID_ICON_AGENT),
										GRID_ICON_SCALE_FACTOR),
								new ImagePortrayal2D(new ImageIcon(
										GRID_ICON_LOADED_AGENT),
										GRID_ICON_SCALE_FACTOR)

						}));

		// this draws the strengthGrid lines
		worldPortrayal.setField(theState.obstacleGrid);
		worldPortrayal.setPortrayalForAll(new RectanglePortrayal2D(
				Color.LIGHT_GRAY, false));

		display.reset();
		display.repaint();
	}

	public static void main(String[] args) {
		new FancySimulationUI().createController();
	}

}
