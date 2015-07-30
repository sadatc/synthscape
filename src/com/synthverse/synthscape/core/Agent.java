package com.synthverse.synthscape.core;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Logger;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.MutableDouble;
import sim.util.Valuable;

import com.synthverse.Main;
import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.util.GridUtils;
import com.synthverse.util.LogUtils;

public abstract class Agent implements Constants, Steppable, Valuable, Comparable<Agent> {

	public InteractionMode interactionMode = InteractionMode.NONE;
	public boolean __capturedResource = false;

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

	protected EnumSet<InteractionMechanism> interactionMechanisms = EnumSet.noneOf(InteractionMechanism.class);

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

	public Unicast receivedUnicastA = null;
	public Unicast receivedUnicastB = null;
	public Unicast receivedUnicastC = null;

	public boolean detectedBroadcastA = false;
	public boolean detectedBroadcastB = false;
	public boolean detectedBroadcastC = false;

	public boolean detectedUnicastA = false;
	public boolean detectedUnicastB = false;
	public boolean detectedUnicastC = false;

	private boolean isProxyAgent = false;
	private Agent hostAgent = null;

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
		// also reset the interaction mode
		interactionMode = InteractionMode.NONE;
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

	protected Agent(Simulation simulation, Species species, int generationNumber, int maxSteps, int startX,
			int startY) {
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
		// TODO: is another agent in the same location considered to be a an
		// obstacle?
		return (GridUtils.gridHasAnObjectAt(sim.obstacleGrid, x, y));
		// return locationHasObstacleOrAgent(x, y);
	}

