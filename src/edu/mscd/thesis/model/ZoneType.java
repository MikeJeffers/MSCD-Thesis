package edu.mscd.thesis.model;

import javafx.scene.paint.Color;

public enum ZoneType {
	RESIDENTIAL("R", new Color(0, 1.0, 0, 0.5)),
	COMMERICAL("C", new Color(0, 0, 1.0, 0.5)),
	INDUSTRIAL("I", new Color(1.0, 1.0, 0, 0.5)),
	EMPTY("0", new Color(0.1, 0.1, 0.1, 0.1));
	
	private Color color;
	private String shortName;
	private ZoneType(String id, Color c){
		this.color = c;
		this.shortName = id;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	@Override
	public String toString(){
		return this.shortName;
	}

	
}
