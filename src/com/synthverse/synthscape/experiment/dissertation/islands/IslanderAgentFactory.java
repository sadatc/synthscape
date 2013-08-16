package com.synthverse.synthscape.experiment.dissertation.islands;

import com.synthverse.stacks.Program;
import com.synthverse.stacks.VirtualMachine;
import com.synthverse.synthscape.core.Agent;
import com.synthverse.synthscape.core.AgentFactory;
import com.synthverse.synthscape.core.Constants;
import com.synthverse.synthscape.core.D;
import com.synthverse.synthscape.core.Simulation;
import com.synthverse.synthscape.core.Species;

public class IslanderAgentFactory extends AgentFactory implements Constants {

    protected IslanderAgentFactory(Simulation simulation) {
	super(simulation);
    }

    @Override
    public Agent getNewFactoryAgent(Species species) {
	IslanderAgent agent = new IslanderAgent(simulation, species);
	agent.setMaxSteps(simulation.getMaxStepsPerAgent());
	
	
	Program p = Program.Factory.createRandom(simulation.random);
	VirtualMachine vm = VirtualMachine.Factory.createDefault(simulation,
		agent, simulation.random);
	vm.loadProgram(p);
	vm.setCpuCycles(simulation.getMaxStepsPerAgent());
	
	D.p("maxSteps="+agent.getMaxSteps());
	agent.setVirtualMachine(vm);

	return agent;
    }

    @Override
    public Agent createNewFactoryAgent(Species species) {
	// TODO Auto-generated method stub
	return null;
    }

}
