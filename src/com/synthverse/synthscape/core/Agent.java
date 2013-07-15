package com.synthverse.synthscape.core;

import java.util.EnumSet;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.Valuable;

import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;

public abstract class Agent implements Constants, Steppable, Valuable,
		Comparable<Agent> {

	private static final long serialVersionUID = -5129827193602692370L;

	Simulation sim ;

	long agentId;

	protected long stepCounter;

	protected long maxSteps;

	protected int x;

	protected int y;

	protected Set<InteractionMechanism> interactionMechanisms = EnumSet
			.noneOf(InteractionMechanism.class);

	protected Set<Trait> traits = EnumSet.noneOf(Trait.class);

	protected int resourcesLoaded;

	protected int loadCapacity = 1;

	protected int previousX;

	protected int previousY;

	protected boolean locationIsCollectionSite;

	protected boolean locationHasResource;

	protected boolean locationHasExtractedResource;

	protected boolean locationHasProcessedResource;

	protected boolean locationHasTrail;

	protected double fitness;

	protected long generation;

	protected VirtualMachine virtualMachine;

	protected Program program;

	protected IntGrid2D visitedCells = new IntGrid2D(WORLD_WIDTH, WORLD_LENGTH,
			0);

	protected Measure stats = new Measure();

	public Agent randomizeGenotype() {
		if (program != null) {
			program.randomizeInstructions();
		}
		return this;
	}

	public final boolean hasObstacle(int x, int y) {
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

			if (!(xDelta == 0 && yDelta == 0) && xMod >= 0
					&& xMod < Simulation.WORLD_WIDTH && yMod >= 0
					&& yMod < Simulation.WORLD_LENGTH
					&& sim.obstacleGrid.field[xMod][yMod] == ABSENT) {
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
				if (hasObstacle(newX, newY)) {
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
	}

	public final boolean _operationExtractResource(boolean resourceLocated,
			IntGrid2D resourceGrid, IntGrid2D extractGrid) {
		boolean actuallyExtracted = false;
		if (resourceLocated) {
			if (resourceGrid.field[x][y] > 0) {
				resourceGrid.field[x][y]--;
				extractGrid.field[x][y]++;
				actuallyExtracted = true;
				this.sim.setNumberOfExtractedResources(this.sim
						.getNumberOfExtractedResources() + 1);
			}
		}
		return actuallyExtracted;
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
			_operationMoveAbsolute(maxX, maxY);
		} else {
			_operationRandomMove();
		}

	}

	public final void _operationFollowTrail(DoubleGrid2D trailA,
			DoubleGrid2D trailB, DoubleGrid2D trailC) {
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
		_operationMoveToLocationAt(Simulation.PRIMARY_COLLECTION_SITE_X,
				Simulation.PRIMARY_COLLECTION_SITE_Y);

	}

	public final void operationMoveToClosestCollectionSite() {

		if (traits.contains(Trait.HOMING)) {
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

			_operationMoveToLocationAt(closestCollectionSite.x,
					closestCollectionSite.y);

		}
	}

	public final void operationMoveToClosestAgent() {
		if (traits.contains(Trait.FLOCKING)) {

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
			_operationMoveToLocationAt(closestAgentLocation.x,
					closestAgentLocation.y);

		}
	}

	public final void operationFollowTrail() {
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			_operationFollowTrail(sim.trailGrid);

			sim.statistics.stepData.trailFollows++;

		}
	}

	public void operationLeaveTrail() {
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			_operationLeaveTrail(sim.trailGrid);

			sim.statistics.stepData.trailDrops++;

			updateLocationStatus(this.x, this.y);

		}
	}

	public final boolean operationDetectCollectionSite() {
		if (traits.contains(Trait.HOMING)) {

			return this.locationIsCollectionSite;
		} else {
			return false;
		}
	}

	public final boolean operationDetectResource() {
		if (traits.contains(Trait.DETECTION)) {

			return (this.locationHasResource);
		} else {
			return false;
		}

	}

	public final boolean operationDetectExtractedResource() {
		if (traits.contains(Trait.DETECTION)) {

			return this.locationHasExtractedResource;
		} else {
			return false;
		}
	}

	public final boolean operationDetectTrail() {
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {

			return this.locationHasTrail;
		} else {
			return false;
		}
	}

	public final void operationExtractResource() {
		if (traits.contains(Trait.EXTRACTION)) {
			if (_operationExtractResource(locationHasResource,
					this.sim.resourceGrid, this.sim.extractedResourceGrid)) {

				sim.statistics.stepData.resourceExtracts++;

			}

			updateLocationStatus(this.x, this.y);
		}
	}

	public final void operationLoadResource() {

		if (traits.contains(Trait.TRANSPORTATION)) {
			if (this.locationHasExtractedResource
					&& this.resourcesLoaded < this.loadCapacity) {
				if (this.sim.extractedResourceGrid.field[x][y] > 0) {
					this.sim.extractedResourceGrid.field[x][y]--;
					this.resourcesLoaded++;
					sim.statistics.stepData.resourceLoads++;

					updateLocationStatus(this.x, this.y);
				}
			}

		}
	}

	public final void operationUnLoadResource() {
		if (traits.contains(Trait.TRANSPORTATION)) {
			if (this.resourcesLoaded > 0) {

				this.resourcesLoaded--;

				sim.statistics.stepData.resourceUnloads++;

				if (this.locationIsCollectionSite) {
					sim.statistics.stepData.resourceCaptures++;

				} else {
					this.sim.extractedResourceGrid.field[x][y]++;
				}

				updateLocationStatus(this.x, this.y);

			}

		}
	}

	protected void reset() {
		stepCounter = 0;
		resourcesLoaded = 0;
		fitness = 0.0;
		visitedCells.setTo(0);
		stats.zeroAll();
	}

	protected Agent() {
	}

	public void updateLocationStatus(int x, int y) {

		this.x = x;
		this.y = y;

		boolean locationIsChanging = (x != previousX || y != previousY);

		// this captures visited cell statistics for this agent
		if (visitedCells.field[x][y] == 0) {
			visitedCells.field[x][y] = 1;
			stats.numberOfCellsDiscovered++;
		}

		// this captures visited cell statistics for the entire environment
		if (sim.refactorGrid.field[x][y] == 0) {
			sim.refactorGrid.field[x][y] = 1;
			sim.statistics.stepData.numberOfCellsDiscovered++;
		}

		if (sim.resourceGrid.field[x][y] > 0) {
			this.locationHasResource = true;
			if (locationIsChanging) {
				sim.statistics.stepData.resourceHits++;

				stats.resourceHits++;

			}
		} else {
			this.locationHasResource = false;
		}

		if (sim.extractedResourceGrid.field[x][y] > 0) {
			this.locationHasExtractedResource = true;
			if (locationIsChanging) {
				sim.statistics.stepData.extractedResourceHits++;

				stats.extractedResourceHits++;

			}
		} else {
			this.locationHasExtractedResource = false;
		}

		if (sim.trailGrid.field[x][y] > 0) {
			this.locationHasTrail = true;
			if (locationIsChanging) {
				sim.statistics.stepData.trailHits++;

				stats.trailHits++;

			}
		} else {
			this.locationHasTrail = false;
		}

		if (sim.collectionSiteGrid.field[x][y] > 0) {
			this.locationIsCollectionSite = true;
			if (locationIsChanging) {
				sim.statistics.stepData.collectionSiteHits++;
				stats.collectionSiteHits++;

				if (x == sim.PRIMARY_COLLECTION_SITE_X
						&& y == sim.PRIMARY_COLLECTION_SITE_Y) {
					sim.statistics.stepData.primaryCollectionSiteHits++;
					stats.primaryCollectionSiteHits++;
				}
			}
		} else {
			this.locationIsCollectionSite = false;
		}

	}

	abstract public double doubleValue();

	/**
	 * Inherited classes should override this
	 * 
	 * @param state
	 */
	abstract public void stepAction(SimState state);

	public boolean hasStepsRemaining() {
		return stepCounter < maxSteps;
	}

	public void step(SimState state) {
		if (hasStepsRemaining()) {
			stepAction(state);
			stepCounter++;
		}
	}

	public double getFitness() {
		return this.fitness;
	}

	public long getId() {
		return this.agentId;
	}

	public long getGeneration() {
		return this.generation;
	}

	public Program getProgram() {
		return this.program;
	}

	public void setVirtualMachine(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;

	}

	public void loadProgramInVirtualMachine() {
		virtualMachine.loadProgram(program);

	}

	public int compareTo(Agent a) {
		if (this.fitness > a.getFitness()) {
			return 1;
		} else if (this.fitness < a.getFitness()) {
			return -1;
		}
		return 0;
	}

}
