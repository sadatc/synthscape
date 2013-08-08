/**
 * 
 */
package com.synthverse.synthscape.core;

import java.io.IOException;
import java.util.ArrayList;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

/**
 * @author sadat
 * 
 */

public abstract class Simulation extends SimState implements Constants {

    private static final long serialVersionUID = 2700375028430112699L;

    protected Experiment experiment;

    protected ExperimentReporter experimentReporter;

    protected int simulationCounter;

    protected int generationCounter;

    protected int stepCounter;

    protected ProblemComplexity problemComplexity;

    protected IntGrid2D obstacleGrid;

    protected IntGrid2D collectionSiteGrid;

    protected ArrayList<Int2D> collectionSiteList;

    protected IntGrid2D setupCollisionGrid;

    protected ObjectGrid2D resourceGrid;

    protected DoubleGrid2D trailGrid;

    protected SparseGrid2D agentGrid;

    protected ArrayList<Agent> agents;

    protected boolean isToroidalWorld;

    protected int numberOfAgentsPerSpecies;

    protected int numberOfObstacles;

    protected int numberOfResources;

    protected int numberOfCollectedResources;

    protected int numberOfCollectionSites;

    protected double trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

    protected AgentFactory agentFactory;

    private int gridWidth;

    private int gridHeight;

    public abstract Experiment getExperiment();

    public abstract AgentFactory getAgentFactory();

    public Simulation(long seed) throws IOException {
	super(seed);

	agentFactory = getAgentFactory();

	experiment = getExperiment();
	gridWidth = experiment.getGridWidth();
	gridHeight = experiment.getGridHeight();
	numberOfAgentsPerSpecies = experiment.getNumberOfAgentsPerSpecies();

	double gridArea = gridWidth * gridHeight;
	numberOfObstacles = (int) (gridArea * experiment.getObstacleDensity());

	numberOfResources = (int) (gridArea * experiment.getResourceDensity());
	numberOfCollectionSites = experiment.getNumberOfCollectionSites();
	problemComplexity = experiment.getProblemComplexity();

	createDataStructures();

	simulationCounter = 0;
	generationCounter = 0;
	numberOfCollectedResources = 0;

	experimentReporter = new ExperimentReporter(experiment,
		DEFAULT_FLUSH_ALWAYS_FLAG);

	isToroidalWorld = TOROIDAL_FLAG;
	trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;
    }

    private void createDataStructures() {

	obstacleGrid = new IntGrid2D(gridWidth, gridHeight, ABSENT);

	collectionSiteGrid = new IntGrid2D(gridWidth, gridHeight, ABSENT);

	collectionSiteList = new ArrayList<Int2D>();

	setupCollisionGrid = new IntGrid2D(gridWidth, gridHeight, ABSENT);

	resourceGrid = new ObjectGrid2D(gridWidth, gridHeight);

	trailGrid = new DoubleGrid2D(gridWidth, gridHeight, ABSENT);

	agentGrid = new SparseGrid2D(gridWidth, gridHeight);

	agents = new ArrayList<Agent>();

    }

    private void resetEnvironment() {

	obstacleGrid.setTo(ABSENT);
	collectionSiteGrid.setTo(ABSENT);
	collectionSiteList.clear();
	setupCollisionGrid.setTo(ABSENT);

	resourceGrid.setTo(ResourceState.NULL);
	trailGrid.setTo(ABSENT);

	D.p("environment cleared...");
    }

    private void resetAll() {
	resetEnvironment();
	agentGrid.clear();
	D.p("agents cleared...");
    }

    public boolean isToroidalWorld() {
	return isToroidalWorld;
    }

    public void setToroidalWorld(boolean isToroidalWorld) {
	this.isToroidalWorld = isToroidalWorld;
    }

    public int getNumberOfObstacles() {
	return numberOfObstacles;
    }

    public void setNumberOfObstacles(int numberOfObstacles) {
	this.numberOfObstacles = numberOfObstacles;
    }

    public void setNumberOfResources(int numberOfResources) {
	this.numberOfResources = numberOfResources;

    }

    public int getNumberOfCollectionSites() {
	return numberOfCollectionSites;
    }

    public void setNumberOfCollectionSites(int numberOfCollectionSites) {
	this.numberOfCollectionSites = numberOfCollectionSites;
    }

    public int getNumberOfResources() {
	return numberOfResources;
    }

    public int getGridWidth() {
	return gridWidth;
    }

    public int getGridHeight() {
	return gridHeight;
    }

    public ProblemComplexity getProblemComplexity() {
	return problemComplexity;
    }

    private void setupPrimaryCollectionSite() {
	// set the primary collection site
	collectionSiteGrid.field[PRIMARY_COLLECTION_SITE_X][PRIMARY_COLLECTION_SITE_Y] = PRESENT;
	setupCollisionGrid.field[PRIMARY_COLLECTION_SITE_X][PRIMARY_COLLECTION_SITE_Y] = PRESENT;
	collectionSiteList.add(new Int2D(PRIMARY_COLLECTION_SITE_X,
		PRIMARY_COLLECTION_SITE_Y));
    }

