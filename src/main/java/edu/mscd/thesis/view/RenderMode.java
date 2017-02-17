package edu.mscd.thesis.view;

public enum RenderMode {
	NORMAL("Normal"),
	POLLUTION("Pollution"),
	LANDVALUE("Landvalues");
	
	private String shortName;
	private RenderMode(String id){
		this.shortName = id;
	}
	@Override
	public String toString(){
		return this.shortName;
	}

}
