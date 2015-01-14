package com.synthverse.synthscape.core;

import javax.swing.JFrame;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.util.gui.SimpleColorMap;

public abstract class SimulationUI extends GUIState implements Constants {
	protected Simulation sim;
	protected Display2D display;
	protected JFrame displayFrame;

	protected ValueGridPortrayal2D collectionSitePortrayal;
	protected ObjectGridPortrayal2D resourcePortrayal;
	protected ValueGridPortrayal2D obstaclesPortrayal;
	protected ValueGridPortrayal2D trailPortrayal;

	protected SparseGridPortrayal2D agentPortrayal;

	protected abstract void initStructures();

	public Simulation getSim() {
		return sim;
	}

	public void setSim(Simulation sim) {
		this.sim = sim;
		sim.setUiObject(this);
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
	}

	public void quit() {
		super.quit();
		if (displayFrame != null)
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	protected void initPortrayal(ValueGridPortrayal2D portrayal,
			IntGrid2D grid, SimpleColorMap colorMap) {

		portrayal.setField(grid);
		portrayal.setMap(colorMap);
	}

	protected void initPortrayal(ValueGridPortrayal2D portrayal,
			DoubleGrid2D grid, SimpleColorMap colorMap) {
		portrayal.setField(grid);
		portrayal.setMap(colorMap);

	}

	protected abstract void initPortrayals();

	public void start() {
		super.start();
		initPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setSim((Simulation) state);
		initPortrayals();
	}

	public SimulationUI() {
		super(null);
		initStructures();
	}

	public SimulationUI(SimState state) {
		super(state);
		initStructures();
		setSim((Simulation) state);
	}

	public static String getName() {
		return "Synthscape Simulation";
	}

	public static void main(String[] args) {
		new SimpleSimulationUI().createController();
	}

}
