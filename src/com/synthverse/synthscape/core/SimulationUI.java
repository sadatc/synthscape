/**
 * 
 */
package com.synthverse.synthscape.core;

import java.awt.Color;

import javax.swing.JFrame;

import sim.app.antsforage.AntsForageWithUI;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.portrayal.Inspector;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;

/**
 * @author sadat
 * 
 * 
 */
public class SimulationUI extends GUIState implements Constants {
	protected Simulation sim;
	protected Display2D display;
	protected JFrame displayFrame;

	protected FastValueGridPortrayal2D collectionSitePortrayal;
	protected FastValueGridPortrayal2D resourcePortrayal;
	protected FastValueGridPortrayal2D obstaclesPortrayal;
	protected FastValueGridPortrayal2D trailPortrayal;

	protected SparseGridPortrayal2D agentPortrayal;

	private void initStructures() {
		collectionSitePortrayal = new FastValueGridPortrayal2D(
				"CollectionSite", true);

		resourcePortrayal = new FastValueGridPortrayal2D("Resource", false);

		obstaclesPortrayal = new FastValueGridPortrayal2D("Obstacle", true);

		agentPortrayal = new SparseGridPortrayal2D();

		trailPortrayal = new FastValueGridPortrayal2D("Trail");
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
		display.setClipping(false);

		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);
		displayFrame.setVisible(true);

		// attach the portrayals from bottom to top
		display.attach(collectionSitePortrayal, "Collection Sites");
		display.attach(resourcePortrayal, "Resources");
		display.attach(trailPortrayal, "Trails");
		display.attach(obstaclesPortrayal, "Obstacles");
		display.attach(agentPortrayal, "Agents");

		// specify the backdrop color -- what gets painted behind the displays
		display.setBackdrop(Color.WHITE);
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

		setupPortrayal(trailPortrayal, theState.trailGrid,
				new sim.util.gui.SimpleColorMap(TRAIL_LEVEL_MIN,
						TRAIL_LEVEL_MAX, new Color(255, 255, 255, 0),
						Color.YELLOW) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

		setupPortrayal(obstaclesPortrayal, theState.obstacleGrid,
				new sim.util.gui.SimpleColorMap(ABSENT, PRESENT, new Color(0,
						0, 0, 0), Color.BLACK));

		setupPortrayal(collectionSitePortrayal, theState.collectionSiteGrid,
				new sim.util.gui.SimpleColorMap(ABSENT, PRESENT, new Color(0,
						0, 0, 0), Color.GREEN));

		setupPortrayal(resourcePortrayal, theState.resourceGrid,
				new sim.util.gui.SimpleColorMap(ResourceState.NULL.ordinal(),
						ResourceState.PROCESSED.ordinal(),
						new Color(0, 0, 0, 0), Color.BLUE));

		agentPortrayal.setField(theState.agentGrid);

		/*
		 * agentPortrayal.setPortrayalForClass(Agent.class, new
		 * FacetedPortrayal2D(new SimplePortrayal2D[] { new
		 * OvalPortrayal2D(Color.PINK, 1.5, false), new
		 * OvalPortrayal2D(Color.PINK, 1.5, true), new
		 * OvalPortrayal2D(Color.PINK, 1) }));
		 */
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

	public SimulationUI() {
		super(null);
		initStructures();

	}

	public SimulationUI(SimState state) {
		super(state);
		initStructures();
		this.sim = (Simulation) state;
	}

	public static String getName() {
		return "Synthscape Simulation";
	}

	public static void main(String[] args) {
		new SimulationUI().createController();
	}

}
