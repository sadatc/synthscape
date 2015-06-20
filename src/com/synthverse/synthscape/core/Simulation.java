/**
 * 
 */
package com.synthverse.synthscape.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import sim.util.MutableDouble;

import com.synthverse.Main;
import com.synthverse.util.DateUtils;
import com.synthverse.util.GridUtils;
import com.synthverse.util.LogUtils;

import ec.util.MersenneTwisterFast;

/**
 * @author sadat
 * 
 */

public abstract class Simulation extends SimState implements Constants {

	public Settings settings = Settings.getInstance();

	private static final long serialVersionUID = 2700375028430112699L;
	protected static Logger logger = Logger.getLogger(Agent.class.getName());

	protected MersenneTwisterFast controlledRandom = new MersenneTwisterFast(1);

	protected Evolver evolver;

	protected AgentFactory agentFactory;

	protected AgentFactory embodiedAgentFactory;

	protected ExperimentReporter experimentReporter;

	protected long simulationCounter;

	protected long simsRunForThisGeneration;

	protected int simStepCounter;

	protected ProblemComplexity problemComplexity;

	protected SparseGrid2D obstacleGrid;
	protected SparseGrid2D benchmarkObstacleGrid;

	protected SparseGrid2D collectionSiteGrid;
	protected SparseGrid2D benchmarkCollectionSiteGrid;

	protected ArrayList<Int2D> collectionSiteList;
	protected ArrayList<Int2D> benchmarkCollectionSiteList;

	protected SparseGrid2D initCollisionGrid;
	protected SparseGrid2D benchmarkInitCollisionGrid;

	protected SparseGrid2D resourceGrid;
	protected SparseGrid2D benchmarkResourceGrid;

	public ResourceStatus[][] resourceStatusArray;
	public ResourceStatus[][] benchmarkResourceStatusArray;

	public HashSet<ResourceStatus> touchedResources = new HashSet<ResourceStatus>();

	protected TrailGridWrapper trailGridWrapper = new TrailGridWrapper();

	// originally, these peer reward variables were implemented as grids
	// however, the reward was offered in all coordinates... the idea is
	// the reward is offered to all agents with the appropriate traits
	// however, updating grids is a waste of CPU time...
	protected MutableDouble extractorPeerReward;
	protected MutableDouble detectorPeerReward;
	protected MutableDouble processorPeerReward;

	public SparseGrid2D agentGrid;

	public boolean matingEnabled = false;

	public ArrayList<Agent> agents;

	protected boolean isToroidalWorld;

	protected int numberOfObstacles;

	protected int numberOfResources;

	protected int resourceCaptureGoal;

	protected int numberOfCollectedResources;

	protected int maxResourcesEverCollected = 0;

	protected int numberOfCollectionSites;

	protected int gridWidth;

	protected int gridHeight;

	protected String experimentName;
	protected boolean reportEvents = false;
	protected boolean reportPerformance = false;
	protected String serverName;
	protected String batchId;

	protected Date startDate;
	protected Date endDate;

	protected double obstacleDensity;
	protected double resourceDensity;
	protected int clonesPerSpecies;

	protected int maxStepsPerAgent;

	protected int simulationsPerExperiment;
	protected int stepsPerSimulation;

	protected Set<Species> speciesComposition = new TreeSet<Species>(
			new SpeciesComparator());
	protected EnumSet<InteractionMechanism> interactionMechanisms = EnumSet
			.noneOf(InteractionMechanism.class);

	protected String eventFileName;

	public EventStats simEventStats = new EventStats();
	public EventStats experimentEventStats = new EventStats();
	public IntervalStats intervalStats = new IntervalStats();

	private int genePoolSize;

	private SimulationUI uiObject;

	private HashMap<SignalType, Broadcast> registeredBroadcasts = new HashMap<SignalType, Broadcast>();
	private HashMap<SignalType, Broadcast> tmpBroadcasts = new HashMap<SignalType, Broadcast>();

	public SummaryStatistics captureStats = new SummaryStatistics();
	// need this for efficiency

