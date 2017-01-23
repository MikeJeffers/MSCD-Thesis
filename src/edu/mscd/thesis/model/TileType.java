package edu.mscd.thesis.model;

public enum TileType {
	MOUNTAIN(false, false, 255, 155, 0), 
	HILL(true, true, 100, 220, 75), 
	BARREN(true, true, 0, 0, 255), 
	FERTILE(true, true, 200, 50, 155), 
	FOREST(true, true, 175, 155, 100), 
	LAKE(true, true, 50, 200, 50), 
	OCEAN(false, false, 200, 200, 0), 
	RIVER(true, true, 155, 200, 50);
	
	
	private boolean zonable;
	private boolean passable;
	private int materialValue;
	private int baseLandValue;
	private int maxDensity;
	private TileType(boolean zone, boolean pass, int material, int landval, int density){
		this.zonable = zone;
		this.passable = pass;
		this.materialValue = material;
		this.baseLandValue = landval;
		this.maxDensity = density;
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


}
