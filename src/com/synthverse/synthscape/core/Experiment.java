package com.synthverse.synthscape.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Experiment {
    private String name;
    private String serverName;
    private String batchId;

    private Date startDate;
    private Date endDate;

    private int gridWidth;
    private int gridHeight;

    private double obstacleDensity;
    private double resourceDensity;
    private int numberOfAgentsPerSpecies;
    private int numberOfCollectionSites;
    private int maxStepsPerAgent;

    private int simulationsPerExperiment;
    private int stepsPerSimulation;

    private ProblemComplexity problemComplexity;
    private Set<Species> speciesComposition = new HashSet<Species>();
    private Set<InteractionMechanism> interactionMechanisms = new HashSet<InteractionMechanism>();

    private String eventFileName;
    private boolean recordExperiment = false;
    private boolean flushAlways = false;

    private Experiment() {
	throw new AssertionError("not allowed");
    }

    public Experiment(String name) {
	this.name = name;

	try {
	    serverName = java.net.InetAddress.getLocalHost().getHostName();
	} catch (Exception e) {
	    serverName = "LOCAL";
	}

	batchId = Long.toHexString(System.currentTimeMillis());

    }

    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate() {
	this.startDate = new java.util.Date();
    }

    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate() {
	this.endDate = new java.util.Date();
    }

    public int getGridWidth() {
	return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
	this.gridWidth = gridWidth;
    }

    public int getGridHeight() {
	return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
	this.gridHeight = gridHeight;
    }

    public double getObstacleDensity() {
	return obstacleDensity;
    }

    public void setObstacleDensity(double obstacleDensity) {
	this.obstacleDensity = obstacleDensity;
    }

    public double getResourceDensity() {
	return resourceDensity;
    }

    public void setResourceDensity(double resourceDensity) {
	this.resourceDensity = resourceDensity;
    }

    

    public Set<Species> getSpeciesComposition() {
	return speciesComposition;
    }

    public void setSpeciesComposition(Set<Species> speciesComposition) {
	this.speciesComposition = speciesComposition;
    }

    public Set<InteractionMechanism> getInteractionMechanisms() {
	return interactionMechanisms;
    }

    public void setInteractionMechanisms(Set<InteractionMechanism> interactionMechanisms) {
	this.interactionMechanisms = interactionMechanisms;
    }

    public ProblemComplexity getProblemComplexity() {
	return problemComplexity;
    }

    public void setProblemComplexity(ProblemComplexity problemComplexity) {
	this.problemComplexity = problemComplexity;
    }

    public int getNumberOfAgentsPerSpecies() {
	return numberOfAgentsPerSpecies;
    }

    public void setNumberOfAgentsPerSpecies(int numberOfAgentsPerSpecies) {
	this.numberOfAgentsPerSpecies = numberOfAgentsPerSpecies;
    }

    public int getNumberOfCollectionSites() {
	return numberOfCollectionSites;
    }

    public void setNumberOfCollectionSites(int numberOfCollectionSites) {
	this.numberOfCollectionSites = numberOfCollectionSites;
    }

    public int getMaxStepsPerAgent() {
	return maxStepsPerAgent;
    }

    public void setMaxStepsPerAgent(int maxStepsPerAgent) {
	this.maxStepsPerAgent = maxStepsPerAgent;
    }

    public String getEventFileName() {

	// modify the file name to contain the id
	if (eventFileName != null) {

	    if (eventFileName.indexOf(".") != -1) {
		String prePart = eventFileName.substring(0,
			eventFileName.lastIndexOf('.'));
		String postPart = eventFileName.substring(eventFileName.lastIndexOf('.'));
		eventFileName = prePart + "_" + batchId + postPart;

	    } else {
		eventFileName += "_" + batchId;
	    }

	}

	return eventFileName;
    }

    public void setEventFileName(String eventFileName) {
	this.eventFileName = eventFileName;
    }

    public boolean isRecordExperiment() {
	return recordExperiment;
    }

    public void setRecordExperiment(boolean recordExperiment) {
	this.recordExperiment = recordExperiment;
    }

    public void addInteractionMechanism(InteractionMechanism interactionMechanism) {
	this.interactionMechanisms.add(interactionMechanism);

    }

    public void addSpecies(Species species) {
	this.speciesComposition.add(species);
    }

    public boolean isFlushAlways() {
	return flushAlways;
    }

    public void setFlushAlways(boolean flushAlways) {
	this.flushAlways = flushAlways;
    }

    public String getName() {
	return name;
    }

    public String getServerName() {
	return serverName;
    }

    public String getBatchId() {
	return batchId;
    }

    public int getSimulationsPerExperiment() {
        return simulationsPerExperiment;
    }

    public void setSimulationsPerExperiment(int simulationsPerExperiment) {
        this.simulationsPerExperiment = simulationsPerExperiment;
    }

    public int getStepsPerSimulation() {
        return stepsPerSimulation;
    }

    public void setStepsPerSimulation(int stepsPerSimulation) {
        this.stepsPerSimulation = stepsPerSimulation;
    }
    
    

}