	public ResourceCaptureStats resourceCaptureStats = new ResourceCaptureStats();

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	protected void init() throws Exception {

		settings.experimentNumber++;

		// we can compute the server name and batch ID right away
		try {
			serverName = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			serverName = "LOCAL";
		}
		batchId = Long.toHexString(System.currentTimeMillis());

		// InstructionTranslator.logStatus();

		setGenePoolSize(configGenePoolSize());
		setReportEvents(configIsReportEvents());
		setReportPerformance(configIsReportPerformance());

		setEventFileName(configEventFileName());

		// now set these up based on the concrete simulation

		setExperimentName(configExperimentName());
		setProblemComplexity(configProblemComplexity());

		setMaxStepsPerAgent(configMaxStepsPerAgent());

		// environmental stuff
		setGridWidth(configGridWidth());
		setGridHeight(configGridHeight());
		setNumberOfCollectionSites(configNumberOfCollectionSites());
		setObstacleDensity(configObstacleDensity());
		setResourceDensity(configResourceDensity());

		// interactions
		setInteractionMechanisms(configInteractionMechanisms());

		// species compositions
		setClonesPerSpecies(configClonesPerSpecies());
		setSpeciesComposition(configSpeciesComposition());

		// steps and simulations...
		setStepsPerSimulation(configStepsPerSimulation());
		setSimulationsPerExperiment(configSimulationsPerExperiment());

		// agent factory and evolver...
		setAgentFactory(configAgentFactory());
		setEmbodiedAgentFactory(configEmbodiedAgentFactory());

		setEvolver(configEvolver());

		// init any other dependencies...
		evolver.init();

		// set these variables
		double gridArea = gridWidth * gridHeight;
		numberOfObstacles = (int) (gridArea * obstacleDensity);
		numberOfResources = (int) (gridArea * resourceDensity);
		resourceCaptureGoal = (int) ((double) numberOfResources * settings.PERC_RESOURCE_CAPTURE_GOAL);

		createDataStructures();

		simulationCounter = 0;
		simsRunForThisGeneration = 0;

		numberOfCollectedResources = 0;

		experimentReporter = new ExperimentReporter(this,
				DEFAULT_FLUSH_ALWAYS_FLAG);

		isToroidalWorld = TOROIDAL_FLAG;

	}

	public Simulation(long seed) throws Exception {
		super(seed);
		init();

	}