    private void setupNonPrimaryCollectionSites() {
	for (int i = 0; i < (numberOfCollectionSites - 1); i++) {
	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);
	    // make sure there isn't an obstacle there already...
	    while (collectionSiteGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);

	    }
	    collectionSiteGrid.field[randomX][randomY] = PRESENT;
	    setupCollisionGrid.field[randomX][randomY] = PRESENT;
	    collectionSiteList.add(new Int2D(randomX, randomY));

	}
    }

    private void setupObstacles() {
	// create obstacles in random locations
	for (int i = 0; i < numberOfObstacles; i++) {

	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);
	    // make sure there isn't an obstacle there already...
	    while (setupCollisionGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    }
	    setupCollisionGrid.field[randomX][randomY] = PRESENT;
	    obstacleGrid.field[randomX][randomY] = PRESENT;

	}

    }

    private void setupResources() {
	for (int i = 0; i < numberOfResources; i++) {

	    int randomX = 0;
	    int randomY = 0;

	    // make sure there are no resources, collectionSites, and obstacles
	    // here
	    do {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    } while (setupCollisionGrid.field[randomX][randomY] == PRESENT);
	    resourceGrid.field[randomX][randomY] = ResourceState.RAW;
	    setupCollisionGrid.field[randomX][randomY] = PRESENT;

	}
	this.numberOfCollectedResources = 0;

    }

    private void setupEnvironment() {
	setupPrimaryCollectionSite();
	setupNonPrimaryCollectionSites();
	setupObstacles();
	setupResources();
    }

    private void setupFirstGeneration() {
	// populate with agents

	for (int i = 0; i < numberOfAgentsPerSpecies; i++) {

	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);

	    while (setupCollisionGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    }
	    setupCollisionGrid.field[randomX][randomY] = PRESENT;

	    Agent agent = agentFactory.create(this, SEED_GENERATION_NUMBER, i,
		    experiment.getMaxStepsPerAgent(), randomX, randomY);

	    agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));

	    agents.add(agent);

	    // add agents to the scheduler so that they can be stepped
	    schedule.scheduleRepeating(agent);

	}

    }

    private void setupNextGeneration() {
	// populate with agents
	generationCounter++;
	for (int i = 0; i < numberOfAgentsPerSpecies; i++) {

	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);

	    while (setupCollisionGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    }
	    setupCollisionGrid.field[randomX][randomY] = PRESENT;

	    Agent agent = agents.get(i);
	    agent.reset();
	    agent.generation = generationCounter;
	    agent.x = randomX;
	    agent.y = randomY;

	    agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));
	}

	D.p("finished population generation #" + generationCounter);
    }

    private void startSimulation() {

	setupEnvironment();
	setupFirstGeneration();

	experiment.setStartDate();
	experimentReporter.setupReporter();

	// this is run at the end of each step

	schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
	    public void step(SimState state) {
		stepCounter++;
		// statistics.simData.numberOfSteps++;
		// D.p("sim loop");

		// this fades the trails at the appropriate rates
		trailGrid.lowerBound(0.0);
		trailGrid.multiply(trailEvaporationConstant);

		// this handles statistics
		doEndOfStepTasks();

		// this terminates the simulation if, conditions have been
		// reached
		if (evaluateSimulationTerminateCondition()) {
		    D.p("**** end of simulation ***");

		    stepCounter = 0;
		    simulationCounter++;

		    if (simulationCounter < experiment
			    .getSimulationsPerExperiment()) {

			startNextSimulation();
		    } else {
			D.p("**** end of experiment ***");
			experiment.setEndDate();
			experimentReporter.cleanupReporter();
			finish();
		    }
		}
	    }

	}, 1);

    }

    protected void startNextSimulation() {
	resetEnvironment();
	setupEnvironment();
	setupNextGeneration();

    }

    protected boolean evaluateSimulationTerminateCondition() {
	return (this.numberOfCollectedResources >= this.numberOfResources || this.stepCounter > experiment
		.getStepsPerSimulation());
    }

    protected void doEndOfStepTasks() {
	// statistics.takeStepSnapshot();
    }

    public void start() {
	this.generationCounter = 0;
	this.simulationCounter = 0;
	this.stepCounter = 0;

	super.start();
	resetAll();
	startSimulation();
    }

    public static String[] parseArguments(String string) {
	String[] array = string.split(" ");
	return array;
    }

    public static void _main(String[] arg) {
	doLoop(Simulation.class, arg);
	// statistics.printExperimentSummary();
	System.exit(0);
    }

    public static void main(String[] arg) {
	_main(arg);
    }

    public void reportEvent(Species species, int agentId, int stepCounter,
	    int x, int y, Event event, String source, String destination) {

	experimentReporter
		.reportEvent(simulationCounter, generationCounter, species,
			agentId, stepCounter, x, y, event, source, destination);

    }

}
