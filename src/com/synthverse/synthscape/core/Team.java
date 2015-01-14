package com.synthverse.synthscape.core;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private int teamId;

	List<Agent> members;
	boolean allMembersEvaluated = false;
	int expectedSize = 0;

	public Team() {
		init();
	}

	public void init() {
		teamId = -1;

		if (members == null) {
			members = new ArrayList<Agent>();
		} else {
			members.clear();
		}
		allMembersEvaluated = false;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public List<Agent> getMembers() {
		return members;
	}

	public void clearMembers() {
		if (members != null) {
			members.clear();
		}
		allMembersEvaluated = false;
	}

	public void addMember(Agent agent) {
		members.add(agent);
	}

	public double getMaxFitness() {
		double maxFitness = 0;
		for (Agent member : members) {
			double memberFitness = member.getFitness();
			if (memberFitness > maxFitness) {
				maxFitness = memberFitness;
			}
		}
		return maxFitness;
	}

	public void setAllMembersEvaluated(boolean value) {
		allMembersEvaluated = value;
	}

	public boolean isAllMembersEvaluated() {
		return allMembersEvaluated;
	}

	public int getExpectedSize() {
		return expectedSize;
	}

	public void setExpectedSize(int expectedSize) {
		this.expectedSize = expectedSize;
	}

}
