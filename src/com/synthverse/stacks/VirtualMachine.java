/**
 * @(#)VirtualMachine.java  3:55:28 PM Feb 3, 2009
 * 
 * Copyright (c) 2004-2009 Sadat Chowdhury (sadatc@gmail.com)
 * 
 * The author reserves all rights to this software. Please contact the author 
 * (sadatc@gmail.com) to obtain permission to use, modify, or redistribute this 
 * software in any form. 
 * 
 * Unless otherwise stated, the author issues no guarantees of success and 
 * offers no warranties against damages of any kind. 
 * 
 * Last Revision : $Rev:: 6                     $: 
 * Last Updated  : $Date:: 2012-10-14 22:50:12 #$:
 * 
 */

package com.synthverse.stacks;

import java.util.logging.Logger;

import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.util.LogUtils;

import ec.util.MersenneTwisterFast;

/**
 * A virtual machine is composed of a set of stacks (with their own state), a
 * program, agent and sim
 * 
 * @author sadat
 * 
 */
public final class VirtualMachine {

	private static Logger logger = Logger.getLogger(VirtualMachine.class
			.getName());

	private MersenneTwisterFast randomNumberGenerator = null;
	private BooleanStack booleanStack = null;
	private IntegerStack integerStack = null;
	private FloatStack floatStack = null;
	private CodeStack codeStack = null;
	private Genotype genotype = null;
	private Agent agent = null;

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Simulation getSim() {
		return sim;
	}

	public void setSim(Simulation sim) {
		this.sim = sim;
	}

	private Simulation sim = null;

	private int IP; // instruction pointer
	private int cpuCycles;

	static {
		LogUtils.applyDefaultSettings(logger);
	}

	private VirtualMachine() {
	}

	public final void resetIP() {
		IP = 0;
	}

	public final void resetStacks() {
		booleanStack.flush();
		integerStack.flush();
		floatStack.flush();
		codeStack.flush();
	}

	public final void resetAll() {
		IP = 0;
		booleanStack.flush();
		integerStack.flush();
		floatStack.flush();
		codeStack.flush();
	}

	public final int getIP() {
		return IP;
	}

	public final int getMaxIP() {
		return genotype.getSize();
	}

	public final void setIP(int iP) {
		IP = iP;
	}

	public final void setExitState() {
		IP = genotype.getSize() + 1;
	}

	public final boolean setValidatedIP(int iP) {
		if (genotype.isValidIndex(iP)) {
			IP = iP;
			return true;
		} else {
			return false;
		}
	}

	public final void incrementIP() {
		IP++;
	}

	public final void decrementCpuCycles() {
		cpuCycles--;
	}

	public final int getCpuCycles() {
		return cpuCycles;
	}

	public final void setCpuCycles(int cpuCycles) {
		this.cpuCycles = cpuCycles;
	}

	public final IntegerStack getIntegerStack() {
		return integerStack;
	}

	public final void setBooleanStack(BooleanStack booleanStack) {
		this.booleanStack = booleanStack;
	}

	public final void setIntegerStack(IntegerStack integerStack) {
		this.integerStack = integerStack;
	}

	public final void setFloatStack(FloatStack floatStack) {
		this.floatStack = floatStack;
	}

	public final BooleanStack getBooleanStack() {
		return booleanStack;
	}

	public final FloatStack getFloatStack() {
		return floatStack;
	}

	public final Genotype getInstructionArray() {
		return genotype;
	}

	public final void setInstructionArray(Genotype genotype) {
		this.genotype = genotype;
	}

	public final CodeStack getCodeStack() {
		return codeStack;
	}

	public final void setCodeStack(CodeStack codeStack) {
		this.codeStack = codeStack;
	}

	public final void overwriteGenotypeWithProgram(Program program) {
		this.genotype.overwriteWithProgram(program);
	}

