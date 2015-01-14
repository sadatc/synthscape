package com.synthverse.synthscape.core;

import java.util.EnumSet;
import java.util.HashMap;
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

public abstract class Agent implements Constants, Steppable, Valuable,
		Comparable<Agent> {

	protected static final long serialVersionUID = -5129827193602692370L;
	protected static Logger logger = Logger.getLogger(Agent.class.getName());
	protected static long _optimazationTotalAgentsCounters = 0;
	protected static int _agentCounter = 0;

	private boolean scheduled = false;
	private Agent archetypeReference = null;
	public int generationsSinceLastMating = Integer.MAX_VALUE;

	private HashMap<String, Integer> intPropertyMap = new HashMap<String, Integer>();

	public Simulation sim;

	private int agentId = UNASSIGNED_AGENT_ID;

	private Team team = null;

	protected int agentStepCounter;

	protected int maxSteps;

	public int x;

	public int y;

	protected EnumSet<InteractionMechanism> interactionMechanisms = EnumSet
			.noneOf(InteractionMechanism.class);

	protected Species species;

	protected boolean isCarryingResource;

	protected ResourceState stateOfCarriedResource;

	protected ResourceStatus statusOfCarriedResource = new ResourceStatus();

	protected int previousX;

	protected int previousY;

	protected boolean locationIsCollectionSite;

	protected boolean locationHasRawResource;

	protected boolean locationHasExtractedResource;

	protected boolean locationHasProcessedResource;

	protected boolean locationHasExtractorReward;

	protected boolean locationHasDetectorReward;

	protected boolean locationHasProcessorReward;

	protected boolean locationHasTrail;

	protected double fitness = 0.0;

	protected int generation;

	protected VirtualMachine virtualMachine;

	protected Program program;

	public EventStats eventStats = new EventStats();

	protected boolean providedFeedback = false;

	public Unicast receivedUnicastA = new Unicast();
	public Unicast receivedUnicastB = new Unicast();
	public Unicast receivedUnicastC = new Unicast();

	private boolean hosted = false;
	private Agent hostAgent = null;

	static {
		LogUtils.applyDefaultSettings(logger, Main.settings.REQUESTED_LOG_LEVEL);
	}

	abstract public double doubleValue();

	abstract public void stepAction(SimState state);

	protected void initGenotype() {
		this.program = Program.Factory.createRandom(sim.random);
		VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this,
				sim.random);
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

	protected Agent(Simulation simulation, Species species,
			int generationNumber, int maxSteps, int startX, int startY) {
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
		int newY = sim.agentGrid.sty(location.y + deltaY);

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
					&& xMod < sim.getGridWidth() && yMod >= 0
					&& yMod < sim.getGridHeight()
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
				if (locationHasObstacle(newX, newY)) {
					_operationRandomMove();
				} else {
					_operationMoveAbsolute(adjustedX, adjustedY);
				}
			}
		}
	}

	public void _operationLeaveTrail(DoubleGrid2D trailGridParam) {
		Int2D location = sim.agentGrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		if (Main.settings.INTERACTION_QUALITY == InteractionQuality.HIGH) {
			trailGridParam.field[x][y] = Constants.TRAIL_LEVEL_MAX;
		} else if (Main.settings.INTERACTION_QUALITY == InteractionQuality.MEDIUM) {
			if (sim.random.nextDouble() <= 0.9) {
				trailGridParam.field[x][y] = Constants.TRAIL_LEVEL_MAX * 0.9;
			}
		} else {
			if (sim.random.nextDouble() <= 0.5) {
				trailGridParam.field[x][y] = Constants.TRAIL_LEVEL_MAX * 0.5;
			}
		}
		sim.recordEvent(this, Event.SENT_TRAIL, "" + this.agentId, NA);
	}

	public void _operationLeaveRewards(DoubleGrid2D rewardGrid,
			Event rewardEvent) {
		if (Main.settings.PEER_REWARDS) {

			// rewardGrid.field[x][y] = Constants.REWARD_LEVEL_MAX;
			rewardGrid.setTo(Constants.REWARD_LEVEL_MAX);
			sim.recordEvent(this, rewardEvent, "" + this.agentId, NA);

		}

	}

	public final boolean _operationPerformResourceAction(Task action,
			ObjectGrid2D resourceGrid) {

		boolean actionPerformed = false;

		ResourceState resourceState = (ResourceState) resourceGrid.field[x][y];

		switch (action) {
		case EXTRACTION:
			if (resourceState == ResourceState.RAW) {
				resourceGrid.field[x][y] = ResourceState.EXTRACTED;
				sim.resourceStatusArray[x][y].state = ResourceState.EXTRACTED;
				sim.resourceStatusArray[x][y].extractionStep = sim.simStepCounter;
				sim.touchedResources.add(sim.resourceStatusArray[x][y]);
				actionPerformed = true;
			}
			break;
		case PROCESSING:
			if (resourceState == ResourceState.EXTRACTED) {
				resourceGrid.field[x][y] = ResourceState.PROCESSED;
				sim.resourceStatusArray[x][y].state = ResourceState.PROCESSED;
				sim.resourceStatusArray[x][y].processingStep = sim.simStepCounter;
				sim.touchedResources.add(sim.resourceStatusArray[x][y]);
				actionPerformed = true;
			}
			break;
		default:
			logger.severe("_operationPerformResourceAction: action is invalid -- should have never come here");
			System.exit(-1);
		}

		return actionPerformed;
	}

	public void operationFollowBroadcast(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				Broadcast broadcast = sim.getRegisteredBroadcast(signalType);
				if (broadcast != null) {
					// logger.info("moving to a broadcast:" + signalType);

					if (signalType == SignalType.SIGNAL_A) {
						sim.recordEvent(this, Event.RECEIVED_BROADCAST_A, NA,
								"" + this.agentId);
					} else if (signalType == SignalType.SIGNAL_B) {
						sim.recordEvent(this, Event.RECEIVED_BROADCAST_B, NA,
								"" + this.agentId);
					} else if (signalType == SignalType.SIGNAL_C) {
						sim.recordEvent(this, Event.RECEIVED_BROADCAST_C, NA,
								"" + this.agentId);
					}

					_operationMoveAbsolute(broadcast.getX(), broadcast.getY());

				}
			}
		}
	}

	public void operationBroadcast(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				Broadcast broadcast = new Broadcast(this, signalType, this.x,
						this.y, 0);
				sim.registerBroadcast(broadcast);

				if (signalType == SignalType.SIGNAL_A) {
					sim.recordEvent(this, Event.SENT_BROADCAST_A, ""
							+ this.agentId, NA);
				} else if (signalType == SignalType.SIGNAL_B) {
					sim.recordEvent(this, Event.SENT_BROADCAST_B, ""
							+ this.agentId, NA);
				} else if (signalType == SignalType.SIGNAL_C) {
					sim.recordEvent(this, Event.SENT_BROADCAST_C, ""
							+ this.agentId, NA);
				}
			}
		}
	}

	public boolean operationDetectBroadcast(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		boolean result = false;

		if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				Broadcast broadcast = sim.getRegisteredBroadcast(signalType);

				if (broadcast != null) {
					if (signalType == SignalType.SIGNAL_A) {
						sim.recordEvent(this, Event.RECEIVED_BROADCAST_A, NA,
								"" + this.agentId);
					} else if (signalType == SignalType.SIGNAL_B) {
						sim.recordEvent(this, Event.RECEIVED_BROADCAST_B, NA,
								"" + this.agentId);
					} else if (signalType == SignalType.SIGNAL_C) {
						sim.recordEvent(this, Event.RECEIVED_BROADCAST_C, NA,
								"" + this.agentId);
					}

					result = true;
				}
			}
		}
		return result;
	}

	public void operationUnicastClosest(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms
				.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				// find the closest agent...
				// Agent closestAgent = findClosestModelAgent(this.x, this.y);
				Agent closestAgent = findClosestAgent(this.x, this.y, 1);

				if (closestAgent != null) {

					// if this is a hosted agent, we'll UNICAST to the host
					if (closestAgent.isHosted()) {
						closestAgent = closestAgent.getHostAgent();
					}

					Unicast targetUnicast = null;
					Agent senderAgent = this;
					if (this.isHosted()) {
						senderAgent = this.getHostAgent();
					}

					if (signalType == SignalType.SIGNAL_A) {
						targetUnicast = closestAgent.receivedUnicastA;
						sim.recordEvent(this, Event.SENT_UNICAST_A_CLOSEST, ""
								+ senderAgent.agentId, ""
								+ closestAgent.agentId);
					} else if (signalType == SignalType.SIGNAL_B) {
						targetUnicast = closestAgent.receivedUnicastB;
						sim.recordEvent(this, Event.SENT_UNICAST_B_CLOSEST, ""
								+ senderAgent.agentId, ""
								+ closestAgent.agentId);
					} else if (signalType == SignalType.SIGNAL_C) {
						targetUnicast = closestAgent.receivedUnicastC;
						sim.recordEvent(this, Event.SENT_UNICAST_C_CLOSEST, ""
								+ senderAgent.agentId, ""
								+ closestAgent.agentId);
					}

					if (targetUnicast != null) {

						boolean doUnicast = true;

						if (Main.settings.INTERACTION_QUALITY == InteractionQuality.MEDIUM) {
							if (sim.random.nextDouble() <= 0.9) {
								doUnicast = true;
							}
						} else if (Main.settings.INTERACTION_QUALITY == InteractionQuality.POOR) {
							if (sim.random.nextDouble() <= 0.5) {
								doUnicast = true;
							}
						}

						if (doUnicast) {
							targetUnicast.setReceiverAgent(closestAgent);
							targetUnicast.setSenderAgent(senderAgent);
							targetUnicast.setSignalType(signalType);
							targetUnicast.setStepClock(-1);
						}
					}

					/*
					 * D.p("Unicast:" + targetUnicast + "(" + closestAgent.x +
					 * "," + closestAgent.y + "," + senderAgent.x + "," +
					 * senderAgent.y + ")" + " distance:" +
					 * distance(closestAgent.x, closestAgent.y, senderAgent.x,
					 * senderAgent.y));
					 */
				}

			}
		}
	}

	public void operationFollowUnicastClosest(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms
				.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				Unicast targetUnicast = null;

				if (signalType == SignalType.SIGNAL_A) {
					if (this.isHosted()) {
						targetUnicast = this.getHostAgent().receivedUnicastA;
					} else {
						targetUnicast = this.receivedUnicastA;
					}

					if (targetUnicast.getSenderAgent() != null) {
						sim.recordEvent(this, Event.RECEIVED_UNICAST_A_CLOSEST,
								"" + targetUnicast.getSenderAgent().getId(), ""
										+ targetUnicast.getReceiverAgent()
												.getId());
					}
				} else if (signalType == SignalType.SIGNAL_B) {
					if (this.isHosted()) {
						targetUnicast = this.getHostAgent().receivedUnicastB;
					} else {
						targetUnicast = this.receivedUnicastB;
					}

					if (targetUnicast.getSenderAgent() != null) {
						sim.recordEvent(this, Event.RECEIVED_UNICAST_B_CLOSEST,
								"" + targetUnicast.getSenderAgent().getId(), ""
										+ targetUnicast.getReceiverAgent()
												.getId());
					}
				} else if (signalType == SignalType.SIGNAL_C) {
					if (this.isHosted()) {
						targetUnicast = this.getHostAgent().receivedUnicastC;
					} else {
						targetUnicast = this.receivedUnicastC;
					}

					if (targetUnicast.getSenderAgent() != null) {
						sim.recordEvent(this, Event.RECEIVED_UNICAST_C_CLOSEST,
								"" + targetUnicast.getSenderAgent().getId(), ""
										+ targetUnicast.getReceiverAgent()
												.getId());
					}
				}

				if (targetUnicast.getSenderAgent() != null) {
					_operationMoveAbsolute(targetUnicast.getSenderX(),
							targetUnicast.getSenderY());
				}

			}
		}
	}

	public boolean operationDetectUnicastClosest(SignalType signalType) {
		boolean result = false;
		if (interactionMechanisms
				.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS && signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				Unicast targetUnicast = null;

				if (signalType == SignalType.SIGNAL_A) {
					if (this.isHosted()) {
						targetUnicast = this.getHostAgent().receivedUnicastA;
					} else {
						targetUnicast = this.receivedUnicastA;
					}

					if (targetUnicast.getSenderAgent() != null) {
						sim.recordEvent(this, Event.RECEIVED_UNICAST_A_CLOSEST,
								"" + targetUnicast.getSenderAgent().getId(), ""
										+ targetUnicast.getReceiverAgent()
												.getId());
						result = true;
					}
				} else if (signalType == SignalType.SIGNAL_B) {
					if (this.isHosted()) {
						targetUnicast = this.getHostAgent().receivedUnicastB;
					} else {
						targetUnicast = this.receivedUnicastB;
					}

					if (targetUnicast.getSenderAgent() != null) {
						sim.recordEvent(this, Event.RECEIVED_UNICAST_B_CLOSEST,
								"" + targetUnicast.getSenderAgent().getId(), ""
										+ targetUnicast.getReceiverAgent()
												.getId());
						result = true;
					}
				} else if (signalType == SignalType.SIGNAL_C) {
					if (this.isHosted()) {
						targetUnicast = this.getHostAgent().receivedUnicastC;
					} else {
						targetUnicast = this.receivedUnicastC;
					}
					if (targetUnicast.getSenderAgent() != null) {
						sim.recordEvent(this, Event.RECEIVED_UNICAST_C_CLOSEST,
								"" + targetUnicast.getSenderAgent().getId(), ""
										+ targetUnicast.getReceiverAgent()
												.getId());
						result = true;
					}
				}

			}
		}

		return result;
	}

	public final void _operationFollowTrail(DoubleGrid2D trail) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
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
					int scanY = trail.sty(this.y + deltaY);

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
			sim.recordEvent(this, Event.RECEIVED_TRAIL, NA, "" + this.agentId);
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
					int scanY = trailA.sty(this.y + deltaY);

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
		_operationMoveToLocationAt(sim.settings.PRIMARY_COLLECTION_SITE_X,
				sim.settings.PRIMARY_COLLECTION_SITE_Y);
		sim.recordEvent(this, Event.MOVE_TO_CLOSEST_COLLECTION_SITE, NA, NA);

	}

	public final void operationMoveToClosestCollectionSite() {

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
		sim.recordEvent(this, Event.MOVE_TO_CLOSEST_COLLECTION_SITE, NA, NA);

	}

	/** This function is being "borrowed" from Int2D.java */
	public final double distance(final double x1, final double y1,
			final double x2, final double y2) {
		final double dx = x1 - x2;
		final double dy = y1 - y2;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Find the closest agent to x and y
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public final Agent findClosestAgent(int x, int y, int minimumDistance) {
		// TODO: find out why some agents can be in the same location
		// (minimumDistance=0) -- this maybe valid

		Agent closestAgent = null;
		Bag agentBag = sim.agentGrid.getAllObjects();
		double closestDistance = Double.MAX_VALUE;

		for (int i = 0; i < agentBag.numObjs; i++) {

			Agent agent = (Agent) agentBag.get(i);
			if (agent == this) {
				continue;
			}
			if (this.isHosted() && agent == this.getHostAgent()) {
				continue;
			}

			if (agent.isHosted() && agent.getHostAgent() == this) {
				continue;
			}

			double distance = distance(agent.x, agent.y, this.x, this.y);
			if (distance >= minimumDistance && distance < closestDistance) {
				closestAgent = agent;
				closestDistance = distance;
			}

		}

		return closestAgent;
	}

	public final void operationMoveToClosestAgent() {
		Agent closestAgent = findClosestAgent(this.x, this.y, 1);
		if (closestAgent != null) {
			_operationMoveToLocationAt(closestAgent.x, closestAgent.y);
			sim.recordEvent(this, Event.MOVE_TO_CLOSEST_AGENT, NA, NA);

		}
	}

	public final void operationFollowTrail() {
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			_operationFollowTrail(sim.trailGrid);
			// sim.statistics.stepData.trailFollows++;
		}
	}

	public void operationLeaveTrail() {
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			_operationLeaveTrail(sim.trailGrid);
			updateLocationStatus(this.x, this.y);

		}
	}

	public final boolean operationDetectCollectionSite() {

		return this.locationIsCollectionSite;

	}

	public final boolean operationDetectRawResource() {
		sim.recordEvent(this, Event.ATTEMPT_DETECT, NA, NA);
		if (species.getTraits().contains(Trait.DETECTION)) {
			updateLocationStatus(x, y);
			if (this.locationHasRawResource) {
				sim.recordEvent(this, Event.DETECTED_RAW_RESOURCE, NA, NA);
				if (sim.resourceStatusArray[x][y].detectionStep == INVALID) {
					sim.resourceStatusArray[x][y].detectionStep = sim.simStepCounter;
				}
				sim.resourceStatusArray[x][y].numTimesDetected++;
				sim.touchedResources.add(sim.resourceStatusArray[x][y]);
			}
			return (this.locationHasRawResource);
		} else {
			return false;
		}

	}

	public final boolean operationDetectExtractedResource() {
		sim.recordEvent(this, Event.ATTEMPT_DETECT, NA, NA);
		if (species.getTraits().contains(Trait.DETECTION)) {
			updateLocationStatus(x, y);
			if (this.locationHasExtractedResource) {
				sim.recordEvent(this, Event.DETECTED_EXTRACTED_RESOURCE, NA, NA);
				if (sim.resourceStatusArray[x][y].detectionStep == INVALID) {
					sim.resourceStatusArray[x][y].detectionStep = sim.simStepCounter;
				}
				sim.resourceStatusArray[x][y].numTimesDetected++;
				sim.touchedResources.add(sim.resourceStatusArray[x][y]);
			}
			return this.locationHasExtractedResource;
		} else {
			return false;
		}
	}

	public boolean operationDetectProcessedResource() {
		if (species.getTraits().contains(Trait.DETECTION)) {
			sim.recordEvent(this, Event.ATTEMPT_DETECT, NA, NA);
			updateLocationStatus(x, y);
			if (this.locationHasProcessedResource) {
				sim.recordEvent(this, Event.DETECTED_PROCESSED_RESOURCE, NA, NA);
				if (sim.resourceStatusArray[x][y].detectionStep == INVALID) {
					sim.resourceStatusArray[x][y].detectionStep = sim.simStepCounter;
				}
				sim.resourceStatusArray[x][y].numTimesDetected++;
				sim.touchedResources.add(sim.resourceStatusArray[x][y]);
			}
			return this.locationHasProcessedResource;
		} else {
			return false;
		}
	}

	public final boolean operationDetectTrail() {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			updateLocationStatus(x, y);
			if (this.locationHasTrail) {
				sim.recordEvent(this, Event.RECEIVED_TRAIL, NA, ""
						+ this.agentId);
			}
			return this.locationHasTrail;
		} else {
			return false;
		}
	}

	public final void operationExtractResource() {
		sim.recordEvent(this, Event.ATTEMPT_EXTRACT, NA, NA);
		if (species.getTraits().contains(Trait.EXTRACTION)) {

			if (_operationPerformResourceAction(Task.EXTRACTION,
					this.sim.resourceGrid)) {
				// sim.statistics.stepData.resourceExtracts++;
				sim.recordEvent(this, Event.EXTRACTED_RESOURCE, NA, NA);
				_operationLeaveRewards(sim.detectorRewardGrid,
						Event.DROPPED_DETECTOR_REWARDS);

			}

			updateLocationStatus(this.x, this.y);
		}
	}

	public void operationProcessResource() {
		sim.recordEvent(this, Event.ATTEMPT_PROCESS, NA, NA);
		if (species.getTraits().contains(Trait.PROCESSING)) {

			if (_operationPerformResourceAction(Task.PROCESSING,
					this.sim.resourceGrid)) {
				// sim.statistics.stepData.resourceProcesses++;
				sim.recordEvent(this, Event.PROCESSED_RESOURCE, NA, NA);
				_operationLeaveRewards(sim.extractorRewardGrid,
						Event.DROPPED_EXTRACTOR_REWARDS);
			}

			updateLocationStatus(this.x, this.y);
		}

	}

	public final void operationLoadResource() {
		sim.recordEvent(this, Event.ATTEMPT_TRANSPORT, NA, NA);
		if (species.getTraits().contains(Trait.TRANSPORTATION)) {
			updateLocationStatus(this.x, this.y);
			if (locationHasExtractedResource || locationHasProcessedResource
					|| locationHasRawResource) {
				if (!isCarryingResource) {
					isCarryingResource = true;
					stateOfCarriedResource = (ResourceState) this.sim.resourceGrid.field[x][y];

					this.sim.resourceGrid.field[x][y] = ResourceState.NULL;

					// copy over the state of the resource
					sim.resourceStatusArray[x][y]
							.cloneTo(statusOfCarriedResource);

					statusOfCarriedResource.numTimesLoaded++;

					// now clear the resource
					sim.resourceStatusArray[x][y].clear();
					sim.resourceStatusArray[x][y].state = ResourceState.NULL;

					// need to keep track of this resource...
					sim.touchedResources.remove(sim.resourceStatusArray[x][y]);
					sim.touchedResources.add(statusOfCarriedResource);

					updateLocationStatus(this.x, this.y);
					sim.recordEvent(this, Event.LOADED_RESOURCE, NA, NA);
				}
			}

		}
	}

	public final void operationUnLoadResource() {
		sim.recordEvent(this, Event.ATTEMPT_TRANSPORT, NA, NA);
		if (species.getTraits().contains(Trait.TRANSPORTATION)
				&& isCarryingResource) {
			updateLocationStatus(x, y);
			if (!locationHasExtractedResource && !locationHasProcessedResource
					&& !locationHasRawResource) {

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
							sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA,
									NA);
							sim.recordEvent(this, Event.COLLECTED_RESOURCE, NA,
									NA);
							_operationLeaveRewards(sim.extractorRewardGrid,
									Event.DROPPED_EXTRACTOR_REWARDS);

							// save over this status
							ResourceStatus collectedResourceStatus = new ResourceStatus();
							statusOfCarriedResource
									.cloneTo(collectedResourceStatus);
							collectedResourceStatus.captureStep = sim.simStepCounter;
							sim.touchedResources
									.remove(statusOfCarriedResource);
							statusOfCarriedResource.clear();
							sim.touchedResources.add(collectedResourceStatus);

							// logger.info("CAPTURE!! 3 complex");
						}

					} else if (this.sim.problemComplexity
							.equals(ProblemComplexity.FOUR_SEQUENTIAL_TASKS)) {
						if (stateOfCarriedResource == ResourceState.PROCESSED) {
							dropResource = false;
							this.sim.numberOfCollectedResources++;
							sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA,
									NA);
							sim.recordEvent(this, Event.COLLECTED_RESOURCE, NA,
									NA);
							_operationLeaveRewards(sim.processorRewardGrid,
									Event.DROPPED_PROCESSOR_REWARDS);

							// save over this status
							ResourceStatus collectedResourceStatus = new ResourceStatus();
							statusOfCarriedResource
									.cloneTo(collectedResourceStatus);
							collectedResourceStatus.captureStep = sim.simStepCounter;
							sim.touchedResources
									.remove(statusOfCarriedResource);
							statusOfCarriedResource.clear();
							sim.touchedResources.add(collectedResourceStatus);

							// logger.info("CAPTURE!! 4 complex");

						}

					} else {
						logger.severe("Problem Complexity Unknown!");
						System.exit(-1);
					}

				}

				if (dropResource) {
					this.sim.resourceGrid.field[x][y] = stateOfCarriedResource;

					// this is a resource drop...place the resource back in the
					// grid
					statusOfCarriedResource
							.cloneTo(this.sim.resourceStatusArray[x][y]);
					this.sim.resourceStatusArray[x][y].x = x;
					this.sim.resourceStatusArray[x][y].y = y;
					this.sim.resourceStatusArray[x][y].numTimesUnloaded++;

					// need to keep track of this resource...
					sim.touchedResources.remove(statusOfCarriedResource);
					sim.touchedResources
							.add(this.sim.resourceStatusArray[x][y]);

					statusOfCarriedResource.clear();

					sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);

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
		eventStats.clear();
	}

	public void updateLocationStatus(int x, int y) {

		this.x = x;
		this.y = y;

		boolean locationIsChanging = (x != previousX || y != previousY);

		this.locationHasExtractedResource = false;
		this.locationHasProcessedResource = false;
		this.locationHasRawResource = false;
		this.locationHasExtractorReward = false;
		this.locationHasDetectorReward = false;
		this.locationHasProcessorReward = false;

		if (sim.resourceGrid.field[x][y] == ResourceState.RAW) {
			this.locationHasRawResource = true;
		} else if (sim.resourceGrid.field[x][y] == ResourceState.EXTRACTED) {
			this.locationHasExtractedResource = true;
		} else if (sim.resourceGrid.field[x][y] == ResourceState.PROCESSED) {
			this.locationHasProcessedResource = true;
		}

		if (sim.extractorRewardGrid.field[x][y] >= 1.0) {
			this.locationHasExtractorReward = true;
			if (Main.settings.PEER_REWARDS) {
				sim.recordEvent(this, Event.RECEIVED_EXTRACTOR_REWARDS, NA, ""
						+ this.agentId);
			}
		}

		if (sim.detectorRewardGrid.field[x][y] >= 1.0) {
			this.locationHasDetectorReward = true;
			if (Main.settings.PEER_REWARDS) {
				sim.recordEvent(this, Event.RECEIVED_DETECTOR_REWARDS, NA, ""
						+ this.agentId);
			}
		}

		if (sim.processorRewardGrid.field[x][y] >= 1.0) {
			this.locationHasProcessorReward = true;
			if (Main.settings.PEER_REWARDS) {
				sim.recordEvent(this, Event.RECEIVED_PROCESSOR_REWARDS, NA, ""
						+ this.agentId);
			}
		}

		if (sim.trailGrid.field[x][y] >= 1) {
			this.locationHasTrail = true;
			if (locationIsChanging) {
				sim.recordEvent(this, Event.RECEIVED_TRAIL, NA, ""
						+ this.agentId);
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

	public void setProgram(Program p) {
		this.program = p;
	}

	public String getSignature() {
		String result = "" + this.getAgentId();
		if (this.program != null) {
			result += ":" + this.program.getSignature();

		}
		return result;

	}

	public String getFingerPrint() {
		String result = "" + this.getAgentId();
		if (this.program != null) {
			result += ":" + this.program.getFingerPrint();

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

	protected static Agent generateAgent(long generation, long agentId, int x,
			int y) {
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

	public void setInteractionMechanisms(
			EnumSet<InteractionMechanism> interactionMechanisms) {
		this.interactionMechanisms = interactionMechanisms;
	}

	public EnumSet<InteractionMechanism> getInteractionMechanisms() {
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
		this.getVirtualMachine().setCpuCycles(
				archetype.getVirtualMachine().getCpuCycles());
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
		return "Agent [agentId=" + agentId + ", teamId="
				+ ((team != null) ? team.getTeamId() : -1)
				+ ", agentStepCounter=" + agentStepCounter + ", maxSteps="
				+ maxSteps + ", x=" + x + ", y=" + y
				+ ", interactionMechanisms=" + interactionMechanisms
				+ ", species=" + species + ", isCarryingResource="
				+ isCarryingResource + ", generation=" + generation
				+ ", program=" + program + "]";
	}

	public String toString2() {
		return "Agent [agentId=" + agentId + ", teamId="
				+ ((team != null) ? team.getTeamId() : -1)
				+ ", agentStepCounter=" + agentStepCounter + ", maxSteps="
				+ maxSteps + ", x=" + x + ", y=" + y
				+ ", interactionMechanisms=" + interactionMechanisms
				+ ", species=" + species + ", isCarryingResource="
				+ isCarryingResource + ", generation=" + generation
				+ ", program=" + program.getSignature() + "]";
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

	public boolean isHosted() {
		return hosted;
	}

	public void setHosted(boolean hosted) {
		this.hosted = hosted;
	}

	public Agent getHostAgent() {
		return hostAgent;
	}

	public void setHostAgent(Agent hostAgent) {
		this.hostAgent = hostAgent;
	}

}
