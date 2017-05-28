package edu.mscd.thesis.geodata;

import edu.mscd.thesis.model.tiles.TileType;
import edu.mscd.thesis.model.zones.Density;
import javafx.scene.paint.Color;

public enum GeoType {
	WATER(Color.rgb(76, 112, 163), Density.NONE,new TileType[]{TileType.WATER}),
	SNOW(Color.rgb(211,226,252),  Density.NONE,new TileType[]{TileType.MOUNTAIN}),
	NO_DENSE(Color.rgb(226,204,204), Density.VERYLOW, new TileType[]{TileType.BARREN, TileType.SLOPE, TileType.HIGHLAND}),
	LOW_DENSE(Color.rgb(221,153,130), Density.LOW, new TileType[]{TileType.BARREN, TileType.HILL, TileType.FOOTHILL}),
	MED_DENSE(Color.rgb(242,0,0),Density.MED,  new TileType[]{TileType.BARREN, TileType.POND, TileType.FOREST}),
	HI_DENSE(Color.rgb(173,0,0), Density.HIGH, new TileType[]{TileType.BARREN}),
	ROCKS(Color.rgb(183,178,165),Density.NONE,  new TileType[]{TileType.MOUNTAIN}),
	SHORE(Color.rgb(252,252,252),Density.NONE,  new TileType[]{TileType.BEACH}),
	FOREST_DECIDUOUS(Color.rgb(107,173,102),Density.NONE,  new TileType[]{TileType.FOREST, TileType.HILL, TileType.STREAM}),
	FOREST_EVERGREEN(Color.rgb(30,102,51),Density.NONE,  new TileType[]{TileType.FOREST, TileType.FOOTHILL}),
	FOREST_MIX(Color.rgb(186,204,147),Density.NONE,  new TileType[]{TileType.FOREST, TileType.HILL}),
	SHRUBS(Color.rgb(209,188,130),Density.NONE,  new TileType[]{TileType.HIGHLAND, TileType.BARREN}),
	GRASSLAND(Color.rgb(229,229,193),Density.NONE,  new TileType[]{TileType.HILL, TileType.FERTILE}),
	PASTURE(Color.rgb(224,219,63),Density.NONE,  new TileType[]{TileType.FERTILE, TileType.HILL}),
	CROPS(Color.rgb(175,117,43), Density.NONE, new TileType[]{TileType.FERTILE}),
	WETLAND_WOODY(Color.rgb(188,219,237),Density.NONE,  new TileType[]{TileType.DELTA, TileType.POND, TileType.STREAM}),
	WETLAND(Color.rgb(117,165,191), Density.NONE, new TileType[]{TileType.DELTA});
	
	

	private TileType[] types;
	private Color color;
	private Density density;
	private GeoType(Color color, Density density, TileType[] tileTypes){
		this.types = tileTypes;
		this.color = color;
		this.density = density;
	}
	
	public TileType[] getPossibleTiles(){
		return this.types;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public Density getDensity(){
		return this.density;
	}

}
