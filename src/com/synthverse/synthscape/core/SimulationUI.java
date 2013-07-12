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
 * 
 */
public class SimulationUI extends GUIState {

	protected Class agentClass;

	protected Simulation sim;

	protected Display2D display;

	protected JFrame displayFrame;

	protected FastValueGridPortrayal2D collectionSitePortrayal;

	protected FastValueGridPortrayal2D resourcePortrayal;

	protected FastValueGridPortrayal2D extractedResourcePortrayal;
	
	protected FastValueGridPortrayal2D processedResourcePortrayal;

	protected FastValueGridPortrayal2D obstaclesPortrayal;

	protected SparseGridPortrayal2D agentPortrayal;

	protected FastValueGridPortrayal2D trailPortrayal;

	private void initStructures() {
		collectionSitePortrayal = new FastValueGridPortrayal2D(
				"CollectionSite", true);

		resourcePortrayal = new FastValueGridPortrayal2D("Resource", false);

		extractedResourcePortrayal = new FastValueGridPortrayal2D(
				"ExtractedResource", false);

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

	public Class getAgentClass() {
		return agentClass;
	}

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
		display.attach(collectionSitePortrayal, "Collection Sites");
		display.attach(resourcePortrayal, "Resources");
		display.attach(extractedResourcePortrayal, "Extracts");
		display.attach(processedResourcePortrayal, "Products");
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
		// tell the portrayals what to portray and how to portray them

		setupPortrayal(trailPortrayal, theState.trailAGrid,
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

		setupPortrayal(collectionSitePortrayal, theState.collectionSiteGrid,
				new sim.util.gui.SimpleColorMap(sim.HOME_FALSE, sim.HOME_TRUE,
						new Color(0, 0, 0, 0), Color.GREEN));

		setupPortrayal(extractedResourcePortrayal, theState.extractAGrid,
				new sim.util.gui.SimpleColorMap(0, 1, new Color(0, 0, 0, 0),
						Color.PINK));

		setupPortrayal(resourceAPortrayal, theState.resourceAGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.PRESENT, new Color(0, 0, 0, 0), Color.BLUE));

		/*
		 * setupPortrayal(resourceAPortrayal, theState.resourceAGrid, new
		 * sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY, sim.RESOURCE_MAX, new
		 * Color(0, 0, 0, 0), Color.RED));
		 */

		setupPortrayal(resourceAPortrayal, theState.resourceAGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.PRESENT, new Color(0, 0, 0, 0), Color.BLUE));

		setupPortrayal(resourceBPortrayal, theState.resourceBGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.PRESENT, new Color(0, 0, 0, 0), Color.BLUE));

		setupPortrayal(resourceCPortrayal, theState.resourceCGrid,
				new sim.util.gui.SimpleColorMap(sim.RESOURCE_EMPTY,
						sim.PRESENT, new Color(0, 0, 0, 0), Color.BLUE));

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
