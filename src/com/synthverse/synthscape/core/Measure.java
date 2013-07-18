package com.synthverse.synthscape.core;

public class Measure {

	public long numberOfSteps = 0;
	public long numberOfCellsDiscovered = 0;

	public long collectionSiteHits = 0;
	public long primaryCollectionSiteHits = 0;

	public long resourceHits = 0;

	public long extractedResourceHits = 0;
	
	public long processedResourceHits = 0;
	
	public long resourceProcesses = 0;

	public long resourceLoads = 0;

	public long resourceUnloads = 0;

	public long resourceExtracts = 0;

	public long resourceCaptures = 0;

	public long broadcasts = 0;

	public long broadcastMoves = 0;

	public long trailFollows = 0;
	public long trailRandomFollows = 0;

	public long trailHits = 0;

	public long trailDrops = 0;

	public void zeroAll() {
		numberOfCellsDiscovered = 0;
		collectionSiteHits = 0;

		resourceHits = 0;

		extractedResourceHits = 0;

		trailHits = 0;

		trailDrops = 0;

		primaryCollectionSiteHits = 0;
		resourceLoads = 0;

		resourceUnloads = 0;

		resourceExtracts = 0;

		resourceCaptures = 0;

		broadcasts = 0;

		broadcastMoves = 0;

		trailFollows = 0;
		trailRandomFollows = 0;
	}

	public Measure() {
		zeroAll();
	}

}
