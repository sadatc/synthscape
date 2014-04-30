package com.synthverse.synthscape.core;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.Valuable;

import com.synthverse.Main;
import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.util.LogUtils;

public abstract class Agent implements Constants, Steppable, Valuable, Comparable<Agent> {

    protected static final long serialVersionUID = -5129827193602692370L;
    protected static Logger logger = Logger.getLogger(Agent.class.getName());
    protected static long _optimazationTotalAgentsCounters = 0;
    protected static int _agentCounter = 0;

    private boolean scheduled = false;
    private Agent archetypeReference = null;

    private HashMap<String, Integer> intPropertyMap = new HashMap<String, Integer>();

    public Simulation sim;

    private int agentId = UNASSIGNED_AGENT_ID;

    private Team team = null;

    protected int agentStepCounter;

    protected int maxSteps;

    protected int x;

    protected int y;

    protected Set<InteractionMechanism> interactionMechanisms = EnumSet.noneOf(InteractionMechanism.class);

    protected Species species;

    protected boolean isCarryingResource;

    protected ResourceState stateOfCarriedResource;

    protected int previousX;

    protected int previousY;

    protected boolean locationIsCollectionSite;

    protected boolean locationHasRawResource;

    protected boolean locationHasExtractedResource;

    protected boolean locationHasProcessedResource;

    protected boolean locationHasTrail;

    protected double fitness = 0.0;

    protected int generation;

    protected VirtualMachine virtualMachine;

    protected Program program;

    public Stats agentStats = new Stats();

    protected boolean providedFeedback = false;

    static {
	LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
    }

    abstract public double doubleValue();

    abstract public void stepAction(SimState state);

