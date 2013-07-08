package com.synthverse.synthscape.core;



public class Data {
	
	public long numberOfSteps = 0;
	public long numberOfCellsDiscovered = 0;

	public long homeHits = 0;
	public long primaryHomeHits = 0;

	public long resourceHits = 0;
	public long resourceAHits = 0;
	public long resourceBHits = 0;
	public long resourceCHits = 0;

	public long extractHits = 0;
	public long extractAHits = 0;
	public long extractBHits = 0;
	public long extractCHits = 0;

	public long resourceLoads = 0;
	public long resourceALoads = 0;
	public long resourceBLoads = 0;
	public long resourceCLoads = 0;

	public long resourceUnloads = 0;
	public long resourceAUnloads = 0;
	public long resourceBUnloads = 0;
	public long resourceCUnloads = 0;

	public long resourceExtracts = 0;
	public long resourceAExtracts = 0;
	public long resourceBExtracts = 0;
	public long resourceCExtracts = 0;

	public long resourceCaptures = 0;
	public long resourceACaptures = 0;
	public long resourceBCaptures = 0;
	public long resourceCCaptures = 0;

	public long broadcasts = 0;
	public long aBroadcasts = 0;
	public long bBroadcasts = 0;
	public long cBroadcasts = 0;

	public long broadcastMoves = 0;
	public long aBroadcastMoves = 0;
	public long bBroadcastMoves = 0;
	public long cBroadcastMoves = 0;

	public long trailFollows = 0;
	public long trailAFollows = 0;
	public long trailBFollows = 0;
	public long trailCFollows = 0;
	public long trailRandomFollows = 0;

	public long trailHits = 0;
	public long trailAHits = 0;
	public long trailBHits = 0;
	public long trailCHits = 0;

	public long trailDrops = 0;
	public long trailADrops = 0;
	public long trailBDrops = 0;
	public long trailCDrops = 0;

	public void zeroAll() {
		numberOfCellsDiscovered = 0;
		homeHits = 0;

		resourceHits = 0;
		resourceAHits = 0;
		resourceBHits = 0;
		resourceCHits = 0;

		extractHits = 0;
		extractAHits = 0;
		extractBHits = 0;
		extractCHits = 0;

		trailHits = 0;
		trailAHits = 0;
		trailBHits = 0;
		trailCHits = 0;

		trailDrops = 0;
		trailADrops = 0;
		trailBDrops = 0;
		trailCDrops = 0;

		primaryHomeHits = 0;
		resourceLoads = 0;
		resourceALoads = 0;
		resourceBLoads = 0;
		resourceCLoads = 0;

		resourceUnloads = 0;
		resourceAUnloads = 0;
		resourceBUnloads = 0;
		resourceCUnloads = 0;

		resourceExtracts = 0;
		resourceAExtracts = 0;
		resourceBExtracts = 0;
		resourceCExtracts = 0;

		resourceCaptures = 0;
		resourceACaptures = 0;
		resourceBCaptures = 0;
		resourceCCaptures = 0;

		broadcasts = 0;
		aBroadcasts = 0;
		bBroadcasts = 0;
		cBroadcasts = 0;

		broadcastMoves = 0;
		aBroadcastMoves = 0;
		bBroadcastMoves = 0;
		cBroadcastMoves = 0;

		trailFollows = 0;
		trailAFollows = 0;
		trailBFollows = 0;
		trailCFollows = 0;
		trailRandomFollows = 0;
	}

	public Data() {
		zeroAll();
	}

}
