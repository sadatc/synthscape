package com.synthverse.synthscape.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Experiment {
    private String name;
    private String serverName;
    private String experimentId;

    private Date startDate;
    private Date endDate;

    private int gridWidth;
    private int gridHeight;

    private double obstacleDensity;
    private double resourceDensity;
    private int numberOfAgentsPerSpecies;
    private int numberOfCollectionSites;
    private int maxStepsPerAgent;

    private int numberOfSimulations;
    private int generationsPerSimulation;

    private ProblemComplexity problemComplexity;
    private Set<Species> speciesComposition = new HashSet<Species>();
    private Set<InteractionMechanism> interactionMechanisms = new HashSet<InteractionMechanism>();

    private String agentEventFileName;
    private boolean recordExperiment = false;
    private boolean flushAlways = false;

    private Experiment() {
	throw new AssertionError("not allowed");
    }

    public Experiment(String name) {
	this.name = name;

	try {
	    serverName = java.net.InetAddress.getLocalHost().getHostName().toUpperCase();
	} catch (Exception e) {
	    serverName = "LOCAL";
	}
	
	experimentId = Long.toHexString(System.currentTimeMillis()).toUpperCase();

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

    public int getNumberOfSimulations() {
	return numberOfSimulations;
    }

    public void setNumberOfSimulations(int numberOfSimulations) {
	this.numberOfSimulations = numberOfSimulations;
    }

    public int getGenerationsPerSimulation() {
	return generationsPerSimulation;
    }

    public void setGenerationsPerSimulation(int generationsPerSimulation) {
	this.generationsPerSimulation = generationsPerSimulation;
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

    public String getAgentEventFileName() {
	
	// modify the file name to contain the id
	if(agentEventFileName!=null) {
	    
	    if(agentEventFileName.indexOf(".")!=-1) {
		String prePart = agentEventFileName.substring(0,agentEventFileName.lastIndexOf('.'));
		String postPart = agentEventFileName.substring(agentEventFileName.lastIndexOf('.'));
		agentEventFileName = prePart+"_"+experimentId+postPart;
		
	    } else {
		agentEventFileName += "_"+experimentId;
	    }
	    
	}
	
	
	return agentEventFileName;
    }

    public void setAgentEventFileName(String agentEventFileName) {
	this.agentEventFileName = agentEventFileName;
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

    public String getExperimentId() {
        return experimentId;
    }
    
    

}
