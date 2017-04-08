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
	
	private String name;
	
	CityProperty(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	

}
