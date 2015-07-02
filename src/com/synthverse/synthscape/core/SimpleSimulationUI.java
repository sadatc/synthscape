/**
 * 
 */
package com.synthverse.synthscape.core;

import java.awt.Color;

import sim.display.Controller;
import sim.display.Display2D;
import sim.engine.SimState;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.portrayal.simple.ShapePortrayal2D;

public class SimpleSimulationUI extends SimulationUI {

	public SimpleSimulationUI() {
		super();
	}

	public SimpleSimulationUI(SimState state) {
		super(state);
	}

	public void init(Controller controller) {

		super.init(controller);

		display = new Display2D(300, 300, this);
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

	protected void initStructures() {
		collectionSitePortrayal = new SparseGridPortrayal2D();
		resourcePortrayal = new SparseGridPortrayal2D();
		obstaclesPortrayal = new SparseGridPortrayal2D();
		agentPortrayal = new SparseGridPortrayal2D();
		trailPortrayal = new SparseGridPortrayal2D();
	}

	protected void initPortrayals() {
		Simulation theState = (Simulation) state;
	

		/*
		 * 
		
			initPortrayal(obstaclesPortrayal, theState.obstacleGrid,
				new sim.util.gui.SimpleColorMap(ABSENT, PRESENT, new Color(0,
						0, 0, 0), Color.BLACK));

		
		initPortrayal(trailPortrayal, theState.trailGridWrapper.grid,
				new sim.util.gui.SimpleColorMap(TRAIL_LEVEL_MIN,
						TRAIL_LEVEL_MAX, new Color(255, 255, 255, 0),
						Color.YELLOW) {
					public double filterLevel(double level) {
						return Math.sqrt(Math.sqrt(level));
					}

				});

	
	
		initPortrayal(collectionSitePortrayal, theState.collectionSiteGrid,
				new sim.util.gui.SimpleColorMap(ABSENT, PRESENT, new Color(0,
						0, 0, 0), Color.GREEN));

		resourcePortrayal.setField(theState.resourceGrid);
		resourcePortrayal.setPortrayalForAll(new FacetedPortrayal2D(
				new SimplePortrayal2D[] {
						new RectanglePortrayal2D(Color.TRANSLUCENT, false),
						new OvalPortrayal2D(Color.YELLOW, true),
						new OvalPortrayal2D(Color.PINK, true),
						new OvalPortrayal2D(Color.RED, true)

				}

		));

*/
		agentPortrayal.setField(theState.agentGrid);
		// agentPortrayal.setPortrayalForAll(new OvalPortrayal2D(Color.BLACK,
		// true));
		agentPortrayal.setPortrayalForAll(new ShapePortrayal2D(
				ShapePortrayal2D.X_POINTS_TRIANGLE_UP,
				ShapePortrayal2D.Y_POINTS_TRIANGLE_UP, Color.BLACK));
		
		display.reset();
		display.repaint();
	}

}
