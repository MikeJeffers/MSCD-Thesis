package edu.mscd.thesis.view;

public enum RenderMode {
	NORMAL("Normal"),
	TERRAIN("Terrain"),
	POLLUTION("Pollution"),
	LANDVALUE("Landvalues"),
	GROWTH("Growth"),
	RESOURCE("Resources"),
	DENSITY("Density"),
	POLICY("AI-Policy Map");
	
	private String shortName;
	private RenderMode(String id){
		this.shortName = id;
	}
	@Override
	public String toString(){
		return this.shortName;
	}

}
