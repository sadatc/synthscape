package com.synthverse.synthscape.core;

import java.util.LinkedHashSet;
import java.util.Set;

import com.synthverse.Main;

public enum Species {

    DETECTOR(0, "detector", new TraitsDefiner() {

	public LinkedHashSet<Trait> define() {
	    LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
	    traits.add(Trait.DETECTION);

	    return traits;
	}

    }),

    EXTRACTOR(1, "extractor", new TraitsDefiner() {

	public LinkedHashSet<Trait> define() {
	    LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
	    traits.add(Trait.EXTRACTION);

	    return traits;
	}

    }), PROCESSOR(2, "processor", new TraitsDefiner() {

	public LinkedHashSet<Trait> define() {
	    LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
	    traits.add(Trait.PROCESSING);

	    return traits;
	}

    }), TRANSPORTER(3, "transporter", new TraitsDefiner() {

	public LinkedHashSet<Trait> define() {
	    LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

	    traits.add(Trait.TRANSPORTATION);

	    return traits;
	}

    }), HETERO(4, "hetero", new TraitsDefiner() {

	@Override
	public LinkedHashSet<Trait> define() {
	    LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

	    traits.add(Trait.DETECTION);
	    traits.add(Trait.EXTRACTION);
	    traits.add(Trait.TRANSPORTATION);
	    traits.add(Trait.MULTICAPABLE);

	    if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
		traits.add(Trait.PROCESSING);
	    }

	    return traits;
	}

    });

    private String name;
    private int id;

    private LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
    private TraitsDefiner traitsDefiner;

    Species(int id, String name, TraitsDefiner traitsDefiner) {
	this.id = id;
	this.name = name;
	this.traitsDefiner = traitsDefiner;
	this.traits.addAll(traitsDefiner.define());
    }

    public Set<Trait> getTraits() {
	return traits;
    }

    public String getAbbreviatedTraits() {
	String result = null;
	StringBuilder buffer = new StringBuilder();

	for (Trait trait : traits) {
	    buffer.append(trait.toString().charAt(0));
	}

	result = buffer.toString();

	return result;
    }

    public String getName() {
	return name;
    }

    public int getId() {
	return id;
    }

}
