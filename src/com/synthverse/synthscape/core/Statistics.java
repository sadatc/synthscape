package com.synthverse.synthscape.core;

import java.util.ArrayList;
import java.util.List;

import sim.field.grid.IntGrid2D;

/*
 * TODO: Statistics capture will be in 3 phases: step, trial, experiment
 */

public class Statistics extends StatisticsManager implements Constants {

	/**
	 * Data pertaining to a single step
	 */
	public Data stepData = new Data();

	/**
	 * Data pertaining to a complete sim cycle (all aggregate steps, so far)
	 */
	public Data simData = new Data();
	/**
	 * Data pertaining to a complete experiment (all sims run so far)
	 */
	public Data experimentData = new Data();
	

	List<Data> simDataList = new ArrayList<Data>();

	public final void aggregateStepData() {

		simData.homeHits += stepData.homeHits;
		simData.numberOfCellsDiscovered += stepData.numberOfCellsDiscovered;

		simData.resourceHits += stepData.resourceHits;
		simData.resourceAHits += stepData.resourceAHits;
		simData.resourceBHits += stepData.resourceBHits;
		simData.resourceCHits += stepData.resourceCHits;

		simData.extractHits += stepData.extractHits;
		simData.extractAHits += stepData.extractAHits;
		simData.extractBHits += stepData.extractBHits;
		simData.extractCHits += stepData.extractCHits;

		simData.trailHits += stepData.trailHits;
		simData.trailAHits += stepData.trailAHits;
		simData.trailBHits += stepData.trailBHits;
		simData.trailCHits += stepData.trailCHits;

		simData.trailDrops += stepData.trailDrops;
		simData.trailADrops += stepData.trailADrops;
		simData.trailBDrops += stepData.trailBDrops;
		simData.trailCDrops += stepData.trailCDrops;

		simData.primaryHomeHits += stepData.primaryHomeHits;
		simData.resourceLoads += stepData.resourceLoads;
		simData.resourceALoads += stepData.resourceALoads;
		simData.resourceBLoads += stepData.resourceBLoads;
		simData.resourceCLoads += stepData.resourceCLoads;

		simData.resourceUnloads += stepData.resourceUnloads;
		simData.resourceAUnloads += stepData.resourceAUnloads;
		simData.resourceBUnloads += stepData.resourceBUnloads;
		simData.resourceCUnloads += stepData.resourceCUnloads;

		simData.resourceExtracts += stepData.resourceExtracts;
		simData.resourceAExtracts += stepData.resourceAExtracts;
		simData.resourceBExtracts += stepData.resourceBExtracts;
		simData.resourceCExtracts += stepData.resourceCExtracts;

		simData.resourceCaptures += stepData.resourceCaptures;
		simData.resourceACaptures += stepData.resourceACaptures;
		simData.resourceBCaptures += stepData.resourceBCaptures;
		simData.resourceCCaptures += stepData.resourceCCaptures;

		simData.broadcasts += stepData.broadcasts;
		simData.aBroadcasts += stepData.aBroadcasts;
		simData.bBroadcasts += stepData.bBroadcasts;
		simData.cBroadcasts += stepData.cBroadcasts;

		simData.broadcastMoves += stepData.broadcastMoves;
		simData.aBroadcastMoves += stepData.aBroadcastMoves;
		simData.bBroadcastMoves += stepData.bBroadcastMoves;
		simData.cBroadcastMoves += stepData.cBroadcastMoves;

		simData.trailFollows += stepData.trailFollows;
		simData.trailAFollows += stepData.trailAFollows;
		simData.trailBFollows += stepData.trailBFollows;
		simData.trailCFollows += stepData.trailCFollows;
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
