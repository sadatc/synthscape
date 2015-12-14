package com.synthverse.synthscape.core;

public enum ProblemComplexity {
	THREE_SEQUENTIAL_TASKS(0, "3 task", "3"), FOUR_SEQUENTIAL_TASKS(1, "4 task", "4");
	private int id;
	private String name;
	private String shortName;

	ProblemComplexity(int id, String name, String shortName) {
		this.id = id;
		this.name = name;
		this.shortName = shortName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

}
