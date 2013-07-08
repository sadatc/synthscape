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
	Simulation sim;

	long agentId;

	protected long stepCounter;

	protected int energy;

	protected int x;

	protected int y;

	protected double communicationCapability;

	protected double visionCapability;

	protected double extractionCapability;

	protected double transportationCapability;

	protected boolean isCharging;

	protected int quantityOfResourceA;

	protected int maxResourceACapacity = 1;

	protected int quantityOfResourceB;

	protected int maxResourceBCapacity = 1;

	protected int quantityOfResourceC;

	protected int maxResourceCCapacity = 1;

	protected int maxEnergy;

	protected int previousX;

	protected int previousY;

	protected boolean locationIsHome;

	protected boolean locationHasResourceA;

	protected boolean locationHasResourceB;

	protected boolean locationHasResourceC;

	protected boolean locationHasExtractA;

	protected boolean locationHasExtractB;

	protected boolean locationHasExtractC;

	protected boolean locationHasTrailA;

	protected boolean locationHasTrailB;

	protected boolean locationHasTrailC;

	protected BroadcastSignal signalA = new BroadcastSignal();

	protected BroadcastSignal signalB = new BroadcastSignal();

	protected BroadcastSignal signalC = new BroadcastSignal();

	protected Set<OperationType> allowedOperations = EnumSet
			.noneOf(OperationType.class);

	protected VirtualMachine virtualMachine;

	protected Program program;

	protected double fitness;

	protected long generation;

	protected IntGrid2D visitedCells = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);

	protected Data myStats = new Data();

	public Agent randomizeGenotype() {
		if (program != null) {
			program.randomizeInstructions();
		}
		return this;
	}

	public boolean isOperationAllowed(OperationType operationType) {
		boolean isAllowed = false;

		if (Simulation.CAPABILITY_BASED_OPERATIONS_ONLY) {
			switch (operationType) {
			case VISION:
				isAllowed = (this.visionCapability > 0.0);
				break;

			case EXTRACTION:
				isAllowed = (this.extractionCapability > 0.0);
				break;

			case COMMUNICATION:
				isAllowed = (this.communicationCapability > 0.0);
				break;

			case TRANSPORTATION:
				isAllowed = (this.transportationCapability > 0.0);
				break;

			case GENERIC:
				isAllowed = true;
				break;
			default:
				isAllowed = false;
			}
		} else {
			isAllowed = true;
		}
		return isAllowed;
	}

	public final void spendEnergy(OperationType operationType) {
		// how much energy each instruction costs, depends on the capability
		// of the agent for that particular type of operation

		if (Simulation.ROLE_BASED_ENERGY_ACCOUNTING) {

			double drain = 0.0;

			switch (operationType) {
			case VISION:
				if (this.visionCapability > 0.0) {
					drain = 1.0 / this.visionCapability;
				} else {
					drain = Simulation.UNSKILLED_ENERGY_DRAIN;
				}
				break;

			case EXTRACTION:
				if (this.extractionCapability > 0.0) {
					drain = 1.0 / this.extractionCapability;
				} else {
					drain = Simulation.UNSKILLED_ENERGY_DRAIN;
				}
				break;

			case TRANSPORTATION:
				if (this.transportationCapability > 0.0) {
					drain = 1.0 / this.transportationCapability;
				} else {
					drain = Simulation.UNSKILLED_ENERGY_DRAIN;
				}
				break;

			case COMMUNICATION:
				if (this.communicationCapability > 0.0) {
					drain = 1.0 / this.communicationCapability;
				} else {
					drain = Simulation.GENERIC_ENERGY_DRAIN;
				}
				break;
			default:
				drain = Simulation.GENERIC_ENERGY_DRAIN;
			}

			energy -= drain;

		} else {
			energy -= Simulation.GENERIC_ENERGY_DRAIN;

		}

		if (energy < 0) {
			energy = 0;
		}

	}

	public final boolean hasObstacle(int x, int y) {
		return (sim.obstacleGrid.field[x][y] == Simulation.OBSTACLE_TRUE);
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
			if (sim.obstacleGrid.field[newX][newY] != Simulation.OBSTACLE_TRUE) {

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
			if (sim.obstacleGrid.field[newX][newY] != Simulation.OBSTACLE_TRUE) {

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

			if (!(xDelta == 0 && yDelta == 0)
					&& xMod >= 0
					&& xMod < Simulation.GRID_WIDTH
					&& yMod >= 0
					&& yMod < Simulation.GRID_HEIGHT
					&& sim.obstacleGrid.field[xMod][yMod] == Simulation.OBSTACLE_FALSE) {
				newX = xMod;
				newY = yMod;
				foundNewUblockedLocation = true;
			}
		} while (!foundNewUblockedLocation);

		_operationMoveAbsolute(newX, newY);
	}

	public final void _operationMoveToLocationAt(int homeX, int homeY) {

		if (!(this.x == homeX && this.y == homeY)) {
			int deltaX = 0;
			int deltaY = 0;

			if (this.x < homeX) {
				deltaX++;
			}

			if (this.x > homeX) {
				deltaX--;
			}

			if (this.y < homeY) {
				deltaY++;
			}

			if (this.y > homeY) {
				deltaY--;
			}

			if (deltaX != 0 || deltaY != 0) {
				int newX = this.x + deltaX;
				int newY = this.y + deltaY;
				if (hasObstacle(newX, newY)) {
					_operationRandomMove();
				} else {
					_operationMoveAbsolute(newX, newY);
				}
			}
		}
	}

	public final void _operationFollowBroadcast(BroadcastSignal signal) {
		if (signal.senderAgentId != -1 && signal.senderAgentId != this.agentId) {
			_operationMoveToLocationAt(signal.x, signal.y);
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
				this.sim.setNumberOfExtractedResources(this.sim.getNumberOfExtractedResources()+1);
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
	 * _operationMove(newX, newY); spendEnergy(OperationType.GENERIC);
	 * 
	 * }
	 */

	public final void operationMoveNorth() {
		_operationMoveRelative(0, -1);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveNorthEast() {
		_operationMoveRelative(1, -1);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveNorthWest() {
		_operationMoveRelative(-1, -1);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveSouth() {
		_operationMoveRelative(0, 1);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveSouthEast() {
		_operationMoveRelative(1, 1);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveSouthWest() {
		_operationMoveRelative(-1, 1);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveEast() {
		_operationMoveRelative(1, 0);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveWest() {
		_operationMoveRelative(-1, 0);
		spendEnergy(OperationType.GENERIC);
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
		spendEnergy(OperationType.GENERIC);

	}

	public final void operationRandomMove() {
		_operationRandomMove();
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveToPrimaryHome() {
		_operationMoveToLocationAt(Simulation.PRIMARY_HOME_X,
				Simulation.PRIMARY_HOME_Y);
		spendEnergy(OperationType.GENERIC);
	}

	public final void operationMoveToClosestHome() {

		if (isOperationAllowed(OperationType.VISION)) {
			// first let's find out the closest home
			Int2D closestHome = null;
			double closestDistance = Double.MAX_VALUE;

			for (Int2D home : this.sim.homeList) {
				double distance = home.distance(this.x, this.y);
				if (home.distance(this.x, this.y) < closestDistance) {
					closestHome = home;
					closestDistance = distance;
				}
			}

			_operationMoveToLocationAt(closestHome.x, closestHome.y);
			spendEnergy(OperationType.VISION);
		}
	}

	public final void operationMoveToClosestAgent() {
		if (isOperationAllowed(OperationType.VISION)) {

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
			spendEnergy(OperationType.VISION);
		}
	}

	public final void operationFollowBroadcastA() {
		if (isOperationAllowed(OperationType.COMMUNICATION)) {
			_operationFollowBroadcast(signalA);
			spendEnergy(OperationType.COMMUNICATION);
			sim.statistics.stepData.aBroadcastMoves++;
			sim.statistics.stepData.broadcastMoves++;
		}
	}

	public final void operationFollowBroadcastB() {
		if (Constants.SINGLE_BROADCAST_MODEL) {
			operationFollowBroadcastA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			_operationFollowBroadcast(signalB);
			spendEnergy(OperationType.COMMUNICATION);
			sim.statistics.stepData.bBroadcastMoves++;
			sim.statistics.stepData.broadcastMoves++;
		}
	}

	public final void operationFollowBroadcastC() {
		if (Constants.SINGLE_BROADCAST_MODEL) {
			operationFollowBroadcastA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			_operationFollowBroadcast(signalC);
			spendEnergy(OperationType.COMMUNICATION);
			sim.statistics.stepData.cBroadcastMoves++;
			sim.statistics.stepData.broadcastMoves++;
		}
	}

	public final void operationFollowBroadcast() {
		operationFollowBroadcastA();
	}

	public final void operationFollowBroadcastAny() {
		if (isOperationAllowed(OperationType.COMMUNICATION)) {
			if (Constants.SINGLE_BROADCAST_MODEL) {
				operationFollowBroadcastA();
			} else

			if (signalA.senderAgentId != -1
					&& signalA.senderAgentId != this.agentId) {
				_operationMoveToLocationAt(signalA.x, signalA.y);
				sim.statistics.stepData.aBroadcastMoves++;
				sim.statistics.stepData.broadcastMoves++;
			} else if (signalB.senderAgentId != -1
					&& signalB.senderAgentId != this.agentId) {
				_operationMoveToLocationAt(signalB.x, signalB.y);
				sim.statistics.stepData.bBroadcastMoves++;
				sim.statistics.stepData.broadcastMoves++;
			} else if (signalC.senderAgentId != -1
					&& signalC.senderAgentId != this.agentId) {
				_operationMoveToLocationAt(signalC.x, signalC.y);
				sim.statistics.stepData.cBroadcastMoves++;
				sim.statistics.stepData.broadcastMoves++;
			}
			spendEnergy(OperationType.COMMUNICATION);
		}
	}

	public final void operationFollowTrailA() {
		if (isOperationAllowed(OperationType.VISION)) {
			_operationFollowTrail(sim.trailAGrid);
			spendEnergy(OperationType.VISION);
			sim.statistics.stepData.trailFollows++;
			sim.statistics.stepData.trailAFollows++;
		}
	}

	public final void operationFollowTrailB() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			operationFollowTrailA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			_operationFollowTrail(sim.trailBGrid);
			spendEnergy(OperationType.VISION);
			sim.statistics.stepData.trailFollows++;
			sim.statistics.stepData.trailBFollows++;
		}
	}

	public final void operationFollowTrailC() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			operationFollowTrailA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			_operationFollowTrail(sim.trailCGrid);
			spendEnergy(OperationType.VISION);
			sim.statistics.stepData.trailFollows++;
			sim.statistics.stepData.trailCFollows++;
		}
	}

	public final void operationFollowTrail() {
		operationFollowTrailA();
	}

	public final void operationFollowTrailAny() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			operationFollowTrailA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			_operationFollowTrail(sim.trailAGrid, sim.trailBGrid,
					sim.trailCGrid);
			spendEnergy(OperationType.VISION);
			sim.statistics.stepData.trailFollows++;
			sim.statistics.stepData.trailRandomFollows++;
		}
	}

	public final void operationLeaveTrail() {
		operationLeaveTrailA();
	}

	public final void operationLeaveTrailsAll() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			operationLeaveTrailA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			operationLeaveTrailA();
			operationLeaveTrailB();
			operationLeaveTrailC();
		}
	}

	public void operationLeaveTrailA() {
		if (isOperationAllowed(OperationType.COMMUNICATION)) {
			_operationLeaveTrail(sim.trailAGrid);
			spendEnergy(OperationType.COMMUNICATION);
			sim.statistics.stepData.trailDrops++;
			sim.statistics.stepData.trailADrops++;
			updateLocationStatus(this.x, this.y);

		}
	}

	public void operationLeaveTrailB() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			operationLeaveTrailA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			_operationLeaveTrail(sim.trailBGrid);
			spendEnergy(OperationType.COMMUNICATION);
			sim.statistics.stepData.trailDrops++;
			sim.statistics.stepData.trailBDrops++;
			updateLocationStatus(this.x, this.y);
		}
	}

	public void operationLeaveTrailC() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			operationLeaveTrailA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			_operationLeaveTrail(sim.trailBGrid);
			spendEnergy(OperationType.COMMUNICATION);
			sim.statistics.stepData.trailDrops++;
			sim.statistics.stepData.trailCDrops++;
			updateLocationStatus(this.x, this.y);
		}
	}

	public final boolean operationDetectHome() {
		spendEnergy(OperationType.GENERIC);
		return this.locationIsHome;
	}

	public final boolean operationDetectResourceAny() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			return operationDetectResourceA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return (this.locationHasResourceA || this.locationHasResourceB || this.locationHasResourceC);
		} else {
			return false;
		}

	}

	public final boolean operationDetectResource() {
		if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return (this.locationHasResourceA);
		} else {
			return false;
		}

	}

	public final boolean operationDetectResourceA() {
		if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasResourceA;
		} else {
			return false;
		}
	}

	public final boolean operationDetectResourceB() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			return operationDetectResourceA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasResourceB;
		} else {
			return false;
		}
	}

	public final boolean operationDetectResourceC() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			return operationDetectResourceA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasResourceC;
		} else {
			return false;
		}
	}

	public final boolean operationDetectExtractedResourceA() {
		if (isOperationAllowed(OperationType.COMMUNICATION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasExtractA;
		} else {
			return false;
		}
	}

	public final boolean operationDetectExtractedResourceB() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			return operationDetectExtractedResourceA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasExtractB;
		} else {
			return false;
		}
	}

	public final boolean operationDetectExtractedResourceC() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			return operationDetectExtractedResourceA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasExtractC;
		} else {
			return false;
		}
	}

	public final boolean operationDetectExtractedResource() {
		return operationDetectExtractedResourceA();
	}

	public final boolean operationDetectExtractedResourceAny() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			return operationDetectExtractedResourceA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return (this.locationHasExtractC || this.locationHasExtractB || this.locationHasExtractA);
		} else {
			return false;
		}

	}

	public final boolean operationDetectTrailA() {
		if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasTrailA;
		} else {
			return false;
		}
	}

	public final boolean operationDetectTrailB() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			return operationDetectTrailA();
		} else if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasTrailB;
		} else {
			return false;

		}
	}

	public final boolean operationDetectTrailC() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			return operationDetectTrailA();
		} else if (isOperationAllowed(OperationType.VISION)) {

			spendEnergy(OperationType.VISION);
			return this.locationHasTrailC;
		} else {
			return false;

		}
	}

	public final boolean operationDetectTrailAny() {
		if (Constants.SINGLE_TRAIL_MODEL) {
			return operationDetectTrailA();
		} else if (isOperationAllowed(OperationType.VISION)) {

			spendEnergy(OperationType.VISION);
			return (this.locationHasTrailA || this.locationHasTrailB || this.locationHasTrailC);
		} else {
			return false;
		}
	}

	public final boolean operationDetectTrail() {

		if (isOperationAllowed(OperationType.VISION)) {
			spendEnergy(OperationType.VISION);
			return this.locationHasTrailA;
		} else {
			return false;
		}

	}

	public final void operationExtractResourceA() {
		if (isOperationAllowed(OperationType.EXTRACTION)) {
			if (_operationExtractResource(locationHasResourceA,
					this.sim.resourceAGrid, this.sim.extractAGrid)) {
				sim.statistics.stepData.resourceAExtracts++;
				sim.statistics.stepData.resourceExtracts++;

			}

			spendEnergy(OperationType.EXTRACTION);
			updateLocationStatus(this.x, this.y);
		}
	}

	public final void operationExtractResourceB() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationExtractResourceA();
		} else

		if (isOperationAllowed(OperationType.EXTRACTION)) {
			if (_operationExtractResource(locationHasResourceB,
					this.sim.resourceBGrid, this.sim.extractBGrid)) {
				sim.statistics.stepData.resourceBExtracts++;
				sim.statistics.stepData.resourceExtracts++;
			}

			spendEnergy(OperationType.EXTRACTION);

			updateLocationStatus(this.x, this.y);
		}
	}

	public final void operationExtractResourceC() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationExtractResourceA();
		} else

		if (isOperationAllowed(OperationType.EXTRACTION)) {
			if (_operationExtractResource(locationHasResourceC,
					this.sim.resourceCGrid, this.sim.extractCGrid)) {
				sim.statistics.stepData.resourceCExtracts++;
				sim.statistics.stepData.resourceExtracts++;
			}

			spendEnergy(OperationType.EXTRACTION);
			updateLocationStatus(this.x, this.y);
		}
	}

	public final void operationExtractResource() {
		operationExtractResourceA();
	}

	public final void operationExtractResourceAny() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationExtractResourceA();
		} else

		if (isOperationAllowed(OperationType.EXTRACTION)) {
			operationExtractResourceA();
			operationExtractResourceB();
			operationExtractResourceC();
		}
	}

	public final void operationLoadResourceA() {

		if (isOperationAllowed(OperationType.TRANSPORTATION)) {
			if (this.locationHasExtractA
					&& this.quantityOfResourceA < this.maxResourceACapacity) {
				if (this.sim.extractAGrid.field[x][y] > 0) {
					this.sim.extractAGrid.field[x][y]--;
					this.quantityOfResourceA++;
					sim.statistics.stepData.resourceLoads++;
					sim.statistics.stepData.resourceALoads++;
					updateLocationStatus(this.x, this.y);
				}
			}
			spendEnergy(OperationType.TRANSPORTATION);
		}
	}

	public final void operationLoadResourceB() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationLoadResourceA();
		} else if (isOperationAllowed(OperationType.TRANSPORTATION)) {

			if (this.locationHasExtractB
					&& this.quantityOfResourceB < this.maxResourceBCapacity) {
				if (this.sim.extractBGrid.field[x][y] > 0) {
					this.sim.extractBGrid.field[x][y]--;
					this.quantityOfResourceB++;
					sim.statistics.stepData.resourceLoads++;
					sim.statistics.stepData.resourceBLoads++;
					updateLocationStatus(this.x, this.y);
				}
			}
			spendEnergy(OperationType.TRANSPORTATION);
		}
	}

	public final void operationLoadResourceC() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationLoadResourceC();
		} else if (isOperationAllowed(OperationType.TRANSPORTATION)) {

			if (this.locationHasExtractC
					&& this.quantityOfResourceC < this.maxResourceCCapacity) {
				if (this.sim.extractCGrid.field[x][y] > 0) {
					this.sim.extractCGrid.field[x][y]--;
					this.quantityOfResourceC++;
					sim.statistics.stepData.resourceLoads++;
					sim.statistics.stepData.resourceCLoads++;
					updateLocationStatus(this.x, this.y);
				}
			}

			spendEnergy(OperationType.TRANSPORTATION);
		}
	}

	public final void operationLoadResource() {
		operationLoadResourceA();
	}

	public final void operationLoadResourceAny() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationLoadResourceA();
		} else if (isOperationAllowed(OperationType.TRANSPORTATION)) {
			operationLoadResourceA();
			operationLoadResourceB();
			operationLoadResourceC();
		}
	}

	public final void operationUnLoadResourceA() {
		if (isOperationAllowed(OperationType.TRANSPORTATION)) {
			if (this.quantityOfResourceA > 0) {

				this.quantityOfResourceA--;

				sim.statistics.stepData.resourceAUnloads++;
				sim.statistics.stepData.resourceUnloads++;

				if (this.locationIsHome) {
					sim.statistics.stepData.resourceCaptures++;
					sim.statistics.stepData.resourceACaptures++;
				} else {
					this.sim.extractAGrid.field[x][y]++;
				}

				updateLocationStatus(this.x, this.y);

			}
			spendEnergy(OperationType.TRANSPORTATION);
		}
	}

	public final void operationUnLoadResourceB() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationUnLoadResourceA();
		} else if (isOperationAllowed(OperationType.TRANSPORTATION)) {
			if (this.quantityOfResourceB > 0) {
				this.sim.extractBGrid.field[x][y]++;
				this.quantityOfResourceB--;
				sim.statistics.stepData.resourceBUnloads++;
				sim.statistics.stepData.resourceUnloads++;
				if (this.locationIsHome) {
					sim.statistics.stepData.resourceCaptures++;
					sim.statistics.stepData.resourceBCaptures++;
				}
				updateLocationStatus(this.x, this.y);
			}
			spendEnergy(OperationType.TRANSPORTATION);
		}
	}

	public final void operationUnLoadResourceC() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationUnLoadResourceA();
		} else if (isOperationAllowed(OperationType.TRANSPORTATION)) {
			if (this.quantityOfResourceC > 0) {
				this.sim.extractCGrid.field[x][y]++;
				this.quantityOfResourceC--;
				sim.statistics.stepData.resourceCUnloads++;
				sim.statistics.stepData.resourceUnloads++;
				if (this.locationIsHome) {
					sim.statistics.stepData.resourceCaptures++;
					sim.statistics.stepData.resourceCCaptures++;
				}
				updateLocationStatus(this.x, this.y);
			}
			spendEnergy(OperationType.TRANSPORTATION);
		}
	}

	public final void operationUnLoadResource() {
		operationUnLoadResourceA();
	}

	public final void operationUnLoadResourceAny() {
		if (Constants.SINGLE_RESOURCE_MODEL) {
			operationUnLoadResourceA();
		} else if (isOperationAllowed(OperationType.TRANSPORTATION)) {
			operationUnLoadResourceA();
			operationUnLoadResourceB();
			operationUnLoadResourceC();
		}
	}

	public final void operationBroadcastA() {
		if (isOperationAllowed(OperationType.COMMUNICATION)) {
			sim.signalALevel = Simulation.SIGNAL_LEVEL_MAX;
			for (Agent agent : sim.agents) {
				agent.signalA.senderAgentId = this.agentId;
				agent.signalA.x = this.x;
				agent.signalA.y = this.y;
			}
			sim.statistics.stepData.aBroadcasts++;
			sim.statistics.stepData.broadcasts++;
			spendEnergy(OperationType.COMMUNICATION);
		}
	}

	public final void operationBroadcastB() {
		if (Constants.SINGLE_BROADCAST_MODEL) {
			operationBroadcastA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			sim.signalBLevel = Simulation.SIGNAL_LEVEL_MAX;
			for (Agent agent : sim.agents) {
				agent.signalB.senderAgentId = this.agentId;
				agent.signalB.x = this.x;
				agent.signalB.y = this.y;
			}
			sim.statistics.stepData.bBroadcasts++;
			sim.statistics.stepData.broadcasts++;
			spendEnergy(OperationType.COMMUNICATION);
		}
	}

	public final void operationBroadcastC() {
		if (Constants.SINGLE_BROADCAST_MODEL) {
			operationBroadcastA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {

			sim.signalCLevel = Simulation.SIGNAL_LEVEL_MAX;
			for (Agent agent : sim.agents) {
				agent.signalC.senderAgentId = this.agentId;
				agent.signalC.x = this.x;
				agent.signalA.y = this.y;
			}
			sim.statistics.stepData.cBroadcasts++;
			sim.statistics.stepData.broadcasts++;
			spendEnergy(OperationType.COMMUNICATION);
		}
	}

	public final void operationBroadcast() {
		operationBroadcastA();
	}

	public final void operationBroadcastAll() {
		if (Constants.SINGLE_BROADCAST_MODEL) {
			operationBroadcastA();
		} else if (isOperationAllowed(OperationType.COMMUNICATION)) {
			operationBroadcastA();
			operationBroadcastB();
			operationBroadcastC();
		}
	}

	public final void operationRecharge() {
		handleRecharge();
	}

	public void handleRecharge() {
		if (isCharging) {
			energy++;
			if (energy == maxEnergy) {
				isCharging = false;
			}
		}
	}

	protected void reset() {

		stepCounter = 0;

		quantityOfResourceA = 0;
		quantityOfResourceB = 0;
		quantityOfResourceC = 0;

		signalA.senderAgentId = -1;
		signalA.x = -1;
		signalA.y = -1;
		signalB.senderAgentId = -1;
		signalB.x = -1;
		signalB.y = -1;
		signalC.senderAgentId = -1;
		signalC.x = -1;
		signalC.y = -1;

		fitness = 0.0;

		visitedCells.setTo(0);

		isCharging = false;

		myStats.zeroAll();

	}

	protected void reset(long generation, long agentId, int energy,
			int maxEnergy, double visionCapability,
			double extractionCapability, double transportationCapability,
			double communicationCapability, int startX, int startY) {

		reset();

		this.agentId = agentId;

		this.generation = generation;

		this.maxEnergy = maxEnergy;
		this.energy = energy;
		this.visionCapability = visionCapability;
		this.extractionCapability = extractionCapability;
		this.transportationCapability = transportationCapability;
		this.communicationCapability = communicationCapability;

		updateLocationStatus(startX, startY);
	}

	protected Agent(Simulation sim, long generation, long agentId, int energy,
			int maxEnergy, double visionCapability,
			double extractionCapability, double transportationCapability,
			double communicationCapability, int startX, int startY) {

		this.sim = sim;

		reset(generation, agentId, energy, maxEnergy, visionCapability,
				extractionCapability, transportationCapability,
				communicationCapability, startX, startY);
	}

	public void updateLocationStatus(int x, int y) {

		this.x = x;
		this.y = y;

		boolean locationIsChanging = (x != previousX || y != previousY);

		// this captures visited cell statistics for this agent
		if (visitedCells.field[x][y] == 0) {
			visitedCells.field[x][y] = 1;
			myStats.numberOfCellsDiscovered++;
		}

		// this captures visited cell statistics for the entire environment
		if (sim.visitedCells.field[x][y] == 0) {
			sim.visitedCells.field[x][y] = 1;
			sim.statistics.stepData.numberOfCellsDiscovered++;
		}

		if (sim.resourceAGrid.field[x][y] > 0) {
			this.locationHasResourceA = true;
			if (locationIsChanging) {
				sim.statistics.stepData.resourceHits++;
				sim.statistics.stepData.resourceAHits++;
				myStats.resourceHits++;
				myStats.resourceAHits++;
			}
		} else {
			this.locationHasResourceA = false;
		}

		if (sim.resourceBGrid.field[x][y] > 0) {
			this.locationHasResourceB = true;
			if (locationIsChanging) {
				sim.statistics.stepData.resourceHits++;
				sim.statistics.stepData.resourceBHits++;
				myStats.resourceHits++;
				myStats.resourceBHits++;
			}
		} else {
			this.locationHasResourceB = false;
		}

		if (sim.resourceCGrid.field[x][y] > 0) {
			this.locationHasResourceC = true;
			if (locationIsChanging) {
				sim.statistics.stepData.resourceHits++;
				sim.statistics.stepData.resourceCHits++;
				myStats.resourceHits++;
				myStats.resourceCHits++;
			}

		} else {
			this.locationHasResourceC = false;
		}

		if (sim.extractAGrid.field[x][y] > 0) {
			this.locationHasExtractA = true;
			if (locationIsChanging) {
				sim.statistics.stepData.extractHits++;
				sim.statistics.stepData.extractAHits++;
				myStats.extractHits++;
				myStats.extractAHits++;
			}
		} else {
			this.locationHasExtractA = false;
		}

		if (sim.extractBGrid.field[x][y] > 0) {
			this.locationHasExtractB = true;
			if (locationIsChanging) {
				sim.statistics.stepData.extractHits++;
				sim.statistics.stepData.extractBHits++;
				myStats.extractHits++;
				myStats.extractBHits++;
			}
		} else {
			this.locationHasExtractB = false;
		}

		if (sim.extractCGrid.field[x][y] > 0) {
			this.locationHasExtractC = true;
			if (locationIsChanging) {
				sim.statistics.stepData.extractHits++;
				sim.statistics.stepData.extractCHits++;
				myStats.extractHits++;
				myStats.extractCHits++;
			}
		} else {
			this.locationHasExtractC = false;
		}

		if (sim.trailAGrid.field[x][y] > 0) {
			this.locationHasTrailA = true;
			if (locationIsChanging) {
				sim.statistics.stepData.trailHits++;
				sim.statistics.stepData.trailAHits++;
				myStats.trailHits++;
				myStats.trailAHits++;
			}
		} else {
			this.locationHasTrailA = false;
		}

		if (sim.trailBGrid.field[x][y] > 0) {
			this.locationHasTrailB = true;
			if (locationIsChanging) {
				sim.statistics.stepData.trailHits++;
				sim.statistics.stepData.trailBHits++;
				myStats.trailHits++;
				myStats.trailBHits++;
			}
		} else {
			this.locationHasTrailB = false;
		}

		if (sim.trailCGrid.field[x][y] > 0) {
			this.locationHasTrailC = true;
			if (locationIsChanging) {
				sim.statistics.stepData.trailHits++;
				sim.statistics.stepData.trailCHits++;
				myStats.trailHits++;
				myStats.trailCHits++;
			}
		} else {
			this.locationHasTrailC = false;
		}

		if (sim.homeGrid.field[x][y] > 0) {
			this.locationIsHome = true;
			if (locationIsChanging) {
				sim.statistics.stepData.homeHits++;
				myStats.homeHits++;

				if (x == sim.PRIMARY_HOME_X && y == sim.PRIMARY_HOME_Y) {
					sim.statistics.stepData.primaryHomeHits++;
					myStats.primaryHomeHits++;
				}
			}
		} else {
			this.locationIsHome = false;
		}

	}

	abstract public double doubleValue();

	/**
	 * Inherited classes should override this
	 * @param state
	 */
	abstract public void stepAction(SimState state);

	public void step(SimState state) {

		if (!Constants.USE_ENERGY_ACCOUNTING) {
			stepAction(state);
		} else {
			if (!isCharging && energy > 0) {
				stepAction(state);
				if (energy == 0) {
					isCharging = true;
				}
			} else {
				handleRecharge();
			}
		}
		stepCounter++;
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