    protected void initGenotype() {
	this.program = Program.Factory.createRandom(sim.random);
	VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this, sim.random);
	vm.overwriteGenotypeWithProgram(this.program);
	vm.setCpuCycles(sim.getMaxStepsPerAgent());
	this.setVirtualMachine(vm);
    }

    private void initId() {
	if (agentId == UNASSIGNED_AGENT_ID) {
	    generateAgentId();
	    _optimazationTotalAgentsCounters++;
	}
    }

    @SuppressWarnings("unused")
    private Agent() {
	throw new AssertionError("Agent constructor is restricted");
    }

    protected Agent(Simulation simulation, Species species) {
	initId();
	setSim(simulation);
	setSpecies(species);
	initGenotype();
    }

    protected Agent(Simulation simulation, Species species, int generationNumber, int maxSteps, int startX, int startY) {
	initId();
	initGenotype();
	setSim(simulation);
	setMaxSteps(maxSteps);
	setX(startX);
	setY(startY);
	setGeneration(generationNumber);
	setSpecies(species);
	setInteractionMechanisms(simulation.getInteractionMechanisms());
	initGenotype();
    }

    public final boolean locationHasObstacle(int x, int y) {
	return (sim.obstacleGrid.field[x][y] == PRESENT);
    }

    /*
     * 
     * ALL OPERATIONS ARE DEFINED HERE
     * 
     * _operationXXX() are generic and don't concern with energy accounting
     * operationXXX() are more concrete and have energy related accounting
     */

    public final void _operationMoveAbsolute(int newX, int newY) {
	Int2D location = sim.agentGrid.getObjectLocation(this);

	// apply moving logic, only if we are moving to a new location
	if (newX != location.x || newY != location.y) {
	    // also, only move if new location is not an obstacle
	    if (sim.obstacleGrid.field[newX][newY] != PRESENT) {

		previousX = location.x;
		previousY = location.y;

		// check various status and update accordingly
		sim.agentGrid.setObjectLocation(this, new Int2D(newX, newY));
		updateLocationStatus(newX, newY);
	    }
	}

    }

    public final void _operationMoveRelative(int deltaX, int deltaY) {
	Int2D location = sim.agentGrid.getObjectLocation(this);

	int newX = sim.agentGrid.stx(location.x + deltaX);
	int newY = sim.agentGrid.stx(location.y + deltaY);

	// apply moving logic, only if we are moving to a new location
	if (newX != location.x || newY != location.y) {
	    // also, only move if new location is not an obstacle
	    if (sim.obstacleGrid.field[newX][newY] != PRESENT) {

		previousX = location.x;
		previousY = location.y;

		// check various status and update accordingly
		sim.agentGrid.setObjectLocation(this, new Int2D(newX, newY));
		updateLocationStatus(newX, newY);
	    }
	}

    }

    public void _operationRandomMove() {

	int newX = x;
	int newY = y;

	boolean foundNewUblockedLocation = false;
	do {

	    int xDelta = (sim.random.nextInt(3) - 1);
	    int yDelta = (sim.random.nextInt(3) - 1);
	    int xMod = sim.agentGrid.stx(x + xDelta);
	    int yMod = sim.agentGrid.sty(y + yDelta);

	    if (!(xDelta == 0 && yDelta == 0) && xMod >= 0 && xMod < sim.getGridWidth() && yMod >= 0
		    && yMod < sim.getGridHeight() && sim.obstacleGrid.field[xMod][yMod] == ABSENT) {
		newX = xMod;
		newY = yMod;
		foundNewUblockedLocation = true;
	    }
	} while (!foundNewUblockedLocation);

	_operationMoveAbsolute(newX, newY);
    }

    public final void _operationMoveToLocationAt(int newX, int newY) {

	if (!(this.x == newX && this.y == newY)) {
	    int deltaX = 0;
	    int deltaY = 0;

	    if (this.x < newX) {
		deltaX++;
	    }

	    if (this.x > newX) {
		deltaX--;
	    }

	    if (this.y < newY) {
		deltaY++;
	    }

	    if (this.y > newY) {
		deltaY--;
	    }

	    if (deltaX != 0 || deltaY != 0) {
		int adjustedX = this.x + deltaX;
		int adjustedY = this.y + deltaY;
		if (locationHasObstacle(newX, newY)) {
		    _operationRandomMove();
		} else {
		    _operationMoveAbsolute(adjustedX, adjustedY);
		}
	    }
	}
    }

    public void _operationLeaveTrail(DoubleGrid2D grid) {
	Int2D location = sim.agentGrid.getObjectLocation(this);
	int x = location.x;
	int y = location.y;

	grid.field[x][y] = 100;
	sim.reportEvent(this, Event.SENT_GENERIC_TRAIL, "" + this.agentId, NA);
    }

    public final boolean _operationPerformResourceAction(Task action, ObjectGrid2D resourceGrid) {

	boolean actionPerformed = false;

	ResourceState resourceState = (ResourceState) resourceGrid.field[x][y];

	switch (action) {
	case EXTRACTION:
	    if (resourceState == ResourceState.RAW) {
		resourceGrid.field[x][y] = ResourceState.EXTRACTED;
		actionPerformed = true;
	    }
	    break;
	case PROCESSING:
	    if (resourceState == ResourceState.EXTRACTED) {
		resourceGrid.field[x][y] = ResourceState.PROCESSED;
		actionPerformed = true;
	    }
	    break;
	default:
	    // TODO: add something later...
	}

	return actionPerformed;
    }

    public void operationFollowBroadcast(SignalType signalType) {
	if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
	    if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.PROCESSED_RESOURCE)
		    || this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

		Broadcast broadcast = sim.getRegisteredBroadcast(signalType);
		if (broadcast != null) {
		    // logger.info("moving to a broadcast:" + signalType);
		    _operationMoveAbsolute(broadcast.getX(), broadcast.getY());
		}
	    }
	}
    }

    public void operationBroadcast(SignalType signalType) {
	if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
	    if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.PROCESSED_RESOURCE)
		    || this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
		Broadcast broadcast = new Broadcast(this, signalType, this.x, this.y, 0);
		sim.registerBroadcast(broadcast);
	    }
	}
    }

    public boolean operationDetectBroadcast(SignalType signalType) {
	boolean result = false;
	if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
	    if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.PROCESSED_RESOURCE)
		    || this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
		Broadcast broadcast = sim.getRegisteredBroadcast(signalType);
		if (broadcast != null) {
		    result = true;
		}
	    }
	}
	return result;
    }

    public final void _operationFollowTrail(DoubleGrid2D trail) {
	// we need to check all neighboring cells to detect which one
	// has the highest concentration of trail A and then
	// move there. If none is found, move at random
	int maxTrail = Integer.MIN_VALUE;
	int maxX = 0, maxY = 0;
	for (int deltaX = -1; deltaX <= 1; deltaX++) {
	    for (int deltaY = -1; deltaY <= 1; deltaY++) {
		// except for the center...
		if (deltaX != 0 && deltaY != 0) {
		    int scanX = trail.stx(this.x + deltaX);
		    int scanY = trail.stx(this.y + deltaY);

		    double trailAmount = trail.field[scanX][scanY];
		    if (trailAmount > maxTrail) {
			maxTrail = (int) trailAmount;
			maxX = scanX;
			maxY = scanY;
		    }
		}
	    }
	}
	// now move to the highest concentration
	// or move randomly
	if (maxTrail > 0) {
	    sim.reportEvent(this, Event.RECEIVED_GENERIC_TRAIL, NA, "" + this.agentId);
	    _operationMoveAbsolute(maxX, maxY);
	} else {
	    _operationRandomMove();
	}

    }

    public final void _operationFollowTrail(DoubleGrid2D trailA, DoubleGrid2D trailB, DoubleGrid2D trailC) {
	// we need to check all neighboring cells to detect which one
	// has the highest concentration of trail A and then
	// move there. If none is found, move at random
	int maxTrail = Integer.MIN_VALUE;
	int maxX = 0, maxY = 0;
	for (int deltaX = -1; deltaX <= 1; deltaX++) {
	    for (int deltaY = -1; deltaY <= 1; deltaY++) {
		// except for the center...
		if (deltaX != 0 && deltaY != 0) {
		    int scanX = trailA.stx(this.x + deltaX);
		    int scanY = trailA.stx(this.y + deltaY);

		    double trailAmountA = trailA.field[scanX][scanY];
		    if (trailAmountA > maxTrail) {
			maxTrail = (int) trailAmountA;
			maxX = scanX;
			maxY = scanY;
		    }

		    double trailAmountB = trailB.field[scanX][scanY];
		    if (trailAmountB > maxTrail) {
			maxTrail = (int) trailAmountB;
			maxX = scanX;
			maxY = scanY;
		    }

		    double trailAmountC = trailC.field[scanX][scanY];
		    if (trailAmountC > maxTrail) {
			maxTrail = (int) trailAmountC;
			maxX = scanX;
			maxY = scanY;
		    }
		}
	    }
	}
	// now move to the highest concentration
	// or move randomly
	if (maxTrail > 0) {
	    _operationMoveAbsolute(maxX, maxY);
	} else {
	    _operationRandomMove();
	}

    }

    /*
     * public final void operationMove(int newX, int newY) {
     * 
     * _operationMove(newX, newY); spendEnergy(Trait.GENERIC);
     * 
     * }
     */

    public final void operationMoveNorth() {
	_operationMoveRelative(0, -1);

    }

    public final void operationMoveNorthEast() {
	_operationMoveRelative(1, -1);

    }

    public final void operationMoveNorthWest() {
	_operationMoveRelative(-1, -1);

    }

    public final void operationMoveSouth() {
	_operationMoveRelative(0, 1);

    }

    public final void operationMoveSouthEast() {
	_operationMoveRelative(1, 1);

    }

    public final void operationMoveSouthWest() {
	_operationMoveRelative(-1, 1);

    }

    public final void operationMoveEast() {
	_operationMoveRelative(1, 0);

    }

    public final void operationMoveWest() {
	_operationMoveRelative(-1, 0);

    }

    public final void operationMoveRelative(int deltaX, int deltaY) {
	if (deltaX < -1) {
	    deltaX = -1;
	}
	if (deltaX > 1) {
	    deltaX = 1;
	}

	if (deltaY < -1) {
	    deltaY = -1;
	}

	if (deltaY > 1) {
	    deltaY = 1;
	}
	_operationMoveRelative(deltaX, deltaY);

    }

    public final void operationRandomMove() {
	_operationRandomMove();

    }

    public final void operationMoveToPrimaryCollectionSite() {
	_operationMoveToLocationAt(sim.settings.PRIMARY_COLLECTION_SITE_X, sim.settings.PRIMARY_COLLECTION_SITE_Y);
	sim.reportEvent(this, Event.MOVE_TO_CLOSEST_COLLECTION_SITE, NA, NA);

    }

    public final void operationMoveToClosestCollectionSite() {

	if (species.getTraits().contains(Trait.HOMING)) {
	    // first let's find out the closest collection site
	    Int2D closestCollectionSite = null;
	    double closestDistance = Double.MAX_VALUE;

	    for (Int2D collectionSite : this.sim.collectionSiteList) {
		double distance = collectionSite.distance(this.x, this.y);
		if (collectionSite.distance(this.x, this.y) < closestDistance) {
		    closestCollectionSite = collectionSite;
		    closestDistance = distance;
		}
	    }

	    _operationMoveToLocationAt(closestCollectionSite.x, closestCollectionSite.y);
	    sim.reportEvent(this, Event.MOVE_TO_CLOSEST_COLLECTION_SITE, NA, NA);
	}
    }

    public final void operationMoveToClosestAgent() {
	if (species.getTraits().contains(Trait.FLOCKING)) {

	    // first let's find out the closest agent
	    Int2D closestAgentLocation = null;
	    double closestDistance = Double.MAX_VALUE;

	    Bag agentBag = sim.agentGrid.getAllObjects();

	    for (int i = 0; i < agentBag.numObjs; i++) {

		Agent agent = (Agent) agentBag.get(i);
		if (agent != this) {
		    Int2D agentLocation = new Int2D(agent.x, agent.y);
		    double distance = agentLocation.distance(this.x, this.y);
		    if (distance < closestDistance) {
			closestAgentLocation = agentLocation;
			closestDistance = distance;
		    }
		}
	    }
	    if (closestAgentLocation != null) {
		_operationMoveToLocationAt(closestAgentLocation.x, closestAgentLocation.y);
		sim.reportEvent(this, Event.MOVE_TO_CLOSEST_AGENT, NA, NA);
	    }

	}
    }

    public final void operationFollowTrail() {
	if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
	    sim.reportEvent(this, Event.SEARCHED_GENERIC_TRAIL, "" + this.agentId, NA);
	    _operationFollowTrail(sim.trailGrid);

	    // sim.statistics.stepData.trailFollows++;

	}
    }

    public void operationLeaveTrail() {
	if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
	    _operationLeaveTrail(sim.trailGrid);

	    // sim.statistics.stepData.trailDrops++;

	    updateLocationStatus(this.x, this.y);

	}
    }

    public final boolean operationDetectCollectionSite() {
	if (species.getTraits().contains(Trait.HOMING)) {
	    return this.locationIsCollectionSite;
	} else {
	    return false;
	}
    }

    public final boolean operationDetectRawResource() {
	if (species.getTraits().contains(Trait.DETECTION)) {
	    if (this.locationHasRawResource) {
		sim.reportEvent(this, Event.DETECTED_RAW_RESOURCE, NA, NA);
	    }
	    return (this.locationHasRawResource);
	} else {
	    return false;
	}

    }

    public final boolean operationDetectExtractedResource() {
	if (species.getTraits().contains(Trait.DETECTION)) {
	    if (this.locationHasExtractedResource) {
		sim.reportEvent(this, Event.DETECTED_EXTRACTED_RESOURCE, NA, NA);
	    }
	    return this.locationHasExtractedResource;
	} else {
	    return false;
	}
    }

    public boolean operationDetectProcessedResource() {
	if (species.getTraits().contains(Trait.DETECTION)) {
	    if (this.locationHasProcessedResource) {
		sim.reportEvent(this, Event.DETECTED_PROCESSED_RESOURCE, NA, NA);
	    }
	    return this.locationHasProcessedResource;
	} else {
	    return false;
	}
    }

    public final boolean operationDetectTrail() {
	if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
	    sim.reportEvent(this, Event.SEARCHED_GENERIC_TRAIL, "" + this.agentId, NA);
	    if (this.locationHasTrail) {
		sim.reportEvent(this, Event.RECEIVED_GENERIC_TRAIL, NA, "" + this.agentId);
	    }
	    return this.locationHasTrail;
	} else {
	    return false;
	}
    }

    public final void operationExtractResource() {
	if (species.getTraits().contains(Trait.EXTRACTION)) {
	    if (_operationPerformResourceAction(Task.EXTRACTION, this.sim.resourceGrid)) {
		// sim.statistics.stepData.resourceExtracts++;
		sim.reportEvent(this, Event.EXTRACTED_RESOURCE, NA, NA);

	    }

	    updateLocationStatus(this.x, this.y);
	}
    }

    public void operationProcessResource() {
	if (species.getTraits().contains(Trait.PROCESSING)) {
	    if (_operationPerformResourceAction(Task.PROCESSING, this.sim.resourceGrid)) {
		// sim.statistics.stepData.resourceProcesses++;
		sim.reportEvent(this, Event.PROCESSED_RESOURCE, NA, NA);
	    }

	    updateLocationStatus(this.x, this.y);
	}

    }

    public final void operationLoadResource() {

	if (species.getTraits().contains(Trait.TRANSPORTATION)) {

	    if (locationHasExtractedResource || locationHasProcessedResource || locationHasRawResource) {
		if (!isCarryingResource) {
		    isCarryingResource = true;
		    stateOfCarriedResource = (ResourceState) this.sim.resourceGrid.field[x][y];

		    this.sim.resourceGrid.field[x][y] = ResourceState.NULL;
		    updateLocationStatus(this.x, this.y);
		    sim.reportEvent(this, Event.LOADED_RESOURCE, NA, NA);
		}
	    }

	}
    }

    public final void operationUnLoadResource() {
	if (species.getTraits().contains(Trait.TRANSPORTATION) && isCarryingResource) {
	    if (!locationHasExtractedResource && !locationHasProcessedResource && !locationHasRawResource) {

		isCarryingResource = false;

		// depending on problem complexity, this resource maybe
		// ready to be reclaimed or dropped

		boolean dropResource = true; // assume we'll drop it

		if (this.locationIsCollectionSite) {
		    // depending on the problem complexity and current state
		    // this resource maybe reclaimed

		    if (this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS) {
			if (stateOfCarriedResource == ResourceState.EXTRACTED) {
			    dropResource = false;
			    this.sim.numberOfCollectedResources++;
			    sim.reportEvent(this, Event.UNLOADED_RESOURCE, NA, NA);
			    sim.reportEvent(this, Event.COLLECTED_RESOURCE, NA, NA);
			    // logger.info("CAPTURE!! 3 complex");
			}

		    } else if (this.sim.problemComplexity.equals(ProblemComplexity.FOUR_SEQUENTIAL_TASKS)) {
			if (stateOfCarriedResource == ResourceState.PROCESSED) {
			    dropResource = false;
			    this.sim.numberOfCollectedResources++;
			    sim.reportEvent(this, Event.UNLOADED_RESOURCE, NA, NA);
			    sim.reportEvent(this, Event.COLLECTED_RESOURCE, NA, NA);
			    // logger.info("CAPTURE!! 4 complex");
			}

		    } else {
			logger.severe("Problem Complexity Unknown!");
			System.exit(-1);
		    }

		}

		if (dropResource) {
		    this.sim.resourceGrid.field[x][y] = stateOfCarriedResource;
		    sim.reportEvent(this, Event.UNLOADED_RESOURCE, NA, NA);

		}

	    }

	    updateLocationStatus(this.x, this.y);

	}
    }

    public void reset() {
	// logger.info("agent reset was called");
	agentStepCounter = 0;
	isCarryingResource = false;
	fitness = 0.0;
	agentStats.clear();
    }

    public void updateLocationStatus(int x, int y) {

	this.x = x;
	this.y = y;

	boolean locationIsChanging = (x != previousX || y != previousY);

	this.locationHasExtractedResource = false;
	this.locationHasProcessedResource = false;
	this.locationHasRawResource = false;

	if (sim.resourceGrid.field[x][y] == ResourceState.RAW) {
	    this.locationHasRawResource = true;
	} else if (sim.resourceGrid.field[x][y] == ResourceState.EXTRACTED) {
	    this.locationHasExtractedResource = true;
	} else if (sim.resourceGrid.field[x][y] == ResourceState.PROCESSED) {
	    this.locationHasProcessedResource = true;
	}

	if (sim.trailGrid.field[x][y] > 0) {
	    this.locationHasTrail = true;
	    if (locationIsChanging) {
		// sim.statistics.stepData.trailHits++;
		sim.reportEvent(this, Event.RECEIVED_GENERIC_TRAIL, NA, "" + this.agentId);

	    }
	} else {
	    this.locationHasTrail = false;
	}

	if (sim.collectionSiteGrid.field[x][y] > 0) {
	    this.locationIsCollectionSite = true;
	} else {
	    this.locationIsCollectionSite = false;
	}

    }

    public boolean hasStepsRemaining() {
	return agentStepCounter < maxSteps;
    }

    public void step(SimState state) {
	if (hasStepsRemaining()) {

	    stepAction(state);
	    agentStepCounter++;

	}
    }

    public double getFitness() {
	return this.fitness;
    }

    public long getId() {
	return getAgentId();
    }

    public int getGeneration() {
	return this.generation;
    }

    public Program getProgram() {
	return this.program;
    }

    public String getSignature() {
	String result = "" + this.getAgentId();
	if (this.program != null) {
	    result += ":" + this.program.getSignature();
	}
	return result;

    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
	this.virtualMachine = virtualMachine;

    }

    public void loadProgramInVirtualMachine() {
	virtualMachine.overwriteGenotypeWithProgram(program);

    }

    public int compareTo(Agent a) {
	if (this.fitness > a.getFitness()) {
	    return 1;
	} else if (this.fitness < a.getFitness()) {
	    return -1;
	}
	return 0;
    }

    public int getAgentId() {
	return agentId;
    }

    private void generateAgentId() {
	_agentCounter++;
	this.agentId = _agentCounter;
    }

    public int getAgentStepCounter() {
	return agentStepCounter;
    }

    public void setStepCounter(int stepCounter) {
	this.agentStepCounter = stepCounter;
    }

    public int getMaxSteps() {
	return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
	this.maxSteps = maxSteps;
    }

    public int getX() {
	return x;
    }

    public void setX(int x) {
	this.x = x;
    }

    public int getY() {
	return y;
    }

    public void setY(int y) {
	this.y = y;
    }

    public VirtualMachine getVirtualMachine() {
	return virtualMachine;
    }

    public void setFitness(double fitness) {
	this.fitness = fitness;
    }

    public void setGeneration(int generation) {
	this.generation = generation;
    }

    public void setSim(Simulation sim) {
	this.sim = sim;
    }

    protected static Agent generateAgent(long generation, long agentId, int x, int y) {
	return null;
    }

    public Simulation getSim() {
	return sim;
    }

    public Species getSpecies() {
	return species;
    }

    public void setSpecies(Species species) {
	this.species = species;
    }

    public void setInteractionMechanisms(Set<InteractionMechanism> interactionMechanisms) {
	this.interactionMechanisms = interactionMechanisms;
    }

    public Set<InteractionMechanism> getInteractionMechanisms() {
	return this.interactionMechanisms;
    }

    public int getIntPropertyValue(String key) {
	return this.intPropertyMap.get(key);
    }

    public void setIntPropertyValue(String key, int value) {
	this.intPropertyMap.put(key, value);
    }

    public boolean isScheduled() {
	return scheduled;
    }

    public void setScheduled(boolean scheduled) {
	this.scheduled = scheduled;
    }

    /**
     * Given an agent, this makes a deep copy of its genotype and sets a link
     * back to the original archetype
     * 
     * @param archetype
     */
    public void cloneAndLinkGeneArcheType(Agent archetype) {
	this.program = new Program(archetype.getProgram());
	this.getVirtualMachine().overwriteGenotypeWithProgram(this.program);
	this.getVirtualMachine().setCpuCycles(archetype.getVirtualMachine().getCpuCycles());
	archetypeReference = archetype;
    }

    public Agent getArchetypeReference() {
	return archetypeReference;
    }

    public static long get_optimazationTotalAgentsCounters() {
	return _optimazationTotalAgentsCounters;
    }

    public boolean isCarryingResource() {
	return isCarryingResource;
    }

    public void setCarryingResource(boolean isCarryingResource) {
	this.isCarryingResource = isCarryingResource;
    }

    public ResourceState getStateOfCarriedResource() {
	return stateOfCarriedResource;
    }

    public void setStateOfCarriedResource(ResourceState stateOfCarriedResource) {
	this.stateOfCarriedResource = stateOfCarriedResource;
    }

    @Override
    public String toString() {
	return "Agent [agentId=" + agentId + ", teamId=" + ((team != null) ? team.getTeamId() : -1)
		+ ", agentStepCounter=" + agentStepCounter + ", maxSteps=" + maxSteps + ", x=" + x + ", y=" + y
		+ ", interactionMechanisms=" + interactionMechanisms + ", species=" + species + ", isCarryingResource="
		+ isCarryingResource + ", generation=" + generation + ", program=" + program + "]";
    }

    public String toString2() {
	return "Agent [agentId=" + agentId + ", teamId=" + ((team != null) ? team.getTeamId() : -1)
		+ ", agentStepCounter=" + agentStepCounter + ", maxSteps=" + maxSteps + ", x=" + x + ", y=" + y
		+ ", interactionMechanisms=" + interactionMechanisms + ", species=" + species + ", isCarryingResource="
		+ isCarryingResource + ", generation=" + generation + ", program=" + program.getSignature() + "]";
    }

    public Team getTeam() {
	return team;
    }

    public void setTeam(Team team) {
	this.team = team;
    }

    public boolean isProvidedFeedback() {
	return providedFeedback;
    }

    public void setProvidedFeedback(boolean providedFeedback) {
	this.providedFeedback = providedFeedback;
    }

    protected void setLocation(int newX, int newY) {
	setX(newX);
	setY(newY);
    }

}
