package com.synthverse.synthscape.core;

public enum ProblemComplexity {
	THREE_SEQUENTIAL_TASKS(0, "3 task"), FOUR_SEQUENTIAL_TASKS(1, "4 task");
	private int id;
	private String name;

	ProblemComplexity(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
