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
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

/**
 * @author sadat
 * 
 */

public abstract class Simulation extends SimState implements Constants {

	private static final long serialVersionUID = 2700375028430112699L;

	protected IntGrid2D obstacleGrid;

	protected IntGrid2D collectionSiteGrid;

	protected ArrayList<Int2D> collectionSiteList;

	protected IntGrid2D objectGrid;

	protected IntGrid2D resourceGrid;

	protected IntGrid2D extractedResourceGrid;

	protected IntGrid2D processedResourceGrid;

	protected DoubleGrid2D trailGrid;

	protected SparseGrid2D agentGrid;

	protected IntGrid2D refactorGrid;

	protected ArrayList<Agent> agents;

	protected boolean isToroidalWorld;

	protected int numberOfExtractedResources;

	protected int numberOfAgents;

	protected int numberOfObstacles;

	protected int numberOfResources;

	protected int numberOfHomes;

	protected double trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

	protected static Statistics statistics;

	protected long generation;
	
	

	static {
		statistics = new Statistics();
	}

	public Simulation(long seed) {
		super(seed);
		initStructures();

	}

	private void initStructures() {

		obstacleGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, ABSENT);

		collectionSiteGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, ABSENT);

		collectionSiteList = new ArrayList<Int2D>();

		objectGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, 0);

		resourceGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, ABSENT);

		extractedResourceGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, ABSENT);

		processedResourceGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, ABSENT);

		trailGrid = new DoubleGrid2D(WORLD_WIDTH, WORLD_LENGTH, 0);

		agentGrid = new SparseGrid2D(WORLD_WIDTH, WORLD_LENGTH);

		refactorGrid = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH, 0);

		agents = new ArrayList<Agent>();

		isToroidalWorld = DEFAULT_IS_TOROIDAL;

		numberOfExtractedResources = 0;

		numberOfAgents = DEFAULT_NUMBER_OF_AGENTS;

		numberOfObstacles = DEFAULT_NUMBER_OF_OBSTACLES;

		numberOfResources = DEFAULT_NUMBER_OF_RESOURCES;

		numberOfHomes = DEFAULT_NUMBER_OF_HOMES;

		trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

		generation = 0;

	}

	public int getNumberOfExtractedResources() {
		return numberOfExtractedResources;
	}

	public void setNumberOfExtractedResources(int numberOfExtractedResources) {
		this.numberOfExtractedResources = numberOfExtractedResources;
	}

	private void clearEnvironmentDataStructures() {
		obstacleGrid.setTo(ABSENT);
		collectionSiteGrid.setTo(ABSENT);
		collectionSiteList.clear();
		objectGrid.setTo(0);

		resourceGrid.setTo(ABSENT);
		extractedResourceGrid.setTo(ABSENT);
		processedResourceGrid.setTo(ABSENT);

		trailGrid.setTo(ABSENT);

		D.p("environment cleared...");
	}

	private void clearAllDataStructures() {
		clearEnvironmentDataStructures();
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

	public int getNumberOfHomes() {
		return numberOfHomes;
	}

	public void setNumberOfHomes(int numberOfHomes) {
		this.numberOfHomes = numberOfHomes;
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

	public abstract Agent generateAgent(long generation, long agentId, int x,
			int y);

	private void setupEnvironmentGrids() {
		// we will create one fixed collectionSite location in the center
		collectionSiteGrid.field[PRIMARY_COLLECTION_SITE_X][PRIMARY_COLLECTION_SITE_Y] = PRESENT;
		objectGrid.field[PRIMARY_COLLECTION_SITE_X][PRIMARY_COLLECTION_SITE_Y] = 1;
		collectionSiteList.add(new Int2D(PRIMARY_COLLECTION_SITE_X,
				PRIMARY_COLLECTION_SITE_Y));

		// create the rest randomly
		for (int i = 0; i < (numberOfHomes - 1); i++) {
			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_LENGTH);
			// make sure there isn't an obstacle there already...
			while (collectionSiteGrid.field[randomX][randomY] == PRESENT) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_LENGTH);

			}
			collectionSiteGrid.field[randomX][randomY] = PRESENT;
			objectGrid.field[randomX][randomY] = 1;
			collectionSiteList.add(new Int2D(randomX, randomY));

		}

		// create obstacles in random locations
		for (int i = 0; i < numberOfObstacles; i++) {

			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_LENGTH);
			// make sure there isn't an obstacle there already...
			while (objectGrid.field[randomX][randomY] == 1) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_LENGTH);
			}
			objectGrid.field[randomX][randomY] = 1;
			obstacleGrid.field[randomX][randomY] = ABSENT;

		}

		// create resources in random locations
		populateRandomResource(numberOfResources, resourceGrid,
				extractedResourceGrid, processedResourceGrid);

	}

	private void createInitialAgents() {
		// populate with agents

		for (int i = 0; i < numberOfAgents; i++) {

			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_LENGTH);

			while (objectGrid.field[randomX][randomY] == 1) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_LENGTH);
			}
			objectGrid.field[randomX][randomY] = 1;

			Agent agent = generateAgent(SEED_GENERATION, i, randomX, randomY);

			agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));

			agents.add(agent);

			// add agents to the scheduler so that they can be stepped
			schedule.scheduleRepeating(agent);

		}

	}

	private void populateNextGeneration() {
		// populate with agents
		generation++;
		for (int i = 0; i < numberOfAgents; i++) {

			int randomX = random.nextInt(WORLD_WIDTH);
			int randomY = random.nextInt(WORLD_LENGTH);

			while (objectGrid.field[randomX][randomY] == 1) {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_LENGTH);
			}
			objectGrid.field[randomX][randomY] = 1;

			Agent agent = agents.get(i);
			agent.reset();
			agent.generation = generation;
			agent.x = randomX;
			agent.y = randomY;

			agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));
		}

		D.p("finished population generation #" + generation);
	}

	private void initGrids() {

		setupEnvironmentGrids();
		createInitialAgents();

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
					terminateSimulation();
				}
			}

		}, 1);

	}

	protected void debugArray(String string, int[][] array) {
		D.p(string);
		int nonZeroCounter = 0;
		for (int x = 0; x < Constants.WORLD_WIDTH; x++) {
			for (int y = 0; y < Constants.WORLD_LENGTH; y++) {
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
		clearEnvironmentDataStructures();
		setupEnvironmentGrids();
		populateNextGeneration();

		// finish();
	}

	protected boolean evaluateTerminalCondition() {
		return (statistics.simData.resourceCaptures >= this.numberOfResources);
	}

	protected void doEndOfStepTasks() {
		statistics.takeStepSnapshot();
	}

	private void populateRandomResource(int numberOfResource,
			IntGrid2D resourceGrid, IntGrid2D extractGrid,
			IntGrid2D processedGrid) {

		for (int i = 0; i < numberOfResource; i++) {

			int randomX = 0;
			int randomY = 0;

			// make sure there are no resources, collectionSites, and obstacles here
			do {
				randomX = random.nextInt(WORLD_WIDTH);
				randomY = random.nextInt(WORLD_LENGTH);
			} while (objectGrid.field[randomX][randomY] == 1);
			resourceGrid.field[randomX][randomY] = PRESENT;
			extractGrid.field[randomX][randomY] = ABSENT;
			processedGrid.field[randomX][randomY] = ABSENT;
			objectGrid.field[randomX][randomY] = 1;

		}

	}

	public void start() {
		super.start();
		clearAllDataStructures();
		initGrids();
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