	public final boolean step() {
		boolean result = false;
		if (cpuCycles > 0 && genotype.isValidIndex(IP)) {
			GenotypeInstruction instruction = genotype.getValue(IP);
			instruction.execute(this);
			decrementCpuCycles();
			result = true;
		} else if (cpuCycles > 0
				&& Config.RECYCLE_EXECUTION_FOR_EXCESSIVE_CPU_CYCLES) {
			this.resetIP();
			GenotypeInstruction instruction = genotype.getValue(IP);
			instruction.execute(this);
			decrementCpuCycles();
			result = true;
		}

		/*
		 * if(result) { logger.info("executed: "+this.toString()); }
		 */

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VirtualMachine [IP=").append(IP).append(", cpuCycles=")
				.append(cpuCycles).append(", booleanStack=")
				.append(booleanStack).append(", integerStack=")
				.append(integerStack).append(", floatStack=")
				.append(floatStack).append(", codeStack=").append(codeStack)
				.append(", genotype=").append(genotype).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + IP;
		result = prime * result
				+ ((booleanStack == null) ? 0 : booleanStack.hashCode());
		result = prime * result
				+ ((codeStack == null) ? 0 : codeStack.hashCode());
		result = prime * result + cpuCycles;
		result = prime * result
				+ ((floatStack == null) ? 0 : floatStack.hashCode());
		result = prime * result
				+ ((genotype == null) ? 0 : genotype.hashCode());
		result = prime * result
				+ ((integerStack == null) ? 0 : integerStack.hashCode());
		result = prime
				* result
				+ ((randomNumberGenerator == null) ? 0 : randomNumberGenerator
						.hashCode());

		result = prime * result + ((agent == null) ? 0 : agent.hashCode());

		result = prime * result + ((sim == null) ? 0 : sim.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VirtualMachine)) {
			return false;
		}
		VirtualMachine other = (VirtualMachine) obj;
		if (IP != other.IP) {
			return false;
		}
		if (booleanStack == null) {
			if (other.booleanStack != null) {
				return false;
			}
		} else if (!booleanStack.equals(other.booleanStack)) {
			return false;
		}
		if (codeStack == null) {
			if (other.codeStack != null) {
				return false;
			}
		} else if (!codeStack.equals(other.codeStack)) {
			return false;
		}
		if (cpuCycles != other.cpuCycles) {
			return false;
		}
		if (floatStack == null) {
			if (other.floatStack != null) {
				return false;
			}
		} else if (!floatStack.equals(other.floatStack)) {
			return false;
		}
		if (genotype == null) {
			if (other.genotype != null) {
				return false;
			}
		} else if (!genotype.equals(other.genotype)) {
			return false;
		}
		if (integerStack == null) {
			if (other.integerStack != null) {
				return false;
			}
		} else if (!integerStack.equals(other.integerStack)) {
			return false;
		}

		if (sim == null) {
			if (other.sim != null) {
				return false;
			}
		} else if (!sim.equals(other.sim)) {
			return false;
		}

		if (agent == null) {
			if (other.agent != null) {
				return false;
			}
		} else if (!agent.equals(other.agent)) {
			return false;
		}

		if (randomNumberGenerator == null) {
			if (other.randomNumberGenerator != null) {
				return false;
			}
		} else if (!randomNumberGenerator.equals(other.randomNumberGenerator)) {
			return false;
		}

		return true;
	}

	public MersenneTwisterFast getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

	private void setRandomNumberGenerator(
			MersenneTwisterFast randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public static class Factory {

		public static final VirtualMachine createDefault(Simulation sim,
				Agent agent, MersenneTwisterFast randomNumberGenerator) {
			VirtualMachine vm = new VirtualMachine();
			vm.setAgent(agent);
			vm.setSim(sim);
			vm.setRandomNumberGenerator(randomNumberGenerator);
			vm.setBooleanStack(new BooleanStack(randomNumberGenerator));
			vm.setIntegerStack(new IntegerStack(randomNumberGenerator));
			vm.setFloatStack(new FloatStack(randomNumberGenerator));
			vm.setCodeStack(new CodeStack(randomNumberGenerator));
			vm.setInstructionArray(new Genotype());
			vm.setCpuCycles(Config.DEFAULT_ENTITY_CPU_CYCLES);
			vm.resetIP();
			return vm;
		}

	}

}