	public final boolean locationHasObstacleOrAgent(int x, int y) {

		return (GridUtils.gridHasAnObjectAt(sim.obstacleGrid, x, y)
				|| GridUtils.gridHasAnObjectAt(sim.agentGrid, x, y));
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

			if (!locationHasObstacle(newX, newY)) {

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
			if (!locationHasObstacle(newX, newY)) {

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
		long searchCount = 0;
		do {
			searchCount++;
			int xDelta = (sim.random.nextInt(3) - 1);
			int yDelta = (sim.random.nextInt(3) - 1);
			int xMod = sim.agentGrid.stx(x + xDelta);
			int yMod = sim.agentGrid.sty(y + yDelta);

			if (!(xDelta == 0 && yDelta == 0) && xMod >= 0 && xMod < sim.getGridWidth() && yMod >= 0
					&& yMod < sim.getGridHeight() && (!locationHasObstacle(xMod, yMod))) {
				newX = xMod;
				newY = yMod;
				foundNewUblockedLocation = true;
			}
			if (searchCount > Constants.MAX_TRIES_TO_FIND_EMPTY_MOVE_LOCATION) {
				// can't find new location, give up.
				break;
			}
		} while (!foundNewUblockedLocation);

		if (foundNewUblockedLocation) {
			_operationMoveAbsolute(newX, newY);
		}
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

	public void _operationLeaveTrail(TrailGridWrapper trailGridWrapper) {
		Int2D location = sim.agentGrid.getObjectLocation(this);
		int x = location.x;
		int y = location.y;

		if (Main.settings.INTERACTION_QUALITY == InteractionQuality.HIGHEST) {
			trailGridWrapper.setTrail(x, y, Constants.TRAIL_LEVEL_MAX);
			sim.recordEvent(this, Event.SENT_TRAIL, this.agentId, NA);
		} else {
			InteractionQuality interactionQualitySetting = Main.settings.INTERACTION_QUALITY;
			if (sim.random.nextDouble() <= interactionQualitySetting.getLevel()) {
				trailGridWrapper.setTrail(x, y, Constants.TRAIL_LEVEL_MAX);
				sim.recordEvent(this, Event.SENT_TRAIL, this.agentId, NA);

			}

		}

	}

	public void _operationLeaveRewards(MutableDouble rewardHolder, Event rewardEvent) {
		if (Main.settings.PEER_REWARDS) {

			rewardHolder.val = Constants.REWARD_LEVEL_MAX;

			sim.recordEvent(this, rewardEvent, this.agentId, NA);

		}

	}
	public final boolean _operationPerformResourceAction(Task action, SparseGrid2D resourceGrid) {

		boolean actionPerformed = false;

		ResourceState resourceState = GridUtils.getResourceState(resourceGrid, x, y);

		switch (action) {
			case EXTRACTION :
				if (resourceState == ResourceState.RAW) {

					GridUtils.set(resourceGrid, x, y, ResourceState.EXTRACTED);
					sim.resourceStatusArray[x][y].state = ResourceState.EXTRACTED;
					sim.resourceStatusArray[x][y].extractionStep = sim.simStepCounter;
					sim.touchedResources.add(sim.resourceStatusArray[x][y]);
					actionPerformed = true;
				}
				break;
			case PROCESSING :
				if (resourceState == ResourceState.EXTRACTED) {
					GridUtils.set(resourceGrid, x, y, ResourceState.PROCESSED);
					sim.resourceStatusArray[x][y].state = ResourceState.PROCESSED;
					sim.resourceStatusArray[x][y].processingStep = sim.simStepCounter;
					sim.touchedResources.add(sim.resourceStatusArray[x][y]);
					actionPerformed = true;
				}
				break;
			default :
				logger.severe("_operationPerformResourceAction: action is invalid -- should have never come here");
				System.exit(-1);
		}

		return actionPerformed;
	}

	public void operationFollowBroadcast(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
					&& signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				Broadcast broadcast = sim.getRegisteredBroadcast(signalType);
				if (broadcast != null) {

					if (Main.settings.CONSTRAINED_INTERACTIONS) {
						// constrained version:
						// 3 task: extractor=A, transporter=B
						// 4 task: extractor=A, processor=B, transporter=C

						Event event = null;

						if (this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS) {
							// 3 task problem

							if (species.getTraits().contains(Trait.EXTRACTION) && signalType == SignalType.SIGNAL_A) {

								event = Event.RECEIVED_BROADCAST_A;

							} else
								if (species.getTraits().contains(Trait.TRANSPORTATION)
										&& signalType == SignalType.SIGNAL_B) {

								event = Event.RECEIVED_BROADCAST_B;
							} else {
								signalType = null;

							}

						} else {
							// 4 task problem
							if (species.getTraits().contains(Trait.EXTRACTION) && signalType == SignalType.SIGNAL_A) {

								event = Event.RECEIVED_BROADCAST_A;
							} else
								if (species.getTraits().contains(Trait.PROCESSING)
										&& signalType == SignalType.SIGNAL_B) {

								event = Event.RECEIVED_BROADCAST_B;
							} else
									if (species.getTraits().contains(Trait.TRANSPORTATION)
											&& signalType == SignalType.SIGNAL_C) {

								event = Event.RECEIVED_BROADCAST_C;
							} else {
								signalType = null;

							}
						}
						if (signalType != null && event != null) {

							sim.recordEvent(this, event, NA, this.agentId);

							Agent detectorAgent = this;
							if (this.isProxyAgent()) {
								detectorAgent = this.getHostAgent();
							}
							if (signalType == SignalType.SIGNAL_A) {
								detectorAgent.detectedBroadcastA = true;
							} else if (signalType == SignalType.SIGNAL_B) {
								detectorAgent.detectedBroadcastB = true;
							} else if (signalType == SignalType.SIGNAL_C) {
								detectorAgent.detectedBroadcastC = true;
							}

							_operationMoveAbsolute(broadcast.getX(), broadcast.getY());
							broadcast.markReceived();
						}

					} else {

						// unconstrained version
						if (signalType == SignalType.SIGNAL_A) {
							sim.recordEvent(this, Event.RECEIVED_BROADCAST_A, NA, this.agentId);
						} else if (signalType == SignalType.SIGNAL_B) {
							sim.recordEvent(this, Event.RECEIVED_BROADCAST_B, NA, this.agentId);
						} else if (signalType == SignalType.SIGNAL_C) {
							sim.recordEvent(this, Event.RECEIVED_BROADCAST_C, NA, this.agentId);
						}

						_operationMoveAbsolute(broadcast.getX(), broadcast.getY());
						broadcast.markReceived();

					}

				}
			}
		}
	}

	public void operationBroadcast(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
					&& signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				if (Main.settings.CONSTRAINED_INTERACTIONS) {
					// constrained version
					// detectors can send signal A only
					// extractors can send signal B only
					// processors can send signal C only
					SignalType finalSignalType = null;
					Event event = null;
					if (species.getTraits().contains(Trait.DETECTION) && signalType == SignalType.SIGNAL_A) {
						finalSignalType = SignalType.SIGNAL_A;
						event = Event.SENT_BROADCAST_A;
					}
					if (species.getTraits().contains(Trait.EXTRACTION) && signalType == SignalType.SIGNAL_B) {
						finalSignalType = SignalType.SIGNAL_B;
						event = Event.SENT_BROADCAST_B;
					}

					if (species.getTraits().contains(Trait.EXTRACTION) && signalType == SignalType.SIGNAL_C) {
						finalSignalType = SignalType.SIGNAL_C;
						event = Event.SENT_BROADCAST_C;
					}
					if (finalSignalType != null && event != null) {
						Broadcast broadcast = new Broadcast(this, finalSignalType, this.x, this.y, 0);
						sim.registerBroadcast(broadcast);
						sim.recordEvent(this, event, this.agentId, NA);
					}

				} else {

					// unconstrained version
					Broadcast broadcast = new Broadcast(this, signalType, this.x, this.y, 0);
					sim.registerBroadcast(broadcast);

					if (signalType == SignalType.SIGNAL_A) {
						sim.recordEvent(this, Event.SENT_BROADCAST_A, this.agentId, NA);
					} else if (signalType == SignalType.SIGNAL_B) {
						sim.recordEvent(this, Event.SENT_BROADCAST_B, this.agentId, NA);
					} else if (signalType == SignalType.SIGNAL_C) {
						sim.recordEvent(this, Event.SENT_BROADCAST_C, this.agentId, NA);
					}
				}

			}
		}
	}

	public boolean operationDetectBroadcast(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		boolean result = false;

		if (interactionMechanisms.contains(InteractionMechanism.BROADCAST)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
					&& signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				Broadcast broadcast = sim.getRegisteredBroadcast(signalType);

				if (broadcast != null) {

					if (Main.settings.CONSTRAINED_INTERACTIONS) {
						// constrained version:
						// 3 task: extractor=A, transporter=B
						// 4 task: extractor=A, processor=B, transporter=C

						SignalType finalSignalType = null;
						Event event = null;

						if (this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS) {
							// 3 task problem

							if (species.getTraits().contains(Trait.EXTRACTION) && signalType == SignalType.SIGNAL_A) {
								finalSignalType = SignalType.SIGNAL_A;
								event = Event.RECEIVED_BROADCAST_A;
							}

							if (species.getTraits().contains(Trait.TRANSPORTATION)
									&& signalType == SignalType.SIGNAL_B) {
								finalSignalType = SignalType.SIGNAL_B;
								event = Event.RECEIVED_BROADCAST_B;
							}

						} else {
							// 4 task problem
							if (species.getTraits().contains(Trait.EXTRACTION) && signalType == SignalType.SIGNAL_A) {
								finalSignalType = SignalType.SIGNAL_A;
								event = Event.RECEIVED_BROADCAST_A;
							}
							if (species.getTraits().contains(Trait.PROCESSING) && signalType == SignalType.SIGNAL_B) {
								finalSignalType = SignalType.SIGNAL_B;
								event = Event.RECEIVED_BROADCAST_B;
							}
							if (species.getTraits().contains(Trait.TRANSPORTATION)
									&& signalType == SignalType.SIGNAL_C) {
								finalSignalType = SignalType.SIGNAL_C;
								event = Event.RECEIVED_BROADCAST_C;
							}
						}
						if (finalSignalType != null && event != null) {

							sim.recordEvent(this, event, NA, this.agentId);

							Agent detectorAgent = this;
							if (this.isProxyAgent()) {
								detectorAgent = this.getHostAgent();
							}
							if (signalType == SignalType.SIGNAL_A) {
								detectorAgent.detectedBroadcastA = true;
							} else if (signalType == SignalType.SIGNAL_B) {
								detectorAgent.detectedBroadcastB = true;
							} else if (signalType == SignalType.SIGNAL_C) {
								detectorAgent.detectedBroadcastC = true;
							}
							broadcast.markReceived();

							result = true;

						}

					} else {

						// unconstrained
						if (signalType == SignalType.SIGNAL_A) {
							sim.recordEvent(this, Event.RECEIVED_BROADCAST_A, NA, this.agentId);
						} else if (signalType == SignalType.SIGNAL_B) {
							sim.recordEvent(this, Event.RECEIVED_BROADCAST_B, NA, this.agentId);
						} else if (signalType == SignalType.SIGNAL_C) {
							sim.recordEvent(this, Event.RECEIVED_BROADCAST_C, NA, this.agentId);
						}
						broadcast.markReceived();

						result = true;
					}
				}
			}
		}
		return result;
	}

	public final void operationUnicastClosest_Unconstrained(SignalType signalType) {

		//
		// -- UNCONSTRAINED VERSION ---
		//
		// find the closest agent...
		Agent closestAgent = findClosestAgent(this.x, this.y, 1);

		if (closestAgent != null) {

			// if this is a isProxyAgent agent, we'll UNICAST to the host
			if (closestAgent.isProxyAgent()) {
				closestAgent = closestAgent.getHostAgent();
			}

			Unicast targetUnicast = null;
			Agent senderAgent = this;
			if (this.isProxyAgent()) {
				senderAgent = this.getHostAgent();
			}

			if (signalType == SignalType.SIGNAL_A) {
				closestAgent.receivedUnicastA = new Unicast(SignalType.SIGNAL_A);
				targetUnicast = closestAgent.receivedUnicastA;

				sim.recordEvent(this, Event.SENT_UNICAST_A_CLOSEST, senderAgent.agentId, closestAgent.agentId);
			} else if (signalType == SignalType.SIGNAL_B) {
				closestAgent.receivedUnicastB = new Unicast(SignalType.SIGNAL_B);
				targetUnicast = closestAgent.receivedUnicastB;

				sim.recordEvent(this, Event.SENT_UNICAST_B_CLOSEST, senderAgent.agentId, closestAgent.agentId);
			} else if (signalType == SignalType.SIGNAL_C) {
				closestAgent.receivedUnicastC = new Unicast(SignalType.SIGNAL_C);
				targetUnicast = closestAgent.receivedUnicastC;

				sim.recordEvent(this, Event.SENT_UNICAST_C_CLOSEST, senderAgent.agentId, closestAgent.agentId);
			}

			if (targetUnicast != null) {

				boolean doUnicast = false;
				if (Main.settings.INTERACTION_QUALITY == InteractionQuality.HIGHEST) {
					doUnicast = true;
				} else {
					InteractionQuality interactionQualitySetting = Main.settings.INTERACTION_QUALITY;
					if (sim.random.nextDouble() <= interactionQualitySetting.getLevel()) {
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
		}
		//
		// -- UNCONSTRAINED VERSION ---
		//
	}

	public final void operationUnicastClosest_Constrained(SignalType signalType) {

		//
		// -- CONSTRAINED VERSION ---
		//
		// find the closest agent...

		// constrained version
		// 3task: det sig a to ext and ext sig b to trans
		// 4task: det sig a to ext, ext sig b to proc and proc sig c
		// to trans
		SignalType finalSignlalType = null;
		Event event = null;
		Trait targetTrait = null;

		// determine if this singalType is allowed by the species designation

		if (species.getTraits().contains(Trait.DETECTION) && signalType == SignalType.SIGNAL_A) {
			finalSignlalType = SignalType.SIGNAL_A;
			event = Event.SENT_UNICAST_A_CLOSEST;
			targetTrait = Trait.EXTRACTION;
		}

		if (species.getTraits().contains(Trait.EXTRACTION)
				&& this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
				&& signalType == SignalType.SIGNAL_B) {
			finalSignlalType = SignalType.SIGNAL_B;
			event = Event.SENT_UNICAST_B_CLOSEST;
			targetTrait = Trait.TRANSPORTATION;
		}

		if (species.getTraits().contains(Trait.PROCESSING)
				&& this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS
				&& signalType == SignalType.SIGNAL_C) {
			finalSignlalType = SignalType.SIGNAL_C;
			event = Event.SENT_UNICAST_C_CLOSEST;
			targetTrait = Trait.TRANSPORTATION;
		}

		// at this point we know the signal type and the event
		// we now need to find the right target agent.
		if (finalSignlalType != null && event != null && targetTrait != null) {
			/*
			 * Broadcast broadcast = new Broadcast(this, signalType, this.x,
			 * this.y, 0); sim.registerBroadcast(broadcast);
			 * sim.recordEvent(this, event, this.agentId, NA);
			 */

			// FIXME: code here

			Agent closestAgent = findClosestAgent(this.x, this.y, 1, targetTrait);

			if (closestAgent != null) {

				// if this is a isProxyAgent agent, we'll UNICAST to the host
				if (closestAgent.isProxyAgent()) {
					closestAgent = closestAgent.getHostAgent();
				}

				Unicast targetUnicast = null;
				Agent senderAgent = this;
				if (this.isProxyAgent()) {
					senderAgent = this.getHostAgent();
				}

				if (signalType == SignalType.SIGNAL_A) {
					closestAgent.receivedUnicastA = new Unicast(SignalType.SIGNAL_A);
					targetUnicast = closestAgent.receivedUnicastA;

					sim.recordEvent(this, Event.SENT_UNICAST_A_CLOSEST, senderAgent.agentId, closestAgent.agentId);
				} else if (signalType == SignalType.SIGNAL_B) {
					closestAgent.receivedUnicastB = new Unicast(SignalType.SIGNAL_B);
					targetUnicast = closestAgent.receivedUnicastB;

					sim.recordEvent(this, Event.SENT_UNICAST_B_CLOSEST, senderAgent.agentId, closestAgent.agentId);
				} else if (signalType == SignalType.SIGNAL_C) {
					closestAgent.receivedUnicastC = new Unicast(SignalType.SIGNAL_C);
					targetUnicast = closestAgent.receivedUnicastC;

					sim.recordEvent(this, Event.SENT_UNICAST_C_CLOSEST, senderAgent.agentId, closestAgent.agentId);
				}

				if (targetUnicast != null) {

					boolean doUnicast = false;
					if (Main.settings.INTERACTION_QUALITY == InteractionQuality.HIGHEST) {
						doUnicast = true;
					} else {
						InteractionQuality interactionQualitySetting = Main.settings.INTERACTION_QUALITY;
						if (sim.random.nextDouble() <= interactionQualitySetting.getLevel()) {
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
			}
		}

	}

	public void operationUnicastClosest(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
					&& signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				if (Main.settings.CONSTRAINED_INTERACTIONS) {

					operationUnicastClosest_Constrained(signalType);

				} else {

					operationUnicastClosest_Unconstrained(signalType);

				}

			}
		}
	}

	public void operationFollowUnicastClosest(SignalType signalType) {
		sim.recordEvent(this, Event.ATTEMPT_COMMUNICATE, NA, NA);
		if (interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {

			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
					&& signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				Unicast targetUnicast = null;

				if (Main.settings.CONSTRAINED_INTERACTIONS) {
					// constrained
					Agent receivingAgent = this;
					Event event = null;

					if (this.isProxyAgent()) {
						receivingAgent = this.getHostAgent();
					}

					// determine if the appropriate unicast is being received
					// thats fit to the trait

					if (signalType == SignalType.SIGNAL_A
							&& receivingAgent.getSpecies().getTraits().contains(Trait.EXTRACTION)) {

						targetUnicast = receivingAgent.receivedUnicastA;

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							event = Event.RECEIVED_UNICAST_A_CLOSEST;
						}
					}

					if (signalType == SignalType.SIGNAL_B
							&& this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
							&& receivingAgent.getSpecies().getTraits().contains(Trait.TRANSPORTATION)) {

						targetUnicast = receivingAgent.receivedUnicastB;

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							event = Event.RECEIVED_UNICAST_B_CLOSEST;
						}
					}

					if (signalType == SignalType.SIGNAL_C
							&& this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS
							&& receivingAgent.getSpecies().getTraits().contains(Trait.TRANSPORTATION)) {

						targetUnicast = receivingAgent.receivedUnicastC;

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							event = Event.RECEIVED_UNICAST_C_CLOSEST;
						}
					}

					if (event != null && targetUnicast != null && targetUnicast.getSenderAgent() != null) {
						_operationMoveAbsolute(targetUnicast.getSenderX(), targetUnicast.getSenderY());

						sim.recordEvent(this, event, targetUnicast.getSenderAgent().getId(),
								targetUnicast.getReceiverAgent().getId());
						targetUnicast.markReceived();

						Agent detectorAgent = this;
						if (this.isProxyAgent()) {
							detectorAgent = this.getHostAgent();
						}
						if (signalType == SignalType.SIGNAL_A) {
							detectorAgent.detectedUnicastA = true;
						} else if (signalType == SignalType.SIGNAL_B) {
							detectorAgent.detectedUnicastB = true;
						} else if (signalType == SignalType.SIGNAL_C) {
							detectorAgent.detectedUnicastC = true;
						}

					}
					// end of constrained

				} else {

					// unconstrained
					if (signalType == SignalType.SIGNAL_A) {
						if (this.isProxyAgent()) {
							targetUnicast = this.getHostAgent().receivedUnicastA;
						} else {
							targetUnicast = this.receivedUnicastA;
						}

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							sim.recordEvent(this, Event.RECEIVED_UNICAST_A_CLOSEST,
									targetUnicast.getSenderAgent().getId(), targetUnicast.getReceiverAgent().getId());
						}
					} else if (signalType == SignalType.SIGNAL_B) {
						if (this.isProxyAgent()) {
							targetUnicast = this.getHostAgent().receivedUnicastB;
						} else {
							targetUnicast = this.receivedUnicastB;
						}

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							sim.recordEvent(this, Event.RECEIVED_UNICAST_B_CLOSEST,
									targetUnicast.getSenderAgent().getId(), targetUnicast.getReceiverAgent().getId());
						}
					} else if (signalType == SignalType.SIGNAL_C) {
						if (this.isProxyAgent()) {
							targetUnicast = this.getHostAgent().receivedUnicastC;
						} else {
							targetUnicast = this.receivedUnicastC;
						}

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							sim.recordEvent(this, Event.RECEIVED_UNICAST_C_CLOSEST,
									targetUnicast.getSenderAgent().getId(), targetUnicast.getReceiverAgent().getId());
						}
					}

					if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
						_operationMoveAbsolute(targetUnicast.getSenderX(), targetUnicast.getSenderY());
					}
					// end of unconstrained

				}

			}
		}
	}

	public boolean operationDetectUnicastClosest(SignalType signalType) {
		boolean result = false;
		if (interactionMechanisms.contains(InteractionMechanism.UNICAST_CLOSEST_AGENT)) {
			if ((this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
					&& signalType != SignalType.SIGNAL_C)
					|| this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {

				Unicast targetUnicast = null;
				if (Main.settings.CONSTRAINED_INTERACTIONS) {

					// constrained
					Agent receivingAgent = this;
					Event event = null;

					if (this.isProxyAgent()) {
						receivingAgent = this.getHostAgent();
					}

					// determine if the appropriate unicast is being received
					// thats fit to the trait

					if (signalType == SignalType.SIGNAL_A
							&& receivingAgent.getSpecies().getTraits().contains(Trait.EXTRACTION)) {

						targetUnicast = this.receivedUnicastA;

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							event = Event.RECEIVED_UNICAST_A_CLOSEST;
						}
					}

					if (signalType == SignalType.SIGNAL_B
							&& this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS
							&& receivingAgent.getSpecies().getTraits().contains(Trait.TRANSPORTATION)) {

						targetUnicast = this.receivedUnicastB;

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							event = Event.RECEIVED_UNICAST_B_CLOSEST;
						}
					}

					if (signalType == SignalType.SIGNAL_C
							&& this.sim.problemComplexity == ProblemComplexity.FOUR_SEQUENTIAL_TASKS
							&& receivingAgent.getSpecies().getTraits().contains(Trait.TRANSPORTATION)) {

						targetUnicast = this.receivedUnicastC;

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							event = Event.RECEIVED_UNICAST_C_CLOSEST;
						}
					}

					if (event != null && targetUnicast != null && targetUnicast.getSenderAgent() != null) {

						sim.recordEvent(this, event, targetUnicast.getSenderAgent().getId(),
								targetUnicast.getReceiverAgent().getId());
						result = true;
						targetUnicast.markReceived();

						Agent detectorAgent = this;
						if (this.isProxyAgent()) {
							detectorAgent = this.getHostAgent();
						}
						if (signalType == SignalType.SIGNAL_A) {
							detectorAgent.detectedUnicastA = true;
						} else if (signalType == SignalType.SIGNAL_B) {
							detectorAgent.detectedUnicastB = true;
						} else if (signalType == SignalType.SIGNAL_C) {
							detectorAgent.detectedUnicastC = true;
						}

					}
					// end of constrained

				} else {

					// unconstrained version
					if (signalType == SignalType.SIGNAL_A) {
						if (this.isProxyAgent()) {
							targetUnicast = this.getHostAgent().receivedUnicastA;
						} else {
							targetUnicast = this.receivedUnicastA;
						}

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							sim.recordEvent(this, Event.RECEIVED_UNICAST_A_CLOSEST,
									targetUnicast.getSenderAgent().getId(), targetUnicast.getReceiverAgent().getId());
							result = true;
						}
					} else if (signalType == SignalType.SIGNAL_B) {
						if (this.isProxyAgent()) {
							targetUnicast = this.getHostAgent().receivedUnicastB;
						} else {
							targetUnicast = this.receivedUnicastB;
						}

						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							sim.recordEvent(this, Event.RECEIVED_UNICAST_B_CLOSEST,
									targetUnicast.getSenderAgent().getId(), targetUnicast.getReceiverAgent().getId());
							result = true;
						}
					} else if (signalType == SignalType.SIGNAL_C) {
						if (this.isProxyAgent()) {
							targetUnicast = this.getHostAgent().receivedUnicastC;
						} else {
							targetUnicast = this.receivedUnicastC;
						}
						if (targetUnicast != null && targetUnicast.getSenderAgent() != null) {
							sim.recordEvent(this, Event.RECEIVED_UNICAST_C_CLOSEST,
									targetUnicast.getSenderAgent().getId(), targetUnicast.getReceiverAgent().getId());
							result = true;
						}
					}
					// unconstrained version
				}

			}
		}

		return result;
	}

	public final void _operationFollowTrail(TrailGridWrapper trailGridWrapper) {
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
					int scanX = trailGridWrapper.strengthGrid.stx(this.x + deltaX);
					int scanY = trailGridWrapper.strengthGrid.sty(this.y + deltaY);

					double trailAmount = trailGridWrapper.getTrailStrengthAt(scanX, scanY);
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
			sim.recordEvent(this, Event.RECEIVED_TRAIL, NA, this.agentId);
			_operationMoveAbsolute(maxX, maxY);
			sim.trailGridWrapper.markUsed(maxX, maxY);
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

		_operationMoveToLocationAt(closestCollectionSite.x, closestCollectionSite.y);
		sim.recordEvent(this, Event.MOVE_TO_CLOSEST_COLLECTION_SITE, NA, NA);

	}

	/** This function is being "borrowed" from Int2D.java */
	public final double distance(final double x1, final double y1, final double x2, final double y2) {
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
	public final Agent findClosestAgent(int x, int y, int minimumDistance, Trait agentTrait) {
		// TODO: find out why some agents can be in the same location
		// (minimumDistance=0) -- this maybe valid

		Agent closestAgent = null;
		Bag agentBag = sim.agentGrid.getAllObjects();
		double closestDistance = Double.MAX_VALUE;

		for (int i = 0; i < agentBag.numObjs; i++) {

			Agent agent = (Agent) agentBag.get(i);

			if (agent.getSpecies().getTraits().contains(agentTrait)) {
				if (agent == this) {
					continue;
				}
				if (this.isProxyAgent() && agent == this.getHostAgent()) {
					continue;
				}

				if (agent.isProxyAgent() && agent.getHostAgent() == this) {
					continue;
				}

				double distance = distance(agent.x, agent.y, this.x, this.y);
				if (distance >= minimumDistance && distance < closestDistance) {
					closestAgent = agent;
					closestDistance = distance;
				}
			}

		}

		return closestAgent;
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
			if (this.isProxyAgent() && agent == this.getHostAgent()) {
				continue;
			}

			if (agent.isProxyAgent() && agent.getHostAgent() == this) {
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
			_operationFollowTrail(sim.trailGridWrapper);
			// sim.statistics.stepData.trailFollows++;
		}
	}

	public void operationLeaveTrail() {
		if (interactionMechanisms.contains(InteractionMechanism.TRAIL)) {
			_operationLeaveTrail(sim.trailGridWrapper);
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
				sim.recordEvent(this, Event.RECEIVED_TRAIL, NA, this.agentId);
				sim.trailGridWrapper.markUsed(x, y);
			}
			return this.locationHasTrail;
		} else {
			return false;
		}
	}

	public final void operationExtractResource() {
		if (Main.settings.CONSTRAINED_INTERACTIONS) {
			// constrained
			sim.recordEvent(this, Event.ATTEMPT_EXTRACT, NA, NA);
			if (species.getTraits().contains(Trait.EXTRACTION)) {

				boolean canExtract = false;

				if (this.isProxyAgent()) {
					canExtract = this.getHostAgent().detectedBroadcastA || this.getHostAgent().detectedUnicastA;
				} else {
					canExtract = this.detectedBroadcastA || this.detectedUnicastA;
				}

				// only extract if allowed
				if (canExtract && _operationPerformResourceAction(Task.EXTRACTION, this.sim.resourceGrid)) {
					// sim.statistics.stepData.resourceExtracts++;
					sim.recordEvent(this, Event.EXTRACTED_RESOURCE, NA, NA);
					_operationLeaveRewards(sim.detectorPeerReward, Event.DROPPED_DETECTOR_REWARDS);

					// now reset the detected signal...
					if (this.isProxyAgent()) {
						if (this.getHostAgent().detectedBroadcastA) {
							this.getHostAgent().detectedBroadcastA = false;
						}
						if (this.getHostAgent().detectedUnicastA) {
							this.getHostAgent().detectedUnicastA = false;
						}
					} else {
						if (this.detectedBroadcastA) {
							this.detectedBroadcastA = false;
						}
						if (this.detectedUnicastA) {
							this.detectedUnicastA = false;
						}
					}
				}

				updateLocationStatus(this.x, this.y);
			}

			// constrained

		} else {
			// unconstrained
			sim.recordEvent(this, Event.ATTEMPT_EXTRACT, NA, NA);
			if (species.getTraits().contains(Trait.EXTRACTION)) {

				if (_operationPerformResourceAction(Task.EXTRACTION, this.sim.resourceGrid)) {
					// sim.statistics.stepData.resourceExtracts++;
					sim.recordEvent(this, Event.EXTRACTED_RESOURCE, NA, NA);
					_operationLeaveRewards(sim.detectorPeerReward, Event.DROPPED_DETECTOR_REWARDS);

				}

				updateLocationStatus(this.x, this.y);
			}
		}
	}

	public void operationProcessResource() {
		if (Main.settings.CONSTRAINED_INTERACTIONS) {
			// constrained
			sim.recordEvent(this, Event.ATTEMPT_PROCESS, NA, NA);

			if (species.getTraits().contains(Trait.PROCESSING)) {

				boolean canProcess = false;

				if (this.isProxyAgent()) {
					canProcess = this.getHostAgent().detectedBroadcastB || this.getHostAgent().detectedUnicastB;
				} else {
					canProcess = this.detectedBroadcastB || this.detectedUnicastB;
				}

				if (canProcess && _operationPerformResourceAction(Task.PROCESSING, this.sim.resourceGrid)) {
					// sim.statistics.stepData.resourceProcesses++;
					sim.recordEvent(this, Event.PROCESSED_RESOURCE, NA, NA);
					_operationLeaveRewards(sim.extractorPeerReward, Event.DROPPED_EXTRACTOR_REWARDS);

					// now reset the detected signal...
					if (this.isProxyAgent()) {
						if (this.getHostAgent().detectedBroadcastB) {
							this.getHostAgent().detectedBroadcastB = false;
						}
						if (this.getHostAgent().detectedUnicastB) {
							this.getHostAgent().detectedUnicastB = false;
						}
					} else {
						if (this.detectedBroadcastB) {
							this.detectedBroadcastB = false;
						}
						if (this.detectedUnicastB) {
							this.detectedUnicastB = false;
						}
					}

				}

				updateLocationStatus(this.x, this.y);
			}
			// constrained

		} else {
			// unconstrained
			sim.recordEvent(this, Event.ATTEMPT_PROCESS, NA, NA);
			if (species.getTraits().contains(Trait.PROCESSING)) {

				if (_operationPerformResourceAction(Task.PROCESSING, this.sim.resourceGrid)) {
					// sim.statistics.stepData.resourceProcesses++;
					sim.recordEvent(this, Event.PROCESSED_RESOURCE, NA, NA);
					_operationLeaveRewards(sim.extractorPeerReward, Event.DROPPED_EXTRACTOR_REWARDS);
				}

				updateLocationStatus(this.x, this.y);
			}
			// unconstrained
		}

	}

	public final void operationLoadResource() {
		if (Main.settings.CONSTRAINED_INTERACTIONS) {
			// constrained...
			// 3 task: only if B was received
			// 4 task only if C was received
			sim.recordEvent(this, Event.ATTEMPT_TRANSPORT, NA, NA);
			if (species.getTraits().contains(Trait.TRANSPORTATION)) {
				updateLocationStatus(this.x, this.y);

				boolean canTransport = false;

				if (this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS) {
					if (this.isProxyAgent()) {
						canTransport = this.getHostAgent().detectedBroadcastB || this.getHostAgent().detectedUnicastB;
					} else {
						canTransport = this.detectedBroadcastB || this.detectedUnicastB;
					}
				} else {
					if (this.isProxyAgent()) {
						canTransport = this.getHostAgent().detectedBroadcastC || this.getHostAgent().detectedUnicastC;
					} else {
						canTransport = this.detectedBroadcastC || this.detectedUnicastC;
					}
				}

				if (canTransport) {
					if (locationHasExtractedResource || locationHasProcessedResource || locationHasRawResource) {
						if (!isCarryingResource) {
							isCarryingResource = true;

							stateOfCarriedResource = GridUtils.getResourceState(this.sim.resourceGrid, x, y);

							GridUtils.removeAllObjectsAt(this.sim.resourceGrid, x, y);
							// this.sim.resourceGrid.removeObjectsAtLocation(x,
							// y);

							// copy over the state of the resource
							sim.resourceStatusArray[x][y].cloneTo(statusOfCarriedResource);

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

		} else {

			// unconstrained...
			sim.recordEvent(this, Event.ATTEMPT_TRANSPORT, NA, NA);
			if (species.getTraits().contains(Trait.TRANSPORTATION)) {
				updateLocationStatus(this.x, this.y);
				if (locationHasExtractedResource || locationHasProcessedResource || locationHasRawResource) {
					if (!isCarryingResource) {
						isCarryingResource = true;

						stateOfCarriedResource = GridUtils.getResourceState(this.sim.resourceGrid, x, y);

						GridUtils.removeAllObjectsAt(this.sim.resourceGrid, x, y);

						// copy over the state of the resource
						sim.resourceStatusArray[x][y].cloneTo(statusOfCarriedResource);

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
			// // unconstrained...
		}
	}

	public final void operationUnLoadResource() {
		if (Main.settings.CONSTRAINED_INTERACTIONS) {
			operationUnLoadResource_Constrained();
		} else {
			operationUnLoadResource_Unconstrained();
		}
	}

	public final void operationUnLoadResource_Unconstrained() {
		sim.recordEvent(this, Event.ATTEMPT_TRANSPORT, NA, NA);
		if (species.getTraits().contains(Trait.TRANSPORTATION) && isCarryingResource) {
			updateLocationStatus(x, y);
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
							sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);
							sim.recordEvent(this, Event.COLLECTED_RESOURCE, NA, NA);
							GridUtils.set(sim.collectedResourceLocationGrid, statusOfCarriedResource.originX,
									statusOfCarriedResource.originY, true);
							_operationLeaveRewards(sim.extractorPeerReward, Event.DROPPED_EXTRACTOR_REWARDS);

							// save over this status
							ResourceStatus collectedResourceStatus = new ResourceStatus();
							statusOfCarriedResource.cloneTo(collectedResourceStatus);
							collectedResourceStatus.captureStep = sim.simStepCounter;
							sim.touchedResources.remove(statusOfCarriedResource);
							statusOfCarriedResource.clear();
							sim.touchedResources.add(collectedResourceStatus);

							// logger.info("CAPTURE!! 3 complex");
						}

					} else if (this.sim.problemComplexity.equals(ProblemComplexity.FOUR_SEQUENTIAL_TASKS)) {
						if (stateOfCarriedResource == ResourceState.PROCESSED) {
							dropResource = false;
							this.sim.numberOfCollectedResources++;
							sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);
							sim.recordEvent(this, Event.COLLECTED_RESOURCE, NA, NA);

							GridUtils.set(sim.collectedResourceLocationGrid, statusOfCarriedResource.originX,
									statusOfCarriedResource.originY, true);
							_operationLeaveRewards(sim.processorPeerReward, Event.DROPPED_PROCESSOR_REWARDS);

							// save over this status
							ResourceStatus collectedResourceStatus = new ResourceStatus();
							statusOfCarriedResource.cloneTo(collectedResourceStatus);
							collectedResourceStatus.captureStep = sim.simStepCounter;
							sim.touchedResources.remove(statusOfCarriedResource);
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

					GridUtils.set(this.sim.resourceGrid, x, y, stateOfCarriedResource);

					// this is a resource drop...place the resource back in the
					// grid
					statusOfCarriedResource.cloneTo(this.sim.resourceStatusArray[x][y]);
					this.sim.resourceStatusArray[x][y].currentX = x;
					this.sim.resourceStatusArray[x][y].currentY = y;
					this.sim.resourceStatusArray[x][y].numTimesUnloaded++;

					// need to keep track of this resource...
					sim.touchedResources.remove(statusOfCarriedResource);
					sim.touchedResources.add(this.sim.resourceStatusArray[x][y]);

					statusOfCarriedResource.clear();

					sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);

				}

			}

			updateLocationStatus(this.x, this.y);

		}
	}

	public final void operationUnLoadResource_Constrained() {
		sim.recordEvent(this, Event.ATTEMPT_TRANSPORT, NA, NA);
		if (species.getTraits().contains(Trait.TRANSPORTATION) && isCarryingResource) {
			updateLocationStatus(x, y);

			boolean canTransport = false;
			if (this.sim.problemComplexity == ProblemComplexity.THREE_SEQUENTIAL_TASKS) {
				if (this.isProxyAgent()) {
					canTransport = this.getHostAgent().detectedBroadcastB || this.getHostAgent().detectedUnicastB;
				} else {
					canTransport = this.detectedBroadcastB || this.detectedUnicastB;
				}
			} else {
				if (this.isProxyAgent()) {
					canTransport = this.getHostAgent().detectedBroadcastC || this.getHostAgent().detectedUnicastC;
				} else {
					canTransport = this.detectedBroadcastC || this.detectedUnicastC;
				}
			}

			if (canTransport) {

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
								sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);
								sim.recordEvent(this, Event.COLLECTED_RESOURCE, NA, NA);
								GridUtils.set(sim.collectedResourceLocationGrid, statusOfCarriedResource.originX,
										statusOfCarriedResource.originY, true);
								_operationLeaveRewards(sim.extractorPeerReward, Event.DROPPED_EXTRACTOR_REWARDS);

								// resource has been collected, now we need to
								// reset the receive signal flag

								// now reset the detected signal...
								if (this.isProxyAgent()) {
									if (this.getHostAgent().detectedBroadcastB) {
										this.getHostAgent().detectedBroadcastB = false;
									}
									if (this.getHostAgent().detectedUnicastB) {
										this.getHostAgent().detectedUnicastB = false;
									}
								} else {
									if (this.detectedBroadcastB) {
										this.detectedBroadcastB = false;
									}
									if (this.detectedUnicastB) {
										this.detectedUnicastB = false;
									}
								}

								// save over this status
								ResourceStatus collectedResourceStatus = new ResourceStatus();
								statusOfCarriedResource.cloneTo(collectedResourceStatus);
								collectedResourceStatus.captureStep = sim.simStepCounter;
								sim.touchedResources.remove(statusOfCarriedResource);
								statusOfCarriedResource.clear();
								sim.touchedResources.add(collectedResourceStatus);

								// logger.info("CAPTURE!! 3 complex");
							}

						} else if (this.sim.problemComplexity.equals(ProblemComplexity.FOUR_SEQUENTIAL_TASKS)) {
							if (stateOfCarriedResource == ResourceState.PROCESSED) {
								dropResource = false;
								this.sim.numberOfCollectedResources++;
								sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);
								sim.recordEvent(this, Event.COLLECTED_RESOURCE, NA, NA);
								GridUtils.set(sim.collectedResourceLocationGrid, statusOfCarriedResource.originX,
										statusOfCarriedResource.originY, true);
								_operationLeaveRewards(sim.processorPeerReward, Event.DROPPED_PROCESSOR_REWARDS);

								// now reset the detected signal...
								if (this.isProxyAgent()) {
									if (this.getHostAgent().detectedBroadcastC) {
										this.getHostAgent().detectedBroadcastC = false;
									}
									if (this.getHostAgent().detectedUnicastC) {
										this.getHostAgent().detectedUnicastC = false;
									}
								} else {
									if (this.detectedBroadcastC) {
										this.detectedBroadcastC = false;
									}
									if (this.detectedUnicastC) {
										this.detectedUnicastC = false;
									}
								}

								// save over this status
								ResourceStatus collectedResourceStatus = new ResourceStatus();
								statusOfCarriedResource.cloneTo(collectedResourceStatus);
								collectedResourceStatus.captureStep = sim.simStepCounter;
								sim.touchedResources.remove(statusOfCarriedResource);
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
						GridUtils.set(this.sim.resourceGrid, x, y, stateOfCarriedResource);

						// this is a resource drop...place the resource back in
						// the
						// grid
						statusOfCarriedResource.cloneTo(this.sim.resourceStatusArray[x][y]);
						this.sim.resourceStatusArray[x][y].currentX = x;
						this.sim.resourceStatusArray[x][y].currentY = y;
						this.sim.resourceStatusArray[x][y].numTimesUnloaded++;

						// need to keep track of this resource...
						sim.touchedResources.remove(statusOfCarriedResource);
						sim.touchedResources.add(this.sim.resourceStatusArray[x][y]);

						statusOfCarriedResource.clear();

						sim.recordEvent(this, Event.UNLOADED_RESOURCE, NA, NA);

					}

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

		if (GridUtils.getResourceState(sim.resourceGrid, x, y) == ResourceState.RAW) {
			this.locationHasRawResource = true;
		} else if (GridUtils.getResourceState(sim.resourceGrid, x, y) == ResourceState.EXTRACTED) {
			this.locationHasExtractedResource = true;
		} else if (GridUtils.getResourceState(sim.resourceGrid, x, y) == ResourceState.PROCESSED) {
			this.locationHasProcessedResource = true;
		}

		if (sim.extractorPeerReward.val >= 1.0) {
			this.locationHasExtractorReward = true;
			if (Main.settings.PEER_REWARDS) {
				sim.recordEvent(this, Event.RECEIVED_EXTRACTOR_REWARDS, NA, this.agentId);
			}
		}

		if (sim.detectorPeerReward.val >= 1.0) {
			this.locationHasDetectorReward = true;
			if (Main.settings.PEER_REWARDS) {
				sim.recordEvent(this, Event.RECEIVED_DETECTOR_REWARDS, NA, this.agentId);
			}
		}

		if (sim.processorPeerReward.val >= 1.0) {
			this.locationHasProcessorReward = true;
			if (Main.settings.PEER_REWARDS) {
				sim.recordEvent(this, Event.RECEIVED_PROCESSOR_REWARDS, NA, this.agentId);
			}
		}

		if (sim.trailGridWrapper.getTrailStrengthAt(x, y) >= 1) {
			this.locationHasTrail = true;

		} else {
			this.locationHasTrail = false;
		}

		if (GridUtils.gridHasAnObjectAt(sim.collectionSiteGrid, x, y)) {
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
		String result = Integer.toString(this.getAgentId());
		if (this.program != null) {
			result += ":" + this.program.getSignature();

		}
		return result;

	}

	public String getFingerPrint() {
		String result = Integer.toString(this.getAgentId());
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

	public void setInteractionMechanisms(EnumSet<InteractionMechanism> interactionMechanisms) {
		this.interactionMechanisms = interactionMechanisms;
		interactionMode = InteractionMode.NONE;
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

	public boolean isProxyAgent() {
		return isProxyAgent;
	}

	public void setProxyAgent(boolean hosted) {
		this.isProxyAgent = hosted;
	}

	public Agent getHostAgent() {
		return hostAgent;
	}

	public void setHostAgent(Agent hostAgent) {
		this.hostAgent = hostAgent;
	}

	/**
	 * Set interaction mode based on event type
	 * 
	 * @param event
	 */
	public void setInteractionMode(Event event) {
		switch (event) {
			case SENT_TRAIL :
				interactionMode = InteractionMode.SENDING_TRAIL;
				break;
			case RECEIVED_TRAIL :
				interactionMode = InteractionMode.RECEIVING_TRAIL;
				break;
			case SENT_BROADCAST_A :
			case SENT_BROADCAST_B :
			case SENT_BROADCAST_C :
				interactionMode = InteractionMode.SENDING_BROADCAST;
				break;
			case RECEIVED_BROADCAST_A :
			case RECEIVED_BROADCAST_B :
			case RECEIVED_BROADCAST_C :
				interactionMode = InteractionMode.RECEIVING_BROADCAST;
				break;
			case SENT_UNICAST_A_CLOSEST :
			case SENT_UNICAST_B_CLOSEST :
			case SENT_UNICAST_C_CLOSEST :
				interactionMode = InteractionMode.SENDING_UNICAST;
				break;
			case RECEIVED_UNICAST_A_CLOSEST :
			case RECEIVED_UNICAST_B_CLOSEST :
			case RECEIVED_UNICAST_C_CLOSEST :
				interactionMode = InteractionMode.RECEIVING_UNICAST;
				break;

			default :
		}

	}

}
