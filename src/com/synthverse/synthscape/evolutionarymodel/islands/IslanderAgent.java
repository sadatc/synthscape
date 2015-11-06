package com.synthverse.synthscape.evolutionarymodel.islands;

import sim.engine.SimState;

import com.synthverse.Main;
import com.synthverse.stacks.GenotypeInstruction;
import com.synthverse.stacks.Instruction;
import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Settings;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

@SuppressWarnings("serial")
public class IslanderAgent extends Agent {
	public static long _optimizationIslanderAgentCounter = 0;

	public IslanderAgent(Simulation simulation, Species species) {
		super(simulation, species);
		_optimizationIslanderAgentCounter++;

	}

	public IslanderAgent(Simulation sim, Species species, int generationNumber,
			int maxSteps, int startX, int startY) {
		super(sim, species, generationNumber, maxSteps, startX, startY);
		_optimizationIslanderAgentCounter++;
	}

	public void stepAction(SimState state) {
		this.getVirtualMachine().step();

	}

	@Override
	public double doubleValue() {
		double result = 0.0; // normal agent
		if (this.isCarryingResource) {
			result = 1.0;
		}
		return result;

	}

	private void seedGenotypeWithNOOPs(Program program) {
		boolean spaceLeft = true;

		do {
			spaceLeft = program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.NOOP));

		} while (spaceLeft);
	}

	private void seedGenotypeWithActions(Program program) {

		boolean spaceLeft = true;

		do {
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_DETECT_HOME));

			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_DETECT_RAW_RESOURCE));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_DETECT_TRAIL));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_FOLLOW_TRAIL));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_IS_CARRYING_EXTRACTED_RESOURCE));

			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_IS_CARRYING_RAW_RESOURCE));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_IS_CARRYING_RESOURCE));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_E));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_W));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_NE));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_NW));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_SE));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_SW));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_MOVE_TO_CLOSEST_HOME));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_RESOURCE_EXTRACT));
			program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_RESOURCE_LOAD));

			spaceLeft = program.addInstructionSafely(GenotypeInstruction
					.fromInstruction(Instruction.ACTION_RESOURCE_UNLOAD));
		} while (spaceLeft);

	}

	@Override
	protected void initGenotype() {
		super.initGenotype();

		if (Main.settings.SEED_GENOTYPE_PRESET_INSTRUCTIONS == Settings.SeedType.NOOPS) {
			this.program = Program.Factory.createEmpty(sim.random);
			seedGenotypeWithNOOPs(this.program);
		} else if (Main.settings.SEED_GENOTYPE_PRESET_INSTRUCTIONS == Settings.SeedType.RANDOM) {
			this.program = Program.Factory.createRandom(sim.random);
		} else if (Main.settings.SEED_GENOTYPE_PRESET_INSTRUCTIONS == Settings.SeedType.ACTIONS) {
			this.program = Program.Factory.createEmpty(sim.random);
			seedGenotypeWithActions(this.program);
		}

		VirtualMachine vm = VirtualMachine.Factory.createDefault(sim, this,
				sim.random);
		vm.overwriteGenotypeWithProgram(this.program);
		vm.setCpuCycles(sim.getMaxStepsPerAgent());
		this.setVirtualMachine(vm);

	}

	public static long get_optimizationIslanderAgentCounter() {
		return _optimizationIslanderAgentCounter;
	}

}
