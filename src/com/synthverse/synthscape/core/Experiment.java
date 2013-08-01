package com.synthverse.synthscape.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Experiment {
    private String name;
    private String signature;

    private Date startDate;
    private Date endDate;

    private int gridWidth;
    private int gridHeight;

    private double obstacleDensity;
    private double resourceDensity;
    private int numberOfAgentsPerSpecies;
    private int numberOfCollectionSites;

    private int numberOfSimulations;
    private int generationsPerSimulation;

    private ProblemComplexity problemComplexity;
    private Set<Species> speciesComposition = new HashSet<Species>();
    private Set<InteractionMechanism> interactionMechanisms = new HashSet<InteractionMechanism>();

    private Experiment() {
	throw new AssertionError("not allowed");
    }

    private String getSignature() {
	StringBuffer sb = new StringBuffer();
	sb.append(this.name.toUpperCase());
	sb.append('-');
	try {
	    sb.append(java.net.InetAddress.getLocalHost().getHostName()
		    .toUpperCase());
	} catch (Exception e) {
	    sb.append("LOCAL");
	}
	sb.append('-');
	sb.append(Long.toHexString(System.currentTimeMillis()).toUpperCase());

	return sb.toString();
    }

    public Experiment(String name) {
	this.name = name;

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

    public void setInteractionMechanisms(
	    Set<InteractionMechanism> interactionMechanisms) {
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

    public void addInteractionMechanism(
	    InteractionMechanism interactionMechanism) {
	this.interactionMechanisms.add(interactionMechanism);

    }
    
    public void addSpecies(Species species) {
	this.speciesComposition.add(species);
    }

}
