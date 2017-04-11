package edu.mscd.thesis.controller;

public enum CityProperty {
	R_DEMAND("Residential Demand"),
	C_DEMAND("Commercial Demand"),
	I_DEMAND("Industrial Demand"),
	HOMELESS("Homelessness %"),
	UNEMPLOY("Unemployment %"),
	WEALTH("Average Wealth"),
	HAPPY("Average Happiness"),
	POP("Population"),
	SCORE("GameScore");
	
	private String label;
	
	CityProperty(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return this.label;
	}
	

}
