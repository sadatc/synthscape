package com.synthverse.synthscape.core;

import java.util.ArrayList;
import java.util.List;

/*
 * TODO: Statistics capture will be in 3 phases: step, trial, experiment
 */

public class Statistics extends StatisticsManager implements Constants {

	/**
	 * Measure pertaining to a single step
	 */
	public Measure stepData = new Measure();

	/**
	 * Measure pertaining to a complete sim cycle (all aggregate steps, so far)
	 */
	public Measure simData = new Measure();
	/**
	 * Measure pertaining to a complete experiment (all sims run so far)
	 */
	public Measure experimentData = new Measure();

	List<Measure> simDataList = new ArrayList<Measure>();

	public final void aggregateStepData() {

		simData.collectionSiteHits += stepData.collectionSiteHits;
		simData.numberOfCellsDiscovered += stepData.numberOfCellsDiscovered;

		simData.resourceHits += stepData.resourceHits;

		simData.extractedResourceHits += stepData.extractedResourceHits;

		simData.trailHits += stepData.trailHits;

		simData.trailDrops += stepData.trailDrops;

		simData.primaryCollectionSiteHits += stepData.primaryCollectionSiteHits;
		simData.resourceLoads += stepData.resourceLoads;

		simData.resourceUnloads += stepData.resourceUnloads;

		simData.resourceExtracts += stepData.resourceExtracts;

		simData.resourceCaptures += stepData.resourceCaptures;

		simData.broadcasts += stepData.broadcasts;

		simData.broadcastMoves += stepData.broadcastMoves;

		simData.trailFollows += stepData.trailFollows;
		simData.trailRandomFollows += stepData.trailRandomFollows;

	}

	public void resetSimStats() {
		stepData.zeroAll();
		simData.zeroAll();
		this._resetSampleSummaryStatisticsCounters();

	}

	public void takeStepSnapshot() {
		aggregateStepData();
		collectStepStats();
		stepData.zeroAll();
	}

	private final void collectStepStats() {

		this._addSampleSummaryValue("extracts#", stepData.resourceExtracts);
		this._addSampleSummaryValue("captures#", stepData.resourceCaptures);

	}

	private void collectSimStats() {
		this._addUniverseSummaryValue("extracts#",
				this._getSampleSummaryStatistics("extracts#"));
	}

	public void takeSimSnapshot() {
		collectSimStats();
		printSimulationSummary();
		resetSimStats();
	}

	public void printSimulationSummary() {
		D.p("Printing out statistical data...");
		this._printSampleSummaryStatistics();
	}

	public void printExperimentSummary() {
		D.p("--- EXPERIMENT SUMMARY ----");
		this._printUniverseSummaryStatistics();

	}
}