	protected void createDataStructures() {
		obstacleGrid = new SparseGrid2D(gridWidth, gridHeight);
		benchmarkObstacleGrid = new SparseGrid2D(gridWidth, gridHeight);

		collectionSiteGrid = new SparseGrid2D(gridWidth, gridHeight);
		benchmarkCollectionSiteGrid = new SparseGrid2D(gridWidth, gridHeight);

		collectionSiteList = new ArrayList<Int2D>();
		benchmarkCollectionSiteList = new ArrayList<Int2D>();

		initCollisionGrid = new SparseGrid2D(gridWidth, gridHeight);
		benchmarkInitCollisionGrid = new SparseGrid2D(gridWidth, gridHeight);

		resourceGrid = new SparseGrid2D(gridWidth, gridHeight);
		benchmarkResourceGrid = new SparseGrid2D(gridWidth, gridHeight);

		trailGridWrapper.createNew(gridWidth, gridHeight);

		extractorPeerReward = new MutableDouble();
		detectorPeerReward = new MutableDouble();
		processorPeerReward = new MutableDouble();

		agentGrid = new SparseGrid2D(gridWidth, gridHeight);
		agents = new ArrayList<Agent>();

		resourceStatusArray = new ResourceStatus[gridWidth][gridHeight];
		benchmarkResourceStatusArray = new ResourceStatus[gridWidth][gridHeight];
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				resourceStatusArray[x][y] = new ResourceStatus();
				benchmarkResourceStatusArray[x][y] = new ResourceStatus();
			}
		}

	}

	protected void resetEnvironment() {

		// TODO: try making this obstacleGrid = new SparseGrid2D() as suggested
		obstacleGrid.clear();
		collectionSiteGrid.clear();
		collectionSiteList.clear();
		initCollisionGrid.clear();

		resourceGrid.clear();
		clearResourceStatusArray();

		trailGridWrapper.clear();

		extractorPeerReward.val = 0.0;
		detectorPeerReward.val = 0.0;
		processorPeerReward.val = 0.0;

		registeredBroadcasts.clear();

	}

	public final void collectResouceCaptureStats() {
		if (touchedResources.size() > 0) {
			for (ResourceStatus status : touchedResources) {
				resourceCaptureStats.addData(status);
			}
		}
		resourceCaptureStats.touchedResources.addValue(touchedResources.size());
	}

	final void clearResourceStatusArray() {
		touchedResources.clear();
		// TODO: time this against simply creating a new status array object
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridHeight; y++) {
				resourceStatusArray[x][y].clear();
			}
		}
	}

	protected void resetAll() {
		resetEnvironment();
		agentGrid.clear();
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
		this.resourceCaptureGoal = (int) ((double) numberOfResources * settings.PERC_RESOURCE_CAPTURE_GOAL);

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

	protected void initPrimaryCollectionSite() {
		// set the primary collection site
		GridUtils.set(collectionSiteGrid, settings.PRIMARY_COLLECTION_SITE_X,
				settings.PRIMARY_COLLECTION_SITE_Y, true);
		GridUtils.set(initCollisionGrid, settings.PRIMARY_COLLECTION_SITE_X,
				settings.PRIMARY_COLLECTION_SITE_Y, true);

		collectionSiteList.add(new Int2D(settings.PRIMARY_COLLECTION_SITE_X,
				settings.PRIMARY_COLLECTION_SITE_Y));
	}
	protected void initNonPrimaryCollectionSites() {
		MersenneTwisterFast randomPrime = this.random;

		for (int i = 0; i < (numberOfCollectionSites - 1); i++) {
			int randomX = randomPrime.nextInt(gridWidth);
			int randomY = randomPrime.nextInt(gridHeight);
			// make sure there isn't an obstacle there already...
			while (GridUtils.gridHasAnObjectAt(collectionSiteGrid, randomX,
					randomY)) {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);

			}
			GridUtils.set(collectionSiteGrid, randomX, randomY, true);

			GridUtils.set(initCollisionGrid, randomX, randomY, true);

			collectionSiteList.add(new Int2D(randomX, randomY));

		}
	}

	protected void initObstacles() {
		// create obstacles in random locations
		MersenneTwisterFast randomPrime = this.random;

		for (int i = 0; i < numberOfObstacles; i++) {

			int randomX = randomPrime.nextInt(gridWidth);
			int randomY = randomPrime.nextInt(gridHeight);
			// make sure there isn't an obstacle there already...
			while (GridUtils.gridHasAnObjectAt(initCollisionGrid, randomX,
					randomY)) {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);
			}
			GridUtils.set(initCollisionGrid, randomX, randomY, true);
			GridUtils.set(obstacleGrid, randomX, randomY, true);

		}

	}

	protected void initResources() {

		MersenneTwisterFast randomPrime = this.random;

		for (int i = 0; i < numberOfResources; i++) {

			int randomX = 0;
			int randomY = 0;

			// make sure there are no resources, collectionSites, and obstacles
			// here
			do {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);
			} while (GridUtils.gridHasAnObjectAt(initCollisionGrid, randomX,
					randomY));
			GridUtils.set(resourceGrid, randomX, randomY, ResourceState.RAW);

			resourceStatusArray[randomX][randomY].state = ResourceState.RAW;
			resourceStatusArray[randomX][randomY].x = randomX;
			resourceStatusArray[randomX][randomY].y = randomY;

			GridUtils.set(initCollisionGrid, randomX, randomY, true);

		}

	}

	final void resetCollectionCounts() {
		this.numberOfCollectedResources = 0;
	}

	protected void initEnvironment() {
		initPrimaryCollectionSite();
		initNonPrimaryCollectionSites();
		initObstacles();
		initResources();
		resetCollectionCounts();
	}

	public static ResourceStatus[][] copyResourceStatusArray(
			ResourceStatus[][] input) {
		if (input == null)
			return null;
		ResourceStatus[][] result = new ResourceStatus[input.length][];
		for (int r = 0; r < input.length; r++) {
			result[r] = input[r].clone();
		}
		return result;
	}

	protected void saveEnvironmentForBenchmark() {

		benchmarkObstacleGrid = new SparseGrid2D(obstacleGrid);
		benchmarkCollectionSiteGrid = new SparseGrid2D(collectionSiteGrid);
		benchmarkInitCollisionGrid = new SparseGrid2D(initCollisionGrid);
		benchmarkResourceGrid = new SparseGrid2D(resourceGrid);
		benchmarkResourceStatusArray = copyResourceStatusArray(resourceStatusArray);

		benchmarkCollectionSiteList.clear();
		for (Int2D coordinate : collectionSiteList) {
			benchmarkCollectionSiteList.add(coordinate);
		}
	}

	protected void initEnvironmentWithBenchmark() {

		obstacleGrid = new SparseGrid2D(benchmarkObstacleGrid);
		collectionSiteGrid = new SparseGrid2D(benchmarkCollectionSiteGrid);
		initCollisionGrid = new SparseGrid2D(benchmarkInitCollisionGrid);
		resourceGrid = new SparseGrid2D(benchmarkResourceGrid);
		resourceStatusArray = copyResourceStatusArray(benchmarkResourceStatusArray);

		collectionSiteList.clear();
		for (Int2D coordinate : benchmarkCollectionSiteList) {
			collectionSiteList.add(coordinate);
		}
		resetCollectionCounts();
	}

	protected void initAgents() {
		logger.info("Simulation.initAgents()");
		// populate with agents

		for (Agent agent : agents) {
			agent.eventStats.clear();
		}

		MersenneTwisterFast randomPrime = this.random;
		agents.clear();

		int previousX = 0;
		int previousY = 0;

		for (Species species : speciesComposition) {
			for (int i = 0; i < clonesPerSpecies; i++) {

				int randomX = randomPrime.nextInt(gridWidth);
				int randomY = randomPrime.nextInt(gridHeight);

				if (!settings.CLUSTERED) {
					while (GridUtils.gridHasAnObjectAt(initCollisionGrid,
							randomX, randomY)) {
						randomX = randomPrime.nextInt(gridWidth);
						randomY = randomPrime.nextInt(gridHeight);
					}
				}

				if (settings.CLUSTERED) {
					Int2D clusterLocation = getClusterLocation(i, previousX,
							previousY);
					randomX = clusterLocation.x;
					randomY = clusterLocation.y;

				}

				GridUtils.set(initCollisionGrid, randomX, randomY, true);

				previousX = randomX;
				previousY = randomY;

				Agent agent = evolver.getAgent(species, randomX, randomY);

				agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));
				agents.add(agent);

				// add agents to the scheduler

				if (!agent.isScheduled()) {
					schedule.scheduleRepeating(agent);

					agent.setScheduled(true);
				}

			}
		}

	}

	protected Int2D getClusterLocation(int agentNum, int pX, int pY) {

		MersenneTwisterFast randomPrime = this.random;

		int randomX = pX;
		int randomY = pY;

		if (agentNum == 0) {
			// ignore previous X and Y and just generate new location

			randomX = randomPrime.nextInt(gridWidth);
			randomY = randomPrime.nextInt(gridHeight);

			while (GridUtils.gridHasAnObjectAt(initCollisionGrid, randomX,
					randomY)) {
				randomX = randomPrime.nextInt(gridWidth);
				randomY = randomPrime.nextInt(gridHeight);
			}
		} else {
			// find something close to previous X and Y
			// we'll look at the 8 locations: N, NW, W, SW, S, SE, E, NE
			// if anything is empty, we return it.
			// if this fails, we move once cell north and repeat process..

			if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX), agentGrid.sty(pY - 1))) {

				// N
				randomY = agentGrid.sty(pY - 1);
			}

			else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX - 1), agentGrid.sty(pY - 1))) {
				// NW
				randomX = agentGrid.stx(pX - 1);
				randomY = agentGrid.sty(pY - 1);
			}

			else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX - 1), agentGrid.sty(pY))) {
				// W
				randomX = agentGrid.stx(pX - 1);
			}

			else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX - 1), agentGrid.sty(pY + 1))) {
				// SW
				randomX = agentGrid.stx(pX - 1);
				randomY = agentGrid.sty(pY + 1);
			}

			else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX), agentGrid.sty(pY + 1))) {
				// S
				randomY = agentGrid.sty(pY + 1);
			}

			else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX + 1), agentGrid.sty(pY + 1))) {
				// SE
				randomX = agentGrid.stx(pX + 1);
				randomY = agentGrid.sty(pY + 1);
			} else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX + 1), agentGrid.sty(pY))) {
				// E
				randomX = agentGrid.stx(pX + 1);

			}

			else if (!GridUtils.gridHasAnObjectAt(initCollisionGrid,
					agentGrid.stx(pX + 1), agentGrid.sty(pY - 1))) {
				// NE
				randomX = agentGrid.stx(pX + 1);
				randomY = agentGrid.sty(pY - 1);

			} else {
				// all of these failed...move north and repeat process by moving
				// north
				Int2D secondTry = getClusterLocation(agentNum, pX,
						agentGrid.sty(pY - 1));
				randomX = secondTry.x;
				randomY = secondTry.y;
			}

		}

		Int2D result = new Int2D(randomX, randomY);
		return result;
	}

	protected void fadeTrails() {
		trailGridWrapper.fadeTrails();
	}

	protected void fadeRewardGrids() {

		if (extractorPeerReward.val > 0) {
			extractorPeerReward.val *= DEFAULT_REWARD_EVAPORATION_CONSTANT;

			// this will get rid of fractions
			extractorPeerReward.val = (int) extractorPeerReward.val;

		}
		if (extractorPeerReward.val < 0) {
			extractorPeerReward.val = 0;
		}

		if (detectorPeerReward.val > 0) {
			detectorPeerReward.val *= DEFAULT_REWARD_EVAPORATION_CONSTANT;

			// this will get rid of fractions
			detectorPeerReward.val = (int) detectorPeerReward.val;
		}

		if (detectorPeerReward.val < 0) {
			detectorPeerReward.val = 0;
		}

		if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
			if (processorPeerReward.val < 0) {
				processorPeerReward.val *= DEFAULT_REWARD_EVAPORATION_CONSTANT;

				// this will get rid of fractions
				detectorPeerReward.val = (int) detectorPeerReward.val;
			}
			if (processorPeerReward.val < 0) {
				processorPeerReward.val = 0;
			}
		}

	}

	protected void ageBroadcasts() {

		if (!registeredBroadcasts.isEmpty()) {
			tmpBroadcasts.clear();

			// first age all of them...
			for (Broadcast broadcast : registeredBroadcasts.values()) {
				int stepClock = broadcast.getStepClock();
				stepClock++;
				if (stepClock <= Constants.NUMBER_OF_STEPS_FOR_BROADCASTED_SIGNAL) {
					broadcast.setStepClock(stepClock);
					tmpBroadcasts.put(broadcast.getSignalType(), broadcast);
				}
			}
			// then add them all...
			registeredBroadcasts.clear();
			if (!tmpBroadcasts.isEmpty()) {
				registeredBroadcasts.putAll(tmpBroadcasts);
			}

		}
	}

	protected void startSimulation() {

		logger.info("EXPERIMENT STARTS: expected maxium simulations ="
				+ simulationsPerExperiment + " stepsPerSimulation="
				+ stepsPerSimulation);

		initEnvironment();
		saveEnvironmentForBenchmark();
		initAgents();

		logger.info("---- starting simulation (" + simulationCounter
				+ ") with: world=" + (gridHeight * gridWidth) + " obstacles="
				+ numberOfObstacles + " sites=" + numberOfCollectionSites
				+ " resources=" + numberOfResources + " agents="
				+ agents.size());

		setStartDate();

		// this synchronizes the seed value for repeated experiments
		settings.SEED = (int) this.seed();

		experimentReporter.initReporter();

		// this is run at the end of each step
		schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
			/**
	     * 
	     */
			private static final long serialVersionUID = -935999426847054013L;

			public void step(SimState state) {

				simStepCounter++;

				if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
					fadeTrails();
				}

				if (Main.settings.PEER_REWARDS) {
					fadeRewardGrids();
				}

				if (interactionMechanisms
						.contains(InteractionMechanism.BROADCAST)) {
					ageBroadcasts();
				}

				doEndOfStepTasks();

				// check if simulation should continue...

				if (evaluateSimulationTerminateCondition()) {

					doEndOfSimulationTasks();

					simStepCounter = 0;
					simulationCounter++;
					simsRunForThisGeneration++;

					if (!collectedAllResources()
							&& simulationCounter < simulationsPerExperiment) {

						if (simulationCounter % settings.GENE_POOL_SIZE == 0) {
							evolver.evolve();
							simsRunForThisGeneration = 0;
						}

						startNextSimulation();

					} else {
						// end of experiment...
						if (collectedAllResources()) {
							logger.info("!!!ALL RESOURCES COLLECTED!!!");
						}
						setEndDate();
						experimentReporter.cleanupReporter();
						finish();
						logger.info("<=====  EXPERIMENT ENDS\n");
						// settings.resetRandomSeedIfNeeded(state);
					}
				}
			}

		}, 1);

	}

	protected void setProblemComplexity(ProblemComplexity problemComplexity) {
		this.problemComplexity = problemComplexity;

	}

	protected void setExperimentName(String experimentName) {
		this.experimentName = experimentName;

	}

	protected void startNextSimulation() {

		intervalStats.resetLastSteps();
		simEventStats.clear();
		resetEnvironment();

		if (settings.generationCounter != 0
				&& (settings.generationCounter % settings.BENCHMARK_GENERATION) == 0) {
			// every BENCHMARK_GENERATION use benchmark environment...
			initEnvironmentWithBenchmark();
		} else {
			initEnvironment();
		}
		initAgents();

	}

	protected boolean evaluateSimulationTerminateCondition() {

		boolean result = (this.numberOfCollectedResources >= this.resourceCaptureGoal)
				|| (this.simStepCounter > stepsPerSimulation);

		return result;
	}

	protected boolean collectedAllResources() {
		boolean result = (this.numberOfCollectedResources >= this.resourceCaptureGoal);
		return result;
	}

	abstract protected void doEndOfStepTasks();

	protected void reclaimAgents() {
		for (Agent agent : agents) {
			agentFactory.reclaimAgent(agent);
		}
	}

	abstract protected void doEndOfSimulationTasks();

	public void start() {
		super.start();

		this.simulationCounter = 0;
		this.simStepCounter = 0;
		resetAll();
		startSimulation();
	}

	public void recordEvent(Agent agent, Event event, long source,
			long destination) {

		agent.eventStats.recordValue(event);

		// D.p("Gen:"+agent.getGeneration()+" Sim:"+simulationCounter+" Step:"+simStepCounter+" EVENT: "+event);
		intervalStats.recordValue(event, agent.getSpecies(), simStepCounter);

		if (isReportEvents()) {
			experimentReporter.reportEvent(simulationCounter,
					agent.getGeneration(), agent.getSpecies(),
					agent.getAgentId(), simStepCounter, agent.getX(),
					agent.getY(), event, source, destination);
		}

	}

	public void setStartDate() {
		this.startDate = new java.util.Date();
	}

	public void setEndDate() {
		this.endDate = new java.util.Date();
	}

	public String getSpeciesCompositionString() {
		StringBuilder sb = new StringBuilder();

		for (Species species : speciesComposition) {
			sb.append(species.getId());
		}

		return sb.toString();

	}

	public String getInteractionMechanismsString() {
		StringBuilder sb = new StringBuilder();

		for (InteractionMechanism mechanism : interactionMechanisms) {
			sb.append(mechanism.getId());
		}

		return sb.toString();

	}

	public String getEventFileName() {

		// modify the file name to contain the id
		if (eventFileName != null) {

			if (eventFileName.indexOf(".") != -1) {
				String prePart = eventFileName.substring(0,
						eventFileName.lastIndexOf('.'));
				String postPart = eventFileName.substring(eventFileName
						.lastIndexOf('.'));
				eventFileName = prePart + "_"
						+ DateUtils.getFileNameDateStamp() + "_" + this.job()
						+ postPart;

			} else {
				eventFileName += "_" + batchId + "_" + this.job();
			}

		}

		return eventFileName;
	}

	public void addInteractionMechanism(
			InteractionMechanism interactionMechanism) {
		this.interactionMechanisms.add(interactionMechanism);
	}

	public void addSpecies(Species species) {
		this.speciesComposition.add(species);
	}

	public abstract int configGridWidth();

	public abstract int configGridHeight();

	public abstract double configObstacleDensity();

	public abstract double configResourceDensity();

	public abstract Set<Species> configSpeciesComposition();

	public abstract EnumSet<InteractionMechanism> configInteractionMechanisms();

	public abstract ProblemComplexity configProblemComplexity();

	public abstract int configClonesPerSpecies();

	public abstract int configNumberOfCollectionSites();

	public abstract int configMaxStepsPerAgent();

	public abstract boolean configIsReportEvents();

	public abstract boolean configIsReportPerformance();

	public abstract String configExperimentName();

	public abstract int configSimulationsPerExperiment();

	public abstract int configStepsPerSimulation();

	public abstract String configEventFileName();

	public abstract Evolver configEvolver();

	public abstract int configGenePoolSize();

	public abstract AgentFactory configAgentFactory();

	public abstract AgentFactory configEmbodiedAgentFactory();

	public Evolver getEvolver() {
		return evolver;
	}

	public void setEvolver(Evolver evolver) {
		this.evolver = evolver;
	}

	public AgentFactory getAgentFactory() {
		return agentFactory;
	}

	public void setAgentFactory(AgentFactory agentFactory) {
		this.agentFactory = agentFactory;
	}

	public boolean isReportEvents() {
		return reportEvents;
	}

	public void setReportEvents(boolean reportEvents) {
		this.reportEvents = reportEvents;
	}

	public String getBatchId() {
		return batchId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public double getObstacleDensity() {
		return obstacleDensity;
	}

	public void setObstacleDensity(double obstacleDensity) {
		this.obstacleDensity = obstacleDensity;
	}

	public double getResourceDensity() {
		return resourceDensity;
	}

	public void setResourceDensity(double resourceDensity) {
		this.resourceDensity = resourceDensity;
	}

	public int getClonesPerSpecies() {
		return clonesPerSpecies;
	}

	public void setClonesPerSpecies(int ClonesPerSpecies) {
		this.clonesPerSpecies = ClonesPerSpecies;
	}

	public int getMaxStepsPerAgent() {
		return maxStepsPerAgent;
	}

	public void setMaxStepsPerAgent(int maxStepsPerAgent) {
		this.maxStepsPerAgent = maxStepsPerAgent;
	}

	public int getSimulationsPerExperiment() {
		return simulationsPerExperiment;
	}

	public void setSimulationsPerExperiment(int simulationsPerExperiment) {
		this.simulationsPerExperiment = simulationsPerExperiment;
	}

	public int getStepsPerSimulation() {
		return stepsPerSimulation;
	}

	public void setStepsPerSimulation(int stepsPerSimulation) {
		this.stepsPerSimulation = stepsPerSimulation;
	}

	public Set<Species> getSpeciesComposition() {
		return speciesComposition;
	}

	public void setSpeciesComposition(Set<Species> speciesComposition) {
		this.speciesComposition = speciesComposition;
	}

	public EnumSet<InteractionMechanism> getInteractionMechanisms() {
		return interactionMechanisms;
	}

	public void setInteractionMechanisms(
			EnumSet<InteractionMechanism> interactionMechanisms) {
		this.interactionMechanisms = interactionMechanisms;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	public void setEventFileName(String eventFileName) {
		this.eventFileName = eventFileName;
	}

	public List<Agent> getAgents() {
		return agents;
	}

	public int getGenePoolSize() {
		return genePoolSize;
	}

	public void setGenePoolSize(int genePoolSize) {
		this.genePoolSize = genePoolSize;
	}

	public boolean isReportPerformance() {
		return reportPerformance;
	}

	public void setReportPerformance(boolean reportPerformance) {
		this.reportPerformance = reportPerformance;
	}

	public SimulationUI getUiObject() {
		return uiObject;
	}

	public void setUiObject(SimulationUI uiObject) {
		this.uiObject = uiObject;
	}

	public void registerBroadcast(Broadcast broadcast) {

		if (Main.settings.INTERACTION_QUALITY == InteractionQuality.HIGHEST) {
			SignalType signalType = broadcast.getSignalType();
			registeredBroadcasts.put(signalType, broadcast);
		} else {
			InteractionQuality interactionQualitySetting = Main.settings.INTERACTION_QUALITY;
			if (random.nextDouble() <= interactionQualitySetting.getLevel()) {
				SignalType signalType = broadcast.getSignalType();
				registeredBroadcasts.put(signalType, broadcast);
			}
		}

	}

	public Broadcast getRegisteredBroadcast(SignalType signalType) {
		Broadcast result = null;
		if (registeredBroadcasts.containsKey(signalType)) {
			result = registeredBroadcasts.get(signalType);

		}
		return result;
	}

	public AgentFactory getEmbodiedAgentFactory() {
		return embodiedAgentFactory;
	}

	public void setEmbodiedAgentFactory(AgentFactory embodiedAgentFactory) {
		this.embodiedAgentFactory = embodiedAgentFactory;
	}

}
