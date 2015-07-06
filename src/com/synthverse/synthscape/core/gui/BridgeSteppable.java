package com.synthverse.synthscape.core.gui;

import sim.engine.SimState;
import sim.engine.Steppable;

@SuppressWarnings("serial")
public class BridgeSteppable implements Steppable {

	@Override
	public void step(SimState state) {
		BridgeState bridgeState = (BridgeState) state;
		System.out.println("++++");
		
	}

}
