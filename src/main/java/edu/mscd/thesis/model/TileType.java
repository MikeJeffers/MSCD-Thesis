package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.util.Rules;
import javafx.scene.paint.Color;

public enum TileType {
	MOUNTAIN("Mtn", false, false, Rules.MAX, Rules.MAX*0.35, Density.NONE, new Color(1.0, 1.0, 1.0, 0.5)), 
	HILL("Hill", true, true, Rules.MAX*0.4, Rules.MAX*0.75, Density.MED, new Color(0.25, 0.5, 0.25, 0.1)), 
	BARREN("Barren", true, true, Rules.MAX*0.0, Rules.MAX*0.11, Density.VERYHIGH, new Color(0.4, 0.2, 0.2, 0.1)), 
	FERTILE("Fertile", true, true, Rules.MAX*0.9, Rules.MAX*0.15,Density.HIGH, new Color(0.1, 0.76, 0.2, 0.1)), 
	FOREST("Forest", true, true, Rules.MAX*0.6, Rules.MAX*0.35, Density.HIGH, new Color(0.22, 0.66, 0.25, 0.1)), 
	LAKE("Lake", true, true, Rules.MAX*0.14, Rules.MAX*0.65, Density.LOW, new Color(0, 0.1, 0.7, 0.1)), 
	OCEAN("Sea", false, false, Rules.MAX*0.7, Rules.MAX*0.6, Density.NONE, new Color(0, 0.005, 0.9, 0.25)), 
	RIVER("River", true, true, Rules.MAX*0.6, Rules.MAX*0.7, Density.VERYLOW, new Color(0, 0, 1.0, 0.1));
	
	
	private boolean zonable;
	private boolean passable;
	private double materialValue;
	private double baseLandValue;
	private Density maxDensity;
	private Color color;
	private String shortName;
	private TileType(String shortName, boolean zone, boolean pass, double material, double landval, Density density, Color c){
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

	public Density getMaxDensity() {
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
