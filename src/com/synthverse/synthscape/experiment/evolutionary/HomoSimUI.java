/**
 * 
 */
package com.synthverse.synthscape.experiment.evolutionary;

import com.synthverse.synthscape.core.SimulationUI;

import sim.display.Console;
import sim.engine.SimState;

/**
 * @author sadat
 * 
 */
public class HomoSimUI extends SimulationUI {

	public HomoSimUI() {
		super(new HomoSim(2));
		
	}
	
	public HomoSimUI(SimState state) {
		super(state);
		
		
	}
	
	public static void main(String[] args) {
		Console c = new Console(new HomoSimUI());
		c.setVisible(true);
	}
}
