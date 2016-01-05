package com.synthverse.synthscape.core;

import java.util.LinkedHashSet;
import java.util.Set;

import com.synthverse.Main;

public enum Species {

	D(0, "d", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.DETECTION);

			return traits;
		}

	}),

	DETECTOR(1, "detector", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.DETECTION);

			return traits;
		}

	}),

	EXTRACTOR(2, "extractor", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.EXTRACTION);

			return traits;
		}

	}),

	E(3, "e", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.EXTRACTION);

			return traits;
		}

	}),

	PROCESSOR(4, "processor", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.PROCESSING);

			return traits;
		}

	}),

	P(5, "p", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.PROCESSING);

			return traits;
		}

	}),

	TRANSPORTER(6, "transporter", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),

	T(7, "t", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),

	HOMOGENOUS(8, "homogenous", new TraitsDefiner() {

		@Override
		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.DETECTION);
			traits.add(Trait.EXTRACTION);
			traits.add(Trait.TRANSPORTATION);
			traits.add(Trait.HOMOGENOUS);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}

			return traits;
		}

	}),

	DET(9, "det", new TraitsDefiner() {

		@Override
		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.DETECTION);
			traits.add(Trait.EXTRACTION);
			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),

	DEPT(10, "dept", new TraitsDefiner() {

		@Override
		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.DETECTION);
			traits.add(Trait.EXTRACTION);
			traits.add(Trait.TRANSPORTATION);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}

			return traits;
		}

	}),

	DE(11, "de", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.DETECTION);
			traits.add(Trait.EXTRACTION);

			return traits;
		}

	}),

	DP(12, "dp", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.DETECTION);
			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}

			return traits;
		}

	}),

	DT(13, "dt", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.DETECTION);
			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),

	ET(14, "et", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.EXTRACTION);
			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),

	EP(15, "ep", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.EXTRACTION);
			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}

			return traits;
		}

	}),

	PT(16, "pt", new TraitsDefiner() {

		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
			traits.add(Trait.TRANSPORTATION);
			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}

			return traits;
		}

	}),

	DEP(17, "dep", new TraitsDefiner() {

		@Override
		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.DETECTION);
			traits.add(Trait.EXTRACTION);
			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}

			return traits;
		}

	}),

	DPT(18, "dpt", new TraitsDefiner() {

		@Override
		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.DETECTION);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}
			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),
	
	EPT(19, "ept", new TraitsDefiner() {

		@Override
		public LinkedHashSet<Trait> define() {
			LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();

			traits.add(Trait.EXTRACTION);

			if (Main.settings.PROBLEM_COMPLEXITY == ProblemComplexity.FOUR_SEQUENTIAL_TASKS) {
				traits.add(Trait.PROCESSING);
			}
			traits.add(Trait.TRANSPORTATION);

			return traits;
		}

	}),

	;

	private String name;
	private int id;

	private LinkedHashSet<Trait> traits = new LinkedHashSet<Trait>();
	@SuppressWarnings("unused")
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
