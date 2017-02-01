package edu.mscd.thesis.model.zones;

public enum Density {
	NONE(0, "None"),
	VERYLOW(1, "Very Low"),
	LOW(2, "Low"),
	MED(3, "Medium"),
	HIGH(4, "High"),
	VERYHIGH(5, "Very High");
	
	private int densityLevel;
	private String shortName;
	
	private Density(int level, String name){
		this.shortName = name;
		this.densityLevel = level;
	}

	public int getDensityLevel() {
		return densityLevel;
	}
	
	public Density getNextLevel(){
		int max = Density.values().length;
		if(this.ordinal()+1>=max){
			return this;
		}
		return Density.values()[(this.ordinal()+1)%max];
	}
	
	public Density getPrevLevel(){
		int min = 0;
		if(this.ordinal()-1<min){
			return this;
		}
		return Density.values()[this.ordinal()-1];
	}

	
	@Override
	public String toString(){
		return this.shortName;
	}
	

}
