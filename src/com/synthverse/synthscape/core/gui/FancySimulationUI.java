/**
 * 
 */
package com.synthverse.synthscape.core.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import com.synthverse.Main;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.InteractionMode;
import com.synthverse.util.SoundEffect;

import sim.display.Controller;
import sim.display.Display2D;
import sim.field.grid.IntGrid2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.SimplePortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.portrayal.simple.FacetedPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.MutableDouble;
import sim.util.gui.SimpleColorMap;

public class FancySimulationUI extends SimulationUI {

    Thread coreSimThread = null;
    boolean simThreadStarted = false;
    Object simLock = new Object();

    public FancySimulationUI() {
	super();
	SoundEffect.init();

    }

    public FancySimulationUI(BridgeState state, Thread coreSimThread) {
	super(state);
	this.coreSimThread = coreSimThread;
    }

    @Override
    public void start() {
	super.start();
	D.p("play button was pressed...");

    }

    BridgeState theState;
    SparseGridPortrayal2D collectionSitePortrayal;
    SparseGridPortrayal2D resourcePortrayal;
    SparseGridPortrayal2D obstaclesPortrayal;
    SparseGridPortrayal2D trailPortrayal;
    SparseGridPortrayal2D agentPortrayal;
    SparseGridPortrayal2D collectedResourcesPortrayal;

    protected ValueGridPortrayal2D worldPortrayal;

    protected void initStructures() {
	collectionSitePortrayal = new SparseGridPortrayal2D();
	resourcePortrayal = new SparseGridPortrayal2D();
	obstaclesPortrayal = new SparseGridPortrayal2D();
	agentPortrayal = new SparseGridPortrayal2D();
	trailPortrayal = new SparseGridPortrayal2D();
	worldPortrayal = new ValueGridPortrayal2D();
	collectedResourcesPortrayal = new SparseGridPortrayal2D();
    }

    public void init(Controller controller) {

	super.init(controller);

	// display = new Display2D(800, 650, this);
	display = new Display2D(400, 350, this);
	display.setScale(1.0);
	display.setClipping(false);

	displayFrame = display.createFrame();
	controller.registerFrame(displayFrame);
	displayFrame.setVisible(true);

	// attach all the portrayals
	display.attach(worldPortrayal, "World");

	display.attach(obstaclesPortrayal, "Obstacles");
	display.attach(collectionSitePortrayal, "Collection Sites");
	display.attach(resourcePortrayal, "Resources");
	display.attach(trailPortrayal, "Trails");
	display.attach(agentPortrayal, "Agents");
	display.attach(collectedResourcesPortrayal, "Collected Resources");

    }

    public void startCoreSimulator() {
	// before starting off the portrayals, start the core simulator
	synchronized (simLock) {
	    if (!simThreadStarted) {
		coreSimThread.start();
		simThreadStarted = true;
	    }
	}
	// now wait for the simulator to actually start up
	while (!Main.settings.__simulationStarted) {
	    Thread.yield();
	}

    }

