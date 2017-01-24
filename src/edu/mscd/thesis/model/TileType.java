package edu.mscd.thesis.model;

import javafx.scene.paint.Color;

public enum TileType {
	MOUNTAIN("Mtn", false, false, 255, 155, 0, new Color(1.0, 1.0, 1.0, 0.9)), 
	HILL("Hill", true, true, 100, 220, 75, new Color(0.25, 0.5, 0.25, 0.9)), 
	BARREN("Barren", true, true, 0, 0, 255, new Color(0.4, 0.2, 0.2, 0.9)), 
	FERTILE("Fertile", true, true, 200, 50, 155, new Color(0.1, 0.76, 0.2, 0.9)), 
	FOREST("Forest", true, true, 175, 155, 100, new Color(0.22, 0.66, 0.25, 0.9)), 
	LAKE("Lake", true, true, 50, 200, 50, new Color(0, 0.1, 0.7, 0.9)), 
	OCEAN("Sea", false, false, 200, 200, 0, new Color(0, 0.005, 0.9, 0.9)), 
	RIVER("River", true, true, 155, 200, 50, new Color(0, 0, 1.0, 0.9));
	
	
	private boolean zonable;
	private boolean passable;
	private int materialValue;
	private int baseLandValue;
	private int maxDensity;
	private Color color;
	private String shortName;
	private TileType(String shortName, boolean zone, boolean pass, int material, int landval, int density, Color c){
		this.zonable = zone;
		this.passable = pass;
		this.materialValue = material;
		this.baseLandValue = landval;
		this.maxDensity = density;
		this.color = c;
		this.shortName = shortName;
	}
	public boolean isZonable() {
		return zonable;
	}

	public boolean isPassable() {
		return passable;
	}

	public int getMaterialValue() {
		return materialValue;
	}

	public int getBaseLandValue() {
		return baseLandValue;
	}

	public int getMaxDensity() {
		return maxDensity;
	}
	
	public Color getColor(){
		return this.color;
	}

	@Override
	public String toString(){
		return this.shortName;
	}

}
