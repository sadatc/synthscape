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

	
	protected Display2D display;
	protected JFrame displayFrame;

	
	protected abstract void initStructures();

	
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

		initPortrayals();
	}

	public SimulationUI() {
		super(null);
		initStructures();
	}

	public SimulationUI(SimState state) {
		super(state);
		initStructures();

	}

	public static String getName() {
		return "Synthscape Simulation";
	}

	
}
