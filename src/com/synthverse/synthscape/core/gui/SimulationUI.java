package com.synthverse.synthscape.core.gui;

import javax.swing.JFrame;

import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.Simulation;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.SparseGridPortrayal2D;

public abstract class SimulationUI extends GUIState implements Constants {

	public long __counter = 0;

	protected Simulation sim;
	protected Display2D display;
	protected JFrame displayFrame;

	protected SparseGridPortrayal2D collectionSitePortrayal;
	protected SparseGridPortrayal2D resourcePortrayal;
	protected SparseGridPortrayal2D obstaclesPortrayal;
	protected SparseGridPortrayal2D trailPortrayal;
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
		if (state instanceof Simulation) {
			setSim((Simulation) state);
		}
	}

	public static String getName() {
		return "Synthscape Simulation";
	}

	public static void main(String[] args) {
		new SimpleSimulationUI().createController();
	}

}
