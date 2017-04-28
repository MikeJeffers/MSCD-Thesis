package edu.mscd.thesis.model.city;

import edu.mscd.thesis.util.Rules;

public enum CityProperty {
	R_DEMAND("Residential Demand", Rules.MAX_PERCENTAGE, true, 1.0),
	C_DEMAND("Commercial Demand", Rules.MAX_PERCENTAGE, true, 1.0),
	I_DEMAND("Industrial Demand", Rules.MAX_PERCENTAGE, true, 1.0),
	DENSITY("Average Density", Rules.MAX_PERCENTAGE, false, 1.0),
	HOMELESS("Homelessness %", Rules.MAX_PERCENTAGE, true, 1.0),
	UNEMPLOY("Unemployment %", Rules.MAX_PERCENTAGE, true, 1.0),
	WEALTH("Average Wealth", Rules.MAX_PERCENTAGE, false, 1.0),
	HAPPY("Average Happiness", Rules.MAX_PERCENTAGE, false, 1.0),
	POP("Population", 1.0, false, 1.0/Rules.MAX_POPULATION);
	
	private String label;
	private double multiplerToView;
	private boolean invertValue;
	private double normalizationFactor;
	
	CityProperty(String label, double multiplier, boolean invert, double normFactor){
		this.label = label;
		this.multiplerToView = multiplier;
		this.invertValue = invert;
		this.normalizationFactor = normFactor;
	}
	
	public String getLabel(){
		return this.label;
	}
	
	public double getMultiplier(){
		return this.multiplerToView;
	}
	
	public boolean needsInversion(){
		return this.invertValue;
	}
	
	public double getNormalizationFactor(){
		return this.normalizationFactor;
	}

}