    public void initPortrayals() {

	startCoreSimulator();

	BridgeState theState = (BridgeState) state;

	// player.play("V0 I[Piano] Eq Ch. | Eq Ch. | Dq Eq Dq Cq V1 I[Flute] Rw
	// | Rw | GmajQQQ CmajQ");

	collectedResourcesPortrayal.setField(theState.collectedResourceLocationGrid);
	collectedResourcesPortrayal.setPortrayalForAll(new ImagePortrayal2D(
		new ImageIcon(GRID_ICON_COLLECTED_RESOURCE), GRID_ICON_SCALE_FACTOR));

	// collection sites
	collectionSitePortrayal.setField(theState.collectionSiteGrid);
	collectionSitePortrayal.setPortrayalForAll(new ImagePortrayal2D(
		new ImageIcon(GRID_ICON_COLLECTION_SITE), GRID_ICON_SCALE_FACTOR));

	// resources -- they can be in different states
	resourcePortrayal.setField(theState.resourceGrid);

	resourcePortrayal.setPortrayalForAll(new FacetedPortrayal2D(new SimplePortrayal2D[] {
		new RectanglePortrayal2D(Color.TRANSLUCENT, true),
		new ImagePortrayal2D(new ImageIcon(GRID_ICON_RAW_RESOURCE), GRID_ICON_SCALE_FACTOR),
		new ImagePortrayal2D(new ImageIcon(GRID_ICON_EXTRACTED_RESOURCE),
			GRID_ICON_SCALE_FACTOR),
		new ImagePortrayal2D(new ImageIcon(GRID_ICON_PROCESSED_RESOURCE),
			GRID_ICON_SCALE_FACTOR),
		// new ImagePortrayal2D(new
		// ImageIcon(GRID_ICON_COLLECTED_RESOURCE),
		// GRID_ICON_SCALE_FACTOR)

	}

	));

	// obstacles sites
	obstaclesPortrayal.setField(theState.obstacleGrid);
	obstaclesPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.GRAY, true));

	agentPortrayal.setField(theState.agentGrid);

	agentPortrayal.setPortrayalForAll(new SimplePortrayal2D() {
	    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {

		Agent agent = (Agent) object;

		if (!agent.isProxyAgent) {
		    Agent realAgent = agent;

		    if (agent.isHostAgent) {
			realAgent = agent.activeAgent;
		    }

		    if (realAgent.__capturedResource) {
			realAgent.__capturedResource = false;
			SoundEffect.CAPTURE.play();
		    }
		    if (realAgent.interactionMode == InteractionMode.SENDING_TRAIL) {
			realAgent.interactionMode = InteractionMode.NONE;
			D.p("sending...");
			SoundEffect.SEND.play();
		    }
		    if (realAgent.interactionMode == InteractionMode.RECEIVING_TRAIL) {
			realAgent.interactionMode = InteractionMode.NONE;
			new RectanglePortrayal2D(Color.BLUE, 0.97, true).draw(object, graphics,
				info);

			D.p("receiving...");
			SoundEffect.RECEIVE.play();
			return;
		    }

		    if (agent.isCarryingResource()) {
			new ImagePortrayal2D(new ImageIcon(GRID_ICON_LOADED_AGENT),
				GRID_ICON_SCALE_FACTOR).draw(object, graphics, info);
			;
		    } else {
			new ImagePortrayal2D(new ImageIcon(GRID_ICON_AGENT), GRID_ICON_SCALE_FACTOR)
				.draw(object, graphics, info);
			;
		    }
		}

	    }

	});

	// this draws the grid lines
	IntGrid2D worldGrid = new IntGrid2D(Main.settings.WORLD_WIDTH, Main.settings.WORLD_HEIGHT,
		1);
	worldPortrayal.setField(worldGrid);
	worldPortrayal.setPortrayalForAll(new RectanglePortrayal2D(Color.LIGHT_GRAY, 0.99, false));

	// trails

	trailPortrayal.setField(theState.trailGrid);
	trailPortrayal.setPortrayalForAll(new SimplePortrayal2D() {

	    SimpleColorMap colorMap = new SimpleColorMap(TRAIL_LEVEL_MIN, TRAIL_LEVEL_MAX,
		    new Color(255, 255, 255, 0), Color.YELLOW) {

		public double filterLevel(double level) {
		    // this silly function grows very very slowly.
		    return Math.sqrt(Math.sqrt(Math.sqrt(Math.sqrt(level / 2))));
		}

	    };

	    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		MutableDouble strength = (MutableDouble) object;
		// D.p("drawing...");
		if (strength.val >= Constants.TRAIL_LEVEL_MIN) {
		    new RectanglePortrayal2D(colorMap.getColor(strength.val), 0.97, true)
			    .draw(object, graphics, info);

		}
	    }

	});

	display.reset();
	display.repaint();

    }

    public static void main(Thread coreSimThread, String[] args) {
	BridgeState bridgeState = new BridgeState(1);
	Main.settings.__showGraphics = true;
	Main.settings.__useSoundEffects = true;
	FancySimulationUI simUI = new FancySimulationUI(bridgeState, coreSimThread);

	simUI.createController();

    }

}
