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
@SuppressWarnings("serial")
public abstract class Simulation extends SimState implements Constants {

	protected IntGrid2D obstacleGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			OBSTACLE_FALSE);

	protected IntGrid2D homeGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			HOME_FALSE);

	protected ArrayList<Int2D> homeList = new ArrayList<Int2D>();

	protected IntGrid2D objectGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);

	protected IntGrid2D resourceAGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			RESOURCE_EMPTY);

	protected IntGrid2D resourceBGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			RESOURCE_EMPTY);

	protected IntGrid2D resourceCGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			RESOURCE_EMPTY);

	protected IntGrid2D extractAGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			RESOURCE_EMPTY);

	protected IntGrid2D extractBGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			RESOURCE_EMPTY);

	protected IntGrid2D extractCGrid = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,
			RESOURCE_EMPTY);

	protected DoubleGrid2D trailAGrid = new DoubleGrid2D(GRID_WIDTH,
			GRID_HEIGHT, 0);

	protected DoubleGrid2D trailBGrid = new DoubleGrid2D(GRID_WIDTH,
			GRID_HEIGHT, 0);

	protected DoubleGrid2D trailCGrid = new DoubleGrid2D(GRID_WIDTH,
			GRID_HEIGHT, 0);

	protected SparseGrid2D agentGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
	
	protected IntGrid2D visitedCells = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);
	

	protected int signalALevel = DEFAULT_SIGNAL_LEVEL_A;
	protected int signalBLevel = DEFAULT_SIGNAL_LEVEL_B;
	protected int signalCLevel = DEFAULT_SIGNAL_LEVEL_C;

	protected ArrayList<Agent> agents = new ArrayList<Agent>();

	protected boolean isToroidalWorld = DEFAULT_IS_TOROIDAL;
	
	protected int numberOfExtractedResources = 0;

	public int getNumberOfExtractedResources() {
		return numberOfExtractedResources;
	}

	public void setNumberOfExtractedResources(int numberOfExtractedResources) {
		this.numberOfExtractedResources = numberOfExtractedResources;
	}

	protected int numberOfAgents = DEFAULT_NUMBER_OF_AGENTS;

	protected int numberOfObstacles = DEFAULT_NUMBER_OF_OBSTACLES;

	protected int numberOfResourceA = DEFAULT_NUMBER_OF_RESOURCES_A;

	protected int numberOfResourceB = DEFAULT_NUMBER_OF_RESOURCES_B;

	protected int numberOfResourceC = DEFAULT_NUMBER_OF_RESOURCES_C;

	protected int numberOfResources = numberOfResourceA + numberOfResourceB
			+ numberOfResourceC;

	protected int numberOfHomes = DEFAULT_NUMBER_OF_HOMES;

	protected double broadcastSignalEvaporationConstant = DEFAULT_BROADCAST_EVAPORATION_CONSTANT;

	protected double trailAEvaporationConstant = DEFAULT_TRAIL_A_EVAPORATION_CONSTANT;

	protected double trailBEvaporationConstant = DEFAULT_TRAIL_B_EVAPORATION_CONSTANT;

	protected double trailCEvaporationConstant = DEFAULT_TRAIL_C_EVAPORATION_CONSTANT;

	protected static Statistics statistics = new Statistics();

	protected long generation = 0;

	private void clearEnvironmentDataStructures() {
		obstacleGrid.setTo(OBSTACLE_FALSE);
		homeGrid.setTo(HOME_FALSE);
		homeList.clear();
		objectGrid.setTo(0);
		resourceAGrid.setTo(RESOURCE_EMPTY);
		resourceBGrid.setTo(RESOURCE_EMPTY);
		resourceCGrid.setTo(RESOURCE_EMPTY);

		extractAGrid.setTo(RESOURCE_EMPTY);
		extractBGrid.setTo(RESOURCE_EMPTY);
		extractCGrid.setTo(RESOURCE_EMPTY);

		trailAGrid.setTo(RESOURCE_EMPTY);
		trailBGrid.setTo(RESOURCE_EMPTY);
		trailCGrid.setTo(RESOURCE_EMPTY);
		D.p("environment cleared...");
	}

	private void clearAllDataStructures() {
		clearEnvironmentDataStructures();
		agentGrid.clear();
		D.p("agents cleared...");
	}

	public int getSignalALevel() {
		return signalALevel;
	}

	public void setSignalALevel(int signalALevel) {
		this.signalALevel = signalALevel;
	}

	public int getSignalBLevel() {
		return signalBLevel;
	}

	public void setSignalBLevel(int signalBLevel) {
		this.signalBLevel = signalBLevel;
	}

	public int getSignalCLevel() {
		return signalCLevel;
	}

	public void setSignalCLevel(int signalCLevel) {
		this.signalCLevel = signalCLevel;
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

	public int getNumberOfResourceA() {
		return numberOfResourceA;
	}

	public void setNumberOfResourceA(int numberOfResourceA) {
		this.numberOfResourceA = numberOfResourceA;
		numberOfResources = numberOfResourceA + numberOfResourceB
				+ numberOfResourceC;
	}

	public void setNumberOfResources(int numberOfResources) {
		this.numberOfResources = numberOfResources;
	}

	public int getNumberOfResourceB() {
		return numberOfResourceB;
	}

	public void setNumberOfResourceB(int numberOfResourceB) {
		this.numberOfResourceB = numberOfResourceB;
		numberOfResources = numberOfResourceA + numberOfResourceB
				+ numberOfResourceC;
	}

	public int getNumberOfResourceC() {
		return numberOfResourceC;
	}

	public void setNumberOfResourceC(int numberOfResourceC) {
		this.numberOfResourceC = numberOfResourceC;
		numberOfResources = numberOfResourceA + numberOfResourceB
				+ numberOfResourceC;
	}

	public int getNumberOfHomes() {
		return numberOfHomes;
	}

	public void setNumberOfHomes(int numberOfHomes) {
		this.numberOfHomes = numberOfHomes;
	}

	public double getBroadcastSignalEvaporationConstant() {
		return broadcastSignalEvaporationConstant;
	}

	public void setBroadcastSignalEvaporationConstant(
			double broadcastSignalEvaporationConstant) {
		this.broadcastSignalEvaporationConstant = broadcastSignalEvaporationConstant;
	}

	public int getNumberOfResources() {
		return numberOfResources;
	}

	public Simulation(long seed) {
		super(seed);
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
		// we will create one fixed home location in the center
		homeGrid.field[PRIMARY_HOME_X][PRIMARY_HOME_Y] = HOME_TRUE;
		objectGrid.field[PRIMARY_HOME_X][PRIMARY_HOME_Y] = 1;
		homeList.add(new Int2D(PRIMARY_HOME_X, PRIMARY_HOME_Y));

		// create the rest randomly
		for (int i = 0; i < (numberOfHomes - 1); i++) {
			int randomX = random.nextInt(GRID_WIDTH);
			int randomY = random.nextInt(GRID_HEIGHT);
			// make sure there isn't an obstacle there already...
			while (homeGrid.field[randomX][randomY] == HOME_TRUE) {
				randomX = random.nextInt(GRID_WIDTH);
				randomY = random.nextInt(GRID_HEIGHT);

			}
			homeGrid.field[randomX][randomY] = HOME_TRUE;
			objectGrid.field[randomX][randomY] = 1;
			homeList.add(new Int2D(randomX, randomY));

		}

		// create obstacles in random locations
		for (int i = 0; i < numberOfObstacles; i++) {

			int randomX = random.nextInt(GRID_WIDTH);
			int randomY = random.nextInt(GRID_HEIGHT);
			// make sure there isn't an obstacle there already...
			while (objectGrid.field[randomX][randomY] == 1) {
				randomX = random.nextInt(GRID_WIDTH);
				randomY = random.nextInt(GRID_HEIGHT);
			}
			objectGrid.field[randomX][randomY] = 1;
			obstacleGrid.field[randomX][randomY] = OBSTACLE_TRUE;

		}

		// create resources in random locations
		populateRandomResource(numberOfResourceA, resourceAGrid, extractAGrid);

		populateRandomResource(numberOfResourceB, resourceBGrid, extractBGrid);
		populateRandomResource(numberOfResourceC, resourceCGrid, extractCGrid);

	}

	private void createInitialAgents() {
		// populate with agents

		for (int i = 0; i < numberOfAgents; i++) {

			int randomX = random.nextInt(GRID_WIDTH);
			int randomY = random.nextInt(GRID_HEIGHT);

			while (objectGrid.field[randomX][randomY] == 1) {
				randomX = random.nextInt(GRID_WIDTH);
				randomY = random.nextInt(GRID_HEIGHT);
			}
			objectGrid.field[randomX][randomY] = 1;

			Agent agent = generateAgent(SEED_GENERATION, i, randomX,
					randomY);

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

			int randomX = random.nextInt(GRID_WIDTH);
			int randomY = random.nextInt(GRID_HEIGHT);

			while (objectGrid.field[randomX][randomY] == 1) {
				randomX = random.nextInt(GRID_WIDTH);
				randomY = random.nextInt(GRID_HEIGHT);
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
				trailAGrid.lowerBound(0.0);
				trailAGrid.multiply(trailAEvaporationConstant);
				trailBGrid.lowerBound(0.0);
				trailBGrid.multiply(trailBEvaporationConstant);
				trailCGrid.lowerBound(0.0);
				trailCGrid.multiply(trailCEvaporationConstant);

				// this fades the broadcast signals
				if (signalALevel > 0) {
					signalALevel = (int) ((double) signalALevel * broadcastSignalEvaporationConstant);
				}

				if (signalBLevel > 0) {
					signalBLevel = (int) ((double) signalBLevel * broadcastSignalEvaporationConstant);
				}

				if (signalCLevel > 0) {
					signalCLevel = (int) ((double) signalCLevel * broadcastSignalEvaporationConstant);
				}

				for (Agent agent : agents) {
					if (signalALevel <= 0) {
						agent.signalA.senderAgentId = -1;
						agent.signalA.x = -1;
						agent.signalA.y = -1;

					}
					if (signalBLevel <= 0) {
						agent.signalB.senderAgentId = -1;
						agent.signalB.x = -1;
						agent.signalB.y = -1;
					}
					if (signalCLevel <= 0) {
						agent.signalC.senderAgentId = -1;
						agent.signalC.x = -1;
						agent.signalC.y = -1;
					}
				}

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
		for (int x = 0; x < Constants.GRID_WIDTH; x++) {
			for (int y = 0; y < Constants.GRID_HEIGHT; y++) {
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
			IntGrid2D resourceGrid, IntGrid2D extractGrid) {

		for (int i = 0; i < numberOfResource; i++) {

			int randomX = 0;
			int randomY = 0;

			// make sure there are no resources, homes, and obstacles here
			do {
				randomX = random.nextInt(GRID_WIDTH);
				randomY = random.nextInt(GRID_HEIGHT);
			} while (objectGrid.field[randomX][randomY] == 1);
			resourceGrid.field[randomX][randomY] = RESOURCE_MAX;
			extractGrid.field[randomX][randomY] = RESOURCE_EMPTY;
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
