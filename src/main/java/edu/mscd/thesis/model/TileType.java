package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.util.Rules;
import javafx.scene.paint.Color;

public enum TileType {
	MOUNTAIN("Mountain", false, false, Rules.MAX, Rules.MAX*0.01, Density.NONE, new Color(1.0, 1.0, 1.0, 0.5)), 
	SLOPE("Slope", true, true, Rules.MAX*0.95, Rules.MAX*0.1, Density.VERYLOW, new Color(0.9, 0.9, 0.8, 0.2)), 
	HIGHLAND("HighLands", true, true, Rules.MAX*0.65, Rules.MAX*0.25, Density.LOW, new Color(0.5, 0.9, 0.5, 0.2)), 
	FOOTHILL("FootHills", true, true, Rules.MAX*0.45, Rules.MAX*0.2, Density.MED, new Color(0.5, 0.9, 0.2, 0.2)), 
	HILL("Hill", true, true, Rules.MAX*0.15, Rules.MAX*0.65, Density.HIGH, new Color(0.25, 0.6, 0.25, 0.2)), 
	BARREN("Barren", true, true, Rules.MAX*0.05, Rules.MAX*0.05, Density.VERYHIGH, new Color(0.4, 0.2, 0.2, 0.2)), 
	FERTILE("Fertile", true, true, Rules.MAX*0.85, Rules.MAX*0.45,Density.VERYHIGH, new Color(0.1, 0.76, 0.2, 0.2)), 
	FOREST("Forest", true, true, Rules.MAX*0.7, Rules.MAX*0.65, Density.HIGH, new Color(0.22, 0.86, 0.25, 0.2)), 
	LAKE("Lake", true, true, Rules.MAX*0.25, Rules.MAX*0.75, Density.VERYLOW, new Color(0, 0.1, 0.7, 0.2)), 
	RIVER("River", true, true, Rules.MAX*0.55, Rules.MAX*0.55, Density.MED, new Color(0, 0.25, 1.0, 0.2)),
	DELTA("Delta", true, true, Rules.MAX*0.75, Rules.MAX*0.3, Density.LOW, new Color(0.25, 0, 1.0, 0.2)),
	BEACH("Beach", true, true, Rules.MAX*0.1, Rules.MAX*0.95, Density.HIGH, new Color(0.8, 0.8, 0.1, 0.2)),
	OCEAN("Sea", false, false, Rules.MAX*0.01, Rules.MAX, Density.NONE, new Color(0, 0.005, 1.0, 0.5));
	
	
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
