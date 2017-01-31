package edu.mscd.thesis.model;

import edu.mscd.thesis.util.Util;
import javafx.scene.paint.Color;

public enum TileType {
	MOUNTAIN("Mtn", false, false, Util.MAX, Util.MAX*0.5, Util.MAX*0.0, new Color(1.0, 1.0, 1.0, 0.5)), 
	HILL("Hill", true, true, Util.MAX*0.4, Util.MAX*0.9, Util.MAX*0.23, new Color(0.25, 0.5, 0.25, 0.1)), 
	BARREN("Barren", true, true, Util.MAX*0.0, Util.MAX*0.0, Util.MAX, new Color(0.4, 0.2, 0.2, 0.1)), 
	FERTILE("Fertile", true, true, Util.MAX*0.9, Util.MAX*0.15, Util.MAX*0.6, new Color(0.1, 0.76, 0.2, 0.1)), 
	FOREST("Forest", true, true, Util.MAX*0.6, Util.MAX*0.4, Util.MAX*0.4, new Color(0.22, 0.66, 0.25, 0.1)), 
	LAKE("Lake", true, true, Util.MAX*0.14, Util.MAX*0.85, Util.MAX*0.2, new Color(0, 0.1, 0.7, 0.1)), 
	OCEAN("Sea", false, false, Util.MAX*0.7, Util.MAX*0.8, Util.MAX*0.0, new Color(0, 0.005, 0.9, 0.25)), 
	RIVER("River", true, true, Util.MAX*0.6, Util.MAX*0.8, Util.MAX*0.2, new Color(0, 0, 1.0, 0.1));
	
	
	private boolean zonable;
	private boolean passable;
	private double materialValue;
	private double baseLandValue;
	private double maxDensity;
	private Color color;
	private String shortName;
	private TileType(String shortName, boolean zone, boolean pass, double material, double landval, double density, Color c){
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

	public double getMaterialValue() {
		return materialValue;
	}

	public double getBaseLandValue() {
		return baseLandValue;
	}

	public double getMaxDensity() {
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
