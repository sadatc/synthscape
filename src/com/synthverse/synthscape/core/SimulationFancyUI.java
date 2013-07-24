/**
 * 
 */
package com.synthverse.synthscape.core;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.portrayal.Inspector;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.gui.SimpleColorMap;

/**
 * @author sadat
 * 
 * 
 */
public class SimulationFancyUI extends GUIState implements Constants {
	protected Simulation sim;
	protected Display2D display;
	protected JFrame displayFrame;

	// these are drawn using images, hence, Value Grid
	// instead of Fast Value Grid
	protected ValueGridPortrayal2D collectionSitePortrayal;
	protected ValueGridPortrayal2D worldPortrayal;
	protected ObjectGridPortrayal2D resourcePortrayal;

	// these are just pixel data
	protected FastValueGridPortrayal2D obstaclesPortrayal;
	protected FastValueGridPortrayal2D trailPortrayal;

	// this one is drawn images, but there can be
	// multiple agents in a location
	protected SparseGridPortrayal2D agentPortrayal;

	private void initStructures() {
		collectionSitePortrayal = new ValueGridPortrayal2D("CollectionSite");
		resourcePortrayal = new ObjectGridPortrayal2D();
		obstaclesPortrayal = new FastValueGridPortrayal2D("Obstacle");
		// above, true = immutable

		agentPortrayal = new SparseGridPortrayal2D();
		trailPortrayal = new FastValueGridPortrayal2D("Trail");
		worldPortrayal = new ValueGridPortrayal2D("World");
	}

	public Simulation getSim() {
		return sim;
	}

	public void setSim(Simulation sim) {
		this.sim = sim;
	}

	public Object getSimulationInspectedObject() {
		return state;
	}

	public Inspector getInspector() {
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
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
	}

	public void quit() {
		super.quit();
		if (displayFrame != null)
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	private void setupPortrayal(FastValueGridPortrayal2D portrayal,
			DoubleGrid2D grid, SimpleColorMap colorMap) {
		portrayal.setField(grid);
		portrayal.setMap(colorMap);

	}

	private void setupPortrayal(FastValueGridPortrayal2D portrayal,
			IntGrid2D grid, SimpleColorMap colorMap) {
		portrayal.setField(grid);
		portrayal.setMap(colorMap);

	}

	public void setupPortrayals() {
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

		setupPortrayal(obstaclesPortrayal, theState.obstacleGrid,
				new sim.util.gui.SimpleColorMap(ABSENT, PRESENT, new Color(0,
						0, 0, 0), Color.BLACK));

		setupPortrayal(trailPortrayal, theState.trailGrid,
				new sim.util.gui.SimpleColorMap(TRAIL_LEVEL_MIN,
						TRAIL_LEVEL_MAX, new Color(255, 255, 255, 0),
						Color.YELLOW) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

		agentPortrayal.setField(theState.agentGrid);
		// agentPortrayal.setPortrayalForClass(Agent.class, new
		// RectanglePortrayal2D(Color.GREEN, true));
		/*
		 * agentPortrayal.setPortrayalForAll(new ImagePortrayal2D(new ImageIcon(
		 * GRID_ICON_AGENT), GRID_ICON_SCALE_FACTOR));
		 */

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

		// this draws the grid lines
		worldPortrayal.setField(theState.obstacleGrid);
		worldPortrayal.setPortrayalForAll(new RectanglePortrayal2D(
				Color.LIGHT_GRAY, false));

		display.reset();
		display.repaint();
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public SimulationFancyUI() {
		super(null);
		initStructures();

	}

	public SimulationFancyUI(SimState state) {
		super(state);
		initStructures();
		this.sim = (Simulation) state;
	}

	public static String getName() {
		return "Synthscape Simulation";
	}

	public static void main(String[] args) {
		new SimulationFancyUI().createController();
	}

}
