package edu.mscd.thesis.model.zones;

import javafx.scene.paint.Color;

public enum ZoneType {
	RESIDENTIAL("R", "Residential", new Color(0, 1.0, 0, 0.5)),
	COMMERICAL("C", "Commerical", new Color(0, 0, 1.0, 0.5)),
	INDUSTRIAL("I", "Industrial", new Color(1.0, 1.0, 0, 0.5)),
	EMPTY("0", "Empty", new Color(0.1, 0.1, 0.1, 0.1));
	
	private Color color;
	private String shortName;
	private String labelText;
	private ZoneType(String id, String label, Color c){
		this.color = c;
		this.shortName = id;
		this.labelText = label;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public String shortName(){
		return this.shortName;
	}
	
	@Override
	public String toString(){
		return this.labelText;
	}

	
}
