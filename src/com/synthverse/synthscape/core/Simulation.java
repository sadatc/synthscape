/**
 * 
 */
package com.synthverse.synthscape.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

import com.synthverse.evolver.core.Evolver;
import com.synthverse.util.DateUtils;

/**
 * @author sadat
 * 
 */

public abstract class Simulation extends SimState implements Constants {

    private static final long serialVersionUID = 2700375028430112699L;

    protected Evolver evolver;

    private AgentFactory agentFactory;

    protected ExperimentReporter experimentReporter;

    protected int simulationCounter;

    protected int generationCounter;

    protected int stepCounter;

    protected ProblemComplexity problemComplexity;

    protected IntGrid2D obstacleGrid;

    protected IntGrid2D collectionSiteGrid;

    protected ArrayList<Int2D> collectionSiteList;

    protected IntGrid2D initCollisionGrid;

    protected ObjectGrid2D resourceGrid;

    protected DoubleGrid2D trailGrid;

    protected SparseGrid2D agentGrid;

    protected ArrayList<Agent> agents;

    protected boolean isToroidalWorld;

    protected int numberOfObstacles;

    protected int numberOfResources;

    protected int numberOfCollectedResources;

    protected int numberOfCollectionSites;

    protected double trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

    private int gridWidth;

    private int gridHeight;

    private String experimentName;
    private boolean recordExperiment = false;
    private String serverName;
    private String batchId;

    private Date startDate;
    private Date endDate;

    private double obstacleDensity;
    private double resourceDensity;
    private int numberOfAgentsPerSpecies;

    private int maxStepsPerAgent;

    private int simulationsPerExperiment;
    private int stepsPerSimulation;

    private Set<Species> speciesComposition = new LinkedHashSet<Species>();
    private Set<InteractionMechanism> interactionMechanisms = new LinkedHashSet<InteractionMechanism>();

    private String eventFileName;
    
    public Stats stepStats = new Stats();
    public List<Stats> stepStatsList = new ArrayList<Stats>();
    public Stats simStats = new Stats();
    


    private void init() {
	// we can compute the server name and batch ID right away
	try {
	    serverName = java.net.InetAddress.getLocalHost().getHostName();
	} catch (Exception e) {
	    serverName = "LOCAL";
	}
	batchId = Long.toHexString(System.currentTimeMillis());
	setRecordExperiment(configIsRecordExperiment());
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
	setNumberOfAgentsPerSpecies(configNumberOfAgentsPerSpecies());
	setSpeciesComposition(configSpeciesComposition());

	// steps and simulations...
	setStepsPerSimulation(configStepsPerSimulation());
	setSimulationsPerExperiment(configSimulationsPerExperiment());

	// agent factory and evolver...
	setAgentFactory(configAgentFactory());
	setEvolver(configEvolver());

	// init any other dependencies...
	evolver.init();

    }

    public Simulation(long seed) throws Exception {
	super(seed);
	init();

	double gridArea = gridWidth * gridHeight;
	numberOfObstacles = (int) (gridArea * obstacleDensity);
	numberOfResources = (int) (gridArea * resourceDensity);

	createDataStructures();

	simulationCounter = 0;
	generationCounter = 0;
	numberOfCollectedResources = 0;

	experimentReporter = new ExperimentReporter(this,
		DEFAULT_FLUSH_ALWAYS_FLAG);

	isToroidalWorld = TOROIDAL_FLAG;
	trailEvaporationConstant = DEFAULT_TRAIL_EVAPORATION_CONSTANT;

    }

    private void createDataStructures() {
	obstacleGrid = new IntGrid2D(gridWidth, gridHeight, ABSENT);
	collectionSiteGrid = new IntGrid2D(gridWidth, gridHeight, ABSENT);
	collectionSiteList = new ArrayList<Int2D>();
	initCollisionGrid = new IntGrid2D(gridWidth, gridHeight, ABSENT);
	resourceGrid = new ObjectGrid2D(gridWidth, gridHeight);
	trailGrid = new DoubleGrid2D(gridWidth, gridHeight, ABSENT);
	agentGrid = new SparseGrid2D(gridWidth, gridHeight);
	agents = new ArrayList<Agent>();
    }

