package edu.mscd.thesis.controller;

import edu.mscd.thesis.util.Rules;

public enum CityProperty {
	R_DEMAND("Residential Demand", Rules.MAX_PERCENTAGE, true),
	C_DEMAND("Commercial Demand", Rules.MAX_PERCENTAGE, true),
	I_DEMAND("Industrial Demand", Rules.MAX_PERCENTAGE, true),
	HOMELESS("Homelessness %", Rules.MAX_PERCENTAGE, true),
	UNEMPLOY("Unemployment %", Rules.MAX_PERCENTAGE, true),
	WEALTH("Average Wealth", Rules.MAX_PERCENTAGE, false),
	HAPPY("Average Happiness", Rules.MAX_PERCENTAGE, false),
	POP("Population", 1, false);
	
	private String label;
	private double multiplerToView;
	private boolean invertValue;
	
	CityProperty(String label, double multiplier, boolean invert){
		this.label = label;
		this.multiplerToView = multiplier;
		this.invertValue = invert;
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

}
