/**
 * 
 */
package com.synthverse.synthscape.core;

import java.awt.Color;

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
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;

/**
 * @author sadat
 * 
 *         color codes:
 * 
 *         resources: red, green, blue extracts: hollow? red, green, blue?
 *         trails: lighter shades of all the above home: triangle/image
 * 
 * 
 */
public class SimulationUI extends GUIState {

	private Class agentClass;

	private Simulation sim;

	public Simulation getSim() {
		return sim;
	}

	public void setSim(Simulation sim) {
		this.sim = sim;
	}

	public Class getAgentClass() {
		return agentClass;
	}

	/*
	 * public void setAgentClass(Class agentClass) { this.agentClass =
	 * agentClass; }
	 */
	// main 2D display object
	public Display2D display;

	public JFrame displayFrame;

	FastValueGridPortrayal2D homePortrayal = new FastValueGridPortrayal2D(
			"Home", true);

	FastValueGridPortrayal2D resourceAPortrayal = new FastValueGridPortrayal2D(
			"ResourceA", false);

	FastValueGridPortrayal2D resourceBPortrayal = new FastValueGridPortrayal2D(
			"ResourceB", false);

	FastValueGridPortrayal2D resourceCPortrayal = new FastValueGridPortrayal2D(
			"ResourceC", false);

	FastValueGridPortrayal2D extractAPortrayal = new FastValueGridPortrayal2D(
			"ExtractA", false);

	FastValueGridPortrayal2D extractBPortrayal = new FastValueGridPortrayal2D(
			"ExtractB", false);

	FastValueGridPortrayal2D extractCPortrayal = new FastValueGridPortrayal2D(
			"ExtractC", false);

	FastValueGridPortrayal2D obstaclesPortrayal = new FastValueGridPortrayal2D(
			"Obstacle", true);

	SparseGridPortrayal2D agentPortrayal = new SparseGridPortrayal2D();

	FastValueGridPortrayal2D trailAPortrayal = new FastValueGridPortrayal2D(
			"TrailA");

	FastValueGridPortrayal2D trailBPortrayal = new FastValueGridPortrayal2D(
			"TrailB");

	FastValueGridPortrayal2D trailCPortrayal = new FastValueGridPortrayal2D(
			"TrailC");

	public Object getSimulationInspectedObject() {
		return state;
	}

	public Inspector getInspector() {
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}

	public void init(Controller c) {
		super.init(c);

		display = new Display2D(300, 300, this);

		displayFrame = display.createFrame();

		c.registerFrame(displayFrame);

		displayFrame.setVisible(true);

		// attach the portrayals from bottom to top
		display.attach(homePortrayal, "Home Locations");
		display.attach(resourceAPortrayal, "Resource A Locations");
		display.attach(resourceBPortrayal, "Resource B Locations");
		display.attach(resourceCPortrayal, "Resource C Locations");
		display.attach(extractAPortrayal, "Extract A Locations");
		display.attach(extractBPortrayal, "Extract B Locations");
		display.attach(extractCPortrayal, "Extract C Locations");
		display.attach(trailAPortrayal, "TrailA");
		display.attach(trailBPortrayal, "TrailB");
		display.attach(trailCPortrayal, "TrailC");

		display.attach(obstaclesPortrayal, "Obstacle Locations");
		display.attach(agentPortrayal, "Agent Locations");

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
		// tell the portrayals what to portray and how to portray them

		setupPortrayal(trailAPortrayal, theState.trailAGrid,
				new sim.util.gui.SimpleColorMap(sim.TRAIL_LEVEL_MIN,
						sim.TRAIL_LEVEL_MAX, new Color(255, 255, 255, 0),
						Color.YELLOW) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

		setupPortrayal(trailBPortrayal, theState.trailBGrid,
				new sim.util.gui.SimpleColorMap(sim.TRAIL_LEVEL_MIN,
						sim.TRAIL_LEVEL_MAX, new Color(0, 0, 0, 0),
						Color.ORANGE) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

		setupPortrayal(
				trailCPortrayal,
				theState.trailCGrid,
				new sim.util.gui.SimpleColorMap(sim.TRAIL_LEVEL_MIN,
						sim.TRAIL_LEVEL_MAX, new Color(0, 0, 0, 0), Color.PINK) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

		setupPortrayal(obstaclesPortrayal, theState.obstacleGrid,
				new sim.util.gui.SimpleColorMap(sim.OBSTACLE_FALSE,
						sim.OBSTACLE_TRUE, new Color(0, 0, 0, 0), Color.BLACK));

		setupPortrayal(homePortrayal, theState.homeGrid,
				new sim.util.gui.SimpleColorMap(sim.HOME_FALSE, sim.HOME_TRUE,
						new Color(0, 0, 0, 0), Color.GREEN));

		setupPortrayal(extractAPortrayal, theState.extractAGrid,
				new sim.util.gui.SimpleColorMap(0, 1, new Color(0, 0, 0, 0),
						Color.PINK));

		setupPortrayal(resourceAPortrayal, theState.resourceAGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.RESOURCE_MAX, new Color(0, 0, 0, 0), Color.BLUE));

		/*
		 * setupPortrayal(resourceAPortrayal, theState.resourceAGrid, new
		 * sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY, sim.RESOURCE_MAX, new
		 * Color(0, 0, 0, 0), Color.RED));
		 */

		setupPortrayal(resourceAPortrayal, theState.resourceAGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.RESOURCE_MAX, new Color(0, 0, 0, 0), Color.BLUE));

		setupPortrayal(resourceBPortrayal, theState.resourceBGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.RESOURCE_MAX, new Color(0, 0, 0, 0), Color.BLUE));

		setupPortrayal(resourceCPortrayal, theState.resourceCGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.RESOURCE_MAX, new Color(0, 0, 0, 0), Color.BLUE));

		agentPortrayal.setField(theState.agentGrid);

		
		agentPortrayal.setPortrayalForClass(getAgentClass(),
				new FacetedPortrayal2D(new SimplePortrayal2D[] {
						new OvalPortrayal2D(Color.PINK, 1.5, false),
						new OvalPortrayal2D(Color.PINK, 1.5, true),
						new OvalPortrayal2D(Color.PINK, 1) }));

		/*
		 * agentPortrayal.setPortrayalForClass(Agent.class, new
		 * FacetedPortrayal2D(new SimplePortrayal2D[] { new
		 * ImagePortrayal2D(this.getClass(), "/tmp/blinkyu.png", 2), new
		 * ImagePortrayal2D(this.getClass(), "/tmp/blinkyu.png", 2), new
		 * ImagePortrayal2D(this.getClass(), "/tmp/blinkyl.png", 2), }));
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

	}

	public SimulationUI(SimState state) {
		super(state);
		this.sim = (Simulation) state;
	}

	public static String getName() {
		return "Synthscape Simulation";
	}

}