    private void resetEnvironment() {

	obstacleGrid.setTo(ABSENT);
	collectionSiteGrid.setTo(ABSENT);
	collectionSiteList.clear();
	initCollisionGrid.setTo(ABSENT);

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

    private void initPrimaryCollectionSite() {
	// set the primary collection site
	collectionSiteGrid.field[PRIMARY_COLLECTION_SITE_X][PRIMARY_COLLECTION_SITE_Y] = PRESENT;
	initCollisionGrid.field[PRIMARY_COLLECTION_SITE_X][PRIMARY_COLLECTION_SITE_Y] = PRESENT;
	collectionSiteList.add(new Int2D(PRIMARY_COLLECTION_SITE_X,
		PRIMARY_COLLECTION_SITE_Y));
    }

    private void initNonPrimaryCollectionSites() {
	for (int i = 0; i < (numberOfCollectionSites - 1); i++) {
	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);
	    // make sure there isn't an obstacle there already...
	    while (collectionSiteGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);

	    }
	    collectionSiteGrid.field[randomX][randomY] = PRESENT;
	    initCollisionGrid.field[randomX][randomY] = PRESENT;
	    collectionSiteList.add(new Int2D(randomX, randomY));

	}
    }

    private void initObstacles() {
	// create obstacles in random locations
	for (int i = 0; i < numberOfObstacles; i++) {

	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);
	    // make sure there isn't an obstacle there already...
	    while (initCollisionGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    }
	    initCollisionGrid.field[randomX][randomY] = PRESENT;
	    obstacleGrid.field[randomX][randomY] = PRESENT;

	}

    }

    private void initResources() {
	for (int i = 0; i < numberOfResources; i++) {

	    int randomX = 0;
	    int randomY = 0;

	    // make sure there are no resources, collectionSites, and obstacles
	    // here
	    do {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    } while (initCollisionGrid.field[randomX][randomY] == PRESENT);
	    resourceGrid.field[randomX][randomY] = ResourceState.RAW;
	    initCollisionGrid.field[randomX][randomY] = PRESENT;

	}
	this.numberOfCollectedResources = 0;

    }

    private void initEnvironment() {
	initPrimaryCollectionSite();
	initNonPrimaryCollectionSites();
	initObstacles();
	initResources();
    }

    private void initFirstGeneration() {
	// populate with agents

	for (Species species : speciesComposition) {
	    for (int i = 0; i < numberOfAgentsPerSpecies; i++) {

		int randomX = random.nextInt(gridWidth);
		int randomY = random.nextInt(gridHeight);

		while (initCollisionGrid.field[randomX][randomY] == PRESENT) {
		    randomX = random.nextInt(gridWidth);
		    randomY = random.nextInt(gridHeight);
		}
		initCollisionGrid.field[randomX][randomY] = PRESENT;

		/*
		 * Agent agent = agentFactory.createFactoryAgent(this, species,
		 * SEED_GENERATION_NUMBER, i, experiment.getMaxStepsPerAgent(),
		 * randomX, randomY);
		 */

		Agent agent = evolver.getSeedAgent(species, randomX, randomY);

		agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));

		agents.add(agent);

		// add agents to the scheduler so that they can be stepped
		schedule.scheduleRepeating(agent);

	    }
	}

    }

    private void initNextGeneration() {
	// populate with agents
	generationCounter++;
	evolver.generateNextGeneration();

	for (Agent agent : agents) {

	    int randomX = random.nextInt(gridWidth);
	    int randomY = random.nextInt(gridHeight);

	    while (initCollisionGrid.field[randomX][randomY] == PRESENT) {
		randomX = random.nextInt(gridWidth);
		randomY = random.nextInt(gridHeight);
	    }
	    initCollisionGrid.field[randomX][randomY] = PRESENT;

	    /*
	     * agent.reset(); agent.generation = generationCounter; agent.x =
	     * randomX; agent.y = randomY;
	     */
	    agent = evolver.getEvolvedAgent(agent, randomX, randomY);

	    agentGrid.setObjectLocation(agent, new Int2D(randomX, randomY));
	}

	D.p("finished population generation #" + generationCounter);
    }

    private void startSimulation() {

	initEnvironment();
	initFirstGeneration();

	setStartDate();
	experimentReporter.initReporter();

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
		    doEndOfSimulationTasks();

		    stepCounter = 0;
		    simulationCounter++;

		    if (simulationCounter < simulationsPerExperiment) {

			startNextSimulation();
		    } else {
			D.p("**** end of experiment ***");
			setEndDate();
			experimentReporter.cleanupReporter();
			finish();
		    }
		}
	    }

	}, 1);

    }

    private void setProblemComplexity(ProblemComplexity problemComplexity) {
	this.problemComplexity = problemComplexity;

    }

    private void setExperimentName(String experimentName) {
	this.experimentName = experimentName;

    }

    protected void startNextSimulation() {
	resetEnvironment();
	initEnvironment();
	initNextGeneration();

    }

    protected boolean evaluateSimulationTerminateCondition() {
	return (this.numberOfCollectedResources >= this.numberOfResources || this.stepCounter > stepsPerSimulation);
    }

    protected void doEndOfStepTasks() {
	// aggregate step stats...
	//D.p(">>> End of step");

	for (Agent agent : agents) {
	    agent.agentStats.aggregateStatsTo(stepStats);
	}
	simStats.aggregateStatsTo(stepStats);
	//stepStats.printValues();
	stepStats.clear();
	
    }

    protected void doEndOfSimulationTasks() {
	// aggregate sim stats
	//D.p(">>> End of sim");
	//simStats.printValues();
	simStats.clear();
	
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

    public void reportEvent(Agent agent, Event event, String source,
	    String destination) {

	agent.agentStats.recordValue(event);

	experimentReporter.reportEvent(simulationCounter, generationCounter,
		agent.getSpecies(), agent.getAgentId(), stepCounter,
		agent.getX(), agent.getY(), event, source, destination);

    }

    public void setStartDate() {
	this.startDate = new java.util.Date();
    }

    public void setEndDate() {
	this.endDate = new java.util.Date();
    }

    public String getSpeciesCompositionSting() {
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
			+ DateUtils.getFileNameDateStamp() + postPart;

	    } else {
		eventFileName += "_" + batchId;
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

    public abstract Set<InteractionMechanism> configInteractionMechanisms();

    public abstract ProblemComplexity configProblemComplexity();

    public abstract int configNumberOfAgentsPerSpecies();

    public abstract int configNumberOfCollectionSites();

    public abstract int configMaxStepsPerAgent();

    public abstract boolean configIsRecordExperiment();

    public abstract String configExperimentName();

    public abstract int configSimulationsPerExperiment();

    public abstract int configStepsPerSimulation();

    public abstract String configEventFileName();

    public abstract Evolver configEvolver();

    public abstract AgentFactory configAgentFactory();

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

    public boolean isRecordExperiment() {
	return recordExperiment;
    }

    public void setRecordExperiment(boolean recordExperiment) {
	this.recordExperiment = recordExperiment;
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

    public int getNumberOfAgentsPerSpecies() {
	return numberOfAgentsPerSpecies;
    }

    public void setNumberOfAgentsPerSpecies(int numberOfAgentsPerSpecies) {
	this.numberOfAgentsPerSpecies = numberOfAgentsPerSpecies;
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

    public Set<InteractionMechanism> getInteractionMechanisms() {
	return interactionMechanisms;
    }

    public void setInteractionMechanisms(
	    Set<InteractionMechanism> interactionMechanisms) {
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
    
}
