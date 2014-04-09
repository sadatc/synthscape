/**
 * Test Agent
 */
package com.synthverse.synthscape.test.manuallycoded;

import sim.engine.SimState;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.ProblemComplexity;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class ManuallyCodedAgent extends Agent {

    enum AgentState {
	INIT, DETECT_RAW, DETECT_EXTRACT, EXTRACT_MODE, PROCESS_MODE, TRANSPORT_MODE, DETECT_PROCESSED, LOAD_EXTRACT, LOAD_PROCESSED, MOVE_TO_COLLECTION_SITE, UNLOAD_RESOURCE, SEEK_COLLECTION_SITE, SEEK_NEW_RESOURCE;
    }

    AgentState myMode = AgentState.INIT;

    public ManuallyCodedAgent(Simulation simulation, Species species) {
	super(simulation, species);

    }

    public ManuallyCodedAgent(Simulation sim, Species species, int generationNumber, int maxSteps, int startX,
	    int startY) {
	super(sim, species, generationNumber, maxSteps, startX, startY);

    }

    public void leaveFollowTrail(SimState state) {
	// the main idea is as follows
	// the agent is in a particular state
	// given it's state, it can only execute
	// an operation and move to a newer state
	Simulation theSim = (Simulation) state;
	boolean isSender = ((this.getAgentId() % 497)==0);

	this.operationRandomMove();
	if(isSender) {
	    this.operationLeaveTrail();
	} else {
	    this.operationFollowTrail();
	}
	

    }

    @Override
    public void stepAction(SimState state) {
	// detectExtractProcessCollect(state);
	leaveFollowTrail(state);

    }

    private void detectExtractProcessCollect(SimState state) {
	// the main idea is as follows
	// the agent is in a particular state
	// given it's state, it can only execute
	// an operation and move to a newer state
	Simulation theSim = (Simulation) state;

	switch (myMode) {
	case INIT:
	case DETECT_RAW:
	    if (this.operationDetectRawResource()) {
		myMode = AgentState.EXTRACT_MODE;
	    } else {
		myMode = AgentState.DETECT_EXTRACT;
	    }

	    break;
	case DETECT_EXTRACT:
	    if (this.operationDetectExtractedResource()) {
		if (theSim.getProblemComplexity() == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
		    myMode = AgentState.PROCESS_MODE;
		} else {
		    myMode = AgentState.LOAD_EXTRACT;
		}
	    } else {
		// no extracts detected...
		if (theSim.getProblemComplexity() == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
		    myMode = AgentState.DETECT_PROCESSED;
		} else {
		    myMode = AgentState.SEEK_NEW_RESOURCE;
		}
	    }
	    break;
	case DETECT_PROCESSED:
	    if (this.operationDetectProcessedResource()) {
		myMode = AgentState.LOAD_PROCESSED;
	    } else {
		myMode = AgentState.SEEK_NEW_RESOURCE;
	    }
	    break;
	case EXTRACT_MODE:
	    this.operationExtractResource();
	    myMode = AgentState.SEEK_NEW_RESOURCE;
	    // myMode = AgentState.DETECT_EXTRACT;
	    // logger.info(oldState + " => " + myMode);

	    break;
	case PROCESS_MODE:
	    this.operationProcessResource();
	    myMode = AgentState.DETECT_PROCESSED;
	    // logger.info(oldState + " => " + myMode);

	    break;
	case LOAD_EXTRACT:
	    this.operationLoadResource();
	    myMode = AgentState.MOVE_TO_COLLECTION_SITE;
	    break;
	case LOAD_PROCESSED:
	    this.operationLoadResource();
	    myMode = AgentState.MOVE_TO_COLLECTION_SITE;
	    break;
	case MOVE_TO_COLLECTION_SITE:
	    if (this.operationDetectCollectionSite()) {
		myMode = AgentState.UNLOAD_RESOURCE;
	    } else {
		myMode = AgentState.SEEK_COLLECTION_SITE;
	    }
	    break;
	case UNLOAD_RESOURCE:
	    this.operationUnLoadResource();
	    myMode = AgentState.SEEK_NEW_RESOURCE;
	    break;
	case SEEK_COLLECTION_SITE:
	    this.operationRandomMove();
	    myMode = AgentState.MOVE_TO_COLLECTION_SITE;
	    break;

	case SEEK_NEW_RESOURCE:
	    this.operationRandomMove();
	    myMode = AgentState.INIT;
	    break;

	default:
	    this.operationRandomMove();

	}

    }

    @Override
    public double doubleValue() {
	double result = 0.0; // normal agent
	if (this.isCarryingResource) {
	    result = 1.0;
	}
	return result;

    }

}