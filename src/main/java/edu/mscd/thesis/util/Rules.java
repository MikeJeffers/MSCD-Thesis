package edu.mscd.thesis.util;

import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.ZoneType;

public class Rules {
	public static final int MAX = 255;
	public static final int GROWTH_THRESHOLD = 100;
	public static final int BASE_GROWTH_COST = 10;
	
	
	
	public static double getValueForZoneOnTile(TileType t, ZoneType z){
		if(z==ZoneType.COMMERICAL){
			double value = (t.getBaseLandValue()*3+t.getMaterialValue())/4;
			return Math.min(MAX, value);
		}else if(z==ZoneType.INDUSTRIAL){
			double value = (t.getBaseLandValue()*1+t.getMaterialValue()*3)/4;
			return Math.min(MAX, value);
		}else if(z==ZoneType.RESIDENTIAL){
			double value = (t.getBaseLandValue()*5+t.getMaterialValue())/6;
			return Math.min(MAX, value);
		}
		return 0;
	}

}
