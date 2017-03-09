package edu.mscd.thesis.util;

import java.util.Collection;

import edu.mscd.thesis.model.City;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;

/**
 * Game constants, methods, rules and behaviors
 * 
 * @author Mike
 *
 */
public class Rules {
	public static final int WORLD_X = 20;
	public static final int WORLD_Y = 15;
	//Game Constants and factors
	public static final int MAX = 255;
	//Zone growth factors
	public static final int GROWTH_THRESHOLD = 125;
	public static final int BASE_GROWTH_COST = 25;
	//City population and Person constants
	public static final int STARTING_POPULATION = 100;
	public static final int BIRTH_RATE = 3;
	public static final int LIFE_SPAN = 100;
	//Tile effect factors
	public static final int POLLUTION_UNIT = 2;
	public static final int POLLUTION_HALFLIFE = 10;
	public static final int LANDVALUE_UNIT = 1;
	public static final int LANDVALUE_DECAY = 10;

	public static double getValueForZoneTypeWithEffects(Tile t, ZoneType z) {
		double value = getValueForZoneOnTile(t.getType(), z);
		if (z == ZoneType.COMMERICAL) {
			value += (t.getCurrentLandValue() - t.getPollution());
		} else if (z == ZoneType.INDUSTRIAL) {
			value += (t.getCurrentLandValue());
		} else if (z == ZoneType.RESIDENTIAL) {
			value += (t.getCurrentLandValue() - t.getPollution() * 2);
		}
		return Util.boundValue(value, 0, Rules.MAX);
	}

	public static double getDemandForZoneType(ZoneType zt, World w) {
		int r = w.getCity().zoneCount(ZoneType.RESIDENTIAL);
		int c = w.getCity().zoneCount(ZoneType.COMMERICAL);
		int i = w.getCity().zoneCount(ZoneType.INDUSTRIAL);
		// double currentRC =
		return -1;
	}

	public static double getValueForZoneOnTile(TileType t, ZoneType z) {
		if (z == ZoneType.COMMERICAL) {
			double value = (t.getBaseLandValue() * 3 + t.getMaterialValue()) / 4;
			return Math.min(MAX, value);
		} else if (z == ZoneType.INDUSTRIAL) {
			double value = (t.getBaseLandValue() * 1 + t.getMaterialValue() * 3) / 4;
			return Math.min(MAX, value);
		} else if (z == ZoneType.RESIDENTIAL) {
			double value = (t.getBaseLandValue() * 5 + t.getMaterialValue()) / 6;
			return Math.min(MAX, value);
		}
		return 0;
	}
	
	/**
	 * Produces score on Model state
	 * REQUIRES: Model is reduced form
	 * @param m - Model THAT HAS BEEN REDUCED
	 * @return double score that is some value based on success metrics
	 */
	public static double score(Model m){
		World w = m.getWorld();
		City c = w.getCity();
		
		double cityScore = 0;
		cityScore+=c.averageHappiness()/MAX;
		cityScore+=c.averageWealth()/MAX;
		cityScore+=(1-c.percentageHomeless());
		cityScore+=(1-c.percentageUnemployed());
		cityScore = cityScore/4.0;
		
		Tile[] tiles = w.getTiles();
		double tilesTotalScore = 0;
		for(int i=0; i<tiles.length;i++){
			double tileScore = 0;
			tileScore+=tiles[i].getCurrentLandValue()/MAX;
			tileScore-=tiles[i].getPollution()/MAX;
			tileScore+=tiles[i].getZoneDensity().getDensityLevel()/Density.VERYHIGH.getDensityLevel();
			tileScore = tileScore/3.0;
			tilesTotalScore+=tileScore;
		}
		tilesTotalScore = tilesTotalScore/tiles.length;
		return tilesTotalScore+cityScore;
	}

}
