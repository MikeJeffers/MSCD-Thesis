package edu.mscd.thesis.util;


import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.City;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
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
	public static final int TILE_COUNT = WORLD_X*WORLD_Y;
	//Game Constants and factors
	public static final int MAX = 255;
	//Zone growth factors
	public static final int GROWTH_THRESHOLD = 125;
	public static final int BASE_GROWTH_COST = 25;
	//City population and Person constants
	public static final int STARTING_POPULATION = 100;
	public static final int BASE_POPULATION = 10;
	public static final int MAX_POPULATION = TILE_COUNT*Density.VERYHIGH.getDensityLevel();
	public static final int BIRTH_RATE = 3;
	public static final int LIFE_SPAN = 100;
	public static final double R_DEMAND_BASE = 0.05;
	//Tile effect factors
	public static final int POLLUTION_UNIT = 2;
	public static final int POLLUTION_HALFLIFE = 10;
	public static final int LANDVALUE_UNIT = 1;
	public static final int LANDVALUE_DECAY = 10;

	public static double getValueForZoneTypeWithEffects(Tile t, ZoneType z) {
		double value = getValueForZoneOnTile(t.getType(), z);
		if (z == ZoneType.COMMERICAL) {
			value += (t.getCurrentLandValue() - t.getPollution()/4.0);
		} else if (z == ZoneType.INDUSTRIAL) {
			value += (t.getCurrentLandValue());
		} else if (z == ZoneType.RESIDENTIAL) {
			value += (t.getCurrentLandValue() - t.getPollution() * 2);
		}
		return Util.boundValue(value, 0, Rules.MAX);
	}

	public static double getDemandForZoneType(ZoneType zt, World w) {
		int r = w.getCity().getZoneCount(ZoneType.RESIDENTIAL);
		int c = w.getCity().getZoneCount(ZoneType.COMMERICAL);
		int i = w.getCity().getZoneCount(ZoneType.INDUSTRIAL);
		if(zt==ZoneType.RESIDENTIAL){
			return Math.max(w.getCity().percentageHomeless(), R_DEMAND_BASE);
		}else if(zt==ZoneType.COMMERICAL){
			double consumerDemand = ((double)r)/((double)TILE_COUNT);
			return Math.max((w.getCity().percentageUnemployed()+consumerDemand)/1.5, 0);
		}else if(zt==ZoneType.INDUSTRIAL){
			double commerceDemand = ((double)c)/((double)TILE_COUNT);
			return Math.max((w.getCity().percentageUnemployed()+commerceDemand)/1.5, commerceDemand);
		}
		return 0;
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
	public static double score(Model<UserData, CityData> m){
		World w = m.getWorld();
		City c = w.getCity();
		double weightSum = 10.0;
		double cityScore = 0;
		cityScore+=(c.averageHappiness()/MAX)*((3*weightSum)/10);
		cityScore+=(c.averageWealth()/MAX)*((4*weightSum)/10);
		cityScore+=(1-c.percentageHomeless())*((1*weightSum)/10);
		cityScore+=(1-c.percentageUnemployed())*((1*weightSum)/10);
		cityScore+=Math.min((c.totalPopulation()/w.getTiles().length), 1.0)*((1*weightSum)/10);
		cityScore = cityScore/(5.0*weightSum);
		
		Tile[] tiles = w.getTiles();
		double tilesTotalScore = 0;
		for(int i=0; i<tiles.length;i++){
			double tileScore = score(tiles[i]);
			tilesTotalScore+=tileScore;
		}
		tilesTotalScore = tilesTotalScore/tiles.length;
		return tilesTotalScore+cityScore;
	}
	
	public static double score(Tile t){
		if(t==null){
			return 0;
		}
		double tileScore = 0;
		double weightSum = 10.0;
		tileScore+=(t.getCurrentLandValue()/MAX)*((2*weightSum)/8);
		tileScore-=(t.getPollution()/MAX)*((2*weightSum)/8);
		tileScore+=(t.getZoneDensity().getDensityLevel()/Density.VERYHIGH.getDensityLevel())*((4*weightSum)/8);
		tileScore = tileScore/(weightSum);
		return tileScore;
		
	}

}
