/**
 * 
 */
package com.synthverse.synthscape.core;

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

	protected int numberOfAgents;

	protected int numberOfObstacles;

	protected int numberOfResources;

	protected int numberOfCollectedResources;

	protected int numberOfCollectionSites;

	protected double trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

	protected static Statistics statistics;

	protected long generation;

	protected AgentFactory agentFactory;

	static {
		statistics = new Statistics();
	}

	public Simulation(AgentFactory agentFactory,
			ProblemComplexity problemComplexity, long seed) {
		super(seed);
		setProblemComplexity(problemComplexity);
		setAgentFactory(agentFactory);
		createDataStructures();
	}

	public void setProblemComplexity(ProblemComplexity problemComplexity) {
		this.problemComplexity = problemComplexity;
	}

	public ProblemComplexity getProblemComplexity() {
		return problemComplexity;
	}

	public void setAgentFactory(AgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

	private void createDataStructures() {

		obstacleGrid = new IntGrid2D(WORLD_WIDTH, WORLD_HEIGHT, ABSENT);

		collectionSiteGrid = new IntGrid2D(WORLD_WIDTH, WORLD_HEIGHT, ABSENT);

		collectionSiteList = new ArrayList<Int2D>();

		setupCollisionGrid = new IntGrid2D(WORLD_WIDTH, WORLD_HEIGHT, ABSENT);

		resourceGrid = new ObjectGrid2D(WORLD_WIDTH, WORLD_HEIGHT);

		trailGrid = new DoubleGrid2D(WORLD_WIDTH, WORLD_HEIGHT, ABSENT);

		agentGrid = new SparseGrid2D(WORLD_WIDTH, WORLD_HEIGHT);

		agents = new ArrayList<Agent>();

		isToroidalWorld = DEFAULT_IS_TOROIDAL;

		numberOfAgents = DEFAULT_NUMBER_OF_AGENTS;

		numberOfObstacles = DEFAULT_NUMBER_OF_OBSTACLES;

		numberOfResources = DEFAULT_NUMBER_OF_RESOURCES;

		numberOfCollectedResources = 0;

		numberOfCollectionSites = DEFAULT_NUMBER_OF_HOMES;

		trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

		generation = 0;

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

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
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
			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_HEIGHT);
			// make sure there isn't an obstacle there already...
			while (collectionSiteGrid.field[randomX][randomY] == PRESENT) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_HEIGHT);

			}
			collectionSiteGrid.field[randomX][randomY] = PRESENT;
			setupCollisionGrid.field[randomX][randomY] = PRESENT;
			collectionSiteList.add(new Int2D(randomX, randomY));

		}
	}

	private void setupObstacles() {
		// create obstacles in random locations
		for (int i = 0; i < numberOfObstacles; i++) {

			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_HEIGHT);
			// make sure there isn't an obstacle there already...
			while (setupCollisionGrid.field[randomX][randomY] == PRESENT) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_HEIGHT);
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
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_HEIGHT);
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

		for (int i = 0; i < numberOfAgents; i++) {

			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_HEIGHT);

			while (setupCollisionGrid.field[randomX][randomY] == PRESENT) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_HEIGHT);
			}
			setupCollisionGrid.field[randomX][randomY] = PRESENT;

			Agent agent = agentFactory.create(this, SEED_GENERATION_NUMBER, i,
					randomX, randomY);

			agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));

			agents.add(agent);

			// add agents to the scheduler so that they can be stepped
			schedule.scheduleRepeating(agent);

		}

	}

	private void setupNextGeneration() {
		// populate with agents
		generation++;
		for (int i = 0; i < numberOfAgents; i++) {

			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_HEIGHT);

			while (setupCollisionGrid.field[randomX][randomY] == PRESENT) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_HEIGHT);
			}
			setupCollisionGrid.field[randomX][randomY] = PRESENT;

			Agent agent = agents.get(i);
			agent.reset();
			agent.generation = generation;
			agent.x = randomX;
			agent.y = randomY;

			agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));
		}

		D.p("finished population generation #" + generation);
	}

	private void setupSimulation() {

		setupEnvironment();
		setupFirstGeneration();

		// this is run at the end of each step

		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
			public void step(SimState state) {
				statistics.simData.numberOfSteps++;
				// D.p("sim loop");

				// this fades the trails at the appropriate rates
				trailGrid.lowerBound(0.0);
				trailGrid.multiply(trailEvaporationConstant);

				// this handles statistics
				doEndOfStepTasks();

				// this terminates the simulation if, conditions have been
				// reached
				if (evaluateTerminalCondition()) {
					D.p("****terminated***");
					terminateSimulation();
				}
			}

		}, 1);

	}

	protected void debugArray(String string, int[][] array) {
		D.p(string);
		int nonZeroCounter = 0;
		for (int x = 0; x < Constants.WORLD_WIDTH; x++) {
			for (int y = 0; y < Constants.WORLD_HEIGHT; y++) {
				int value = array[x][y];
				System.out.print(value);
				if (value > 0) {
					nonZeroCounter++;
				}
			}
			D.p();
		}
		D.p("total:" + nonZeroCounter);

	}

	protected void terminateSimulation() {
		statistics.takeSimSnapshot();
		resetEnvironment();
		setupEnvironment();
		setupNextGeneration();

		// finish();
	}

	protected boolean evaluateTerminalCondition() {
		return (this.numberOfCollectedResources >= this.numberOfResources);
	}

	protected void doEndOfStepTasks() {
		statistics.takeStepSnapshot();
	}

	public void start() {
		super.start();
		resetAll();
		setupSimulation();
	}

	public static String[] parseArguments(String string) {
		String[] array = string.split(" ");
		return array;
	}

	public static void _main(String[] arg) {
		doLoop(Simulation.class, arg);
		statistics.printExperimentSummary();
		System.exit(0);
	}

	public static void main(String[] arg) {
		_main(arg);
	}

}
