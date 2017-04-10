package edu.mscd.thesis.controller;

public enum CityProperty {
	R_DEMAND("R"),
	C_DEMAND("C"),
	I_DEMAND("I"),
	HOMELESS("Homelessness"),
	UNEMPLOY("Unemployment"),
	WEALTH("Wealth"),
	HAPPY("Happiness"),
	SCORE("GameScore");
	
	private String label;
	
	CityProperty(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return this.label;
	}
	

}
