package edu.mscd.thesis.util;

import java.util.Map;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.city.City;
import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;

/**
 * Game constants, methods, rules and behaviors
 * 
 * @author Mike
 *
 */
public class Rules {
	public static final int WORLD_X = 32;
	public static final int WORLD_Y = 24;
	public static final int TILE_COUNT = WORLD_X * WORLD_Y;
	public static final int WORLD_TILE_NOISE = 1;
	// Game Constants and factors
	public static final int MAX = 255;
	public static final int MAX_PERCENTAGE = 100;
	// Zone growth factors
	public static final int GROWTH_THRESHOLD = 125;
	public static final int BASE_GROWTH_COST = 25;
	public static final double GROWTH_RATE = 0.1;
	// City population and Person constants
	public static final int STARTING_POPULATION = 100;
	public static final int BASE_POPULATION = 50;
	public static final int MAX_POPULATION = TILE_COUNT * Density.VERYHIGH.getDensityLevel();
	public static final int BIRTH_RATE = 5;
	public static final int LIFE_SPAN = 100;
	public static final double R_DEMAND_BASE = 0.05;
	// Tile effect factors
	public static final int POLLUTION_UNIT = 1;
	public static final int POLLUTION_HALFLIFE = 10;
	public static final int LANDVALUE_UNIT = 1;
	public static final int LANDVALUE_DECAY = 10;

	/**
	 * Method to produce value for which a given tile's zone should grow. This
	 * value is used to determine if the zone's growth state increases,
	 * decreases or stays the same density each turn
	 * 
	 * @param t
	 *            - Tile to modify, and query
	 * @param z
	 *            - the Tile's zonetype
	 * @return a double value that will be used to increment the Zone's growth
	 *         value
	 */
	public static double getGrowthValue(Tile t, ZoneType z) {
		double value = 0;
		double valueAdded = t.getCurrentLandValue() - t.baseLandValue();
		if (z == ZoneType.COMMERICAL) {
			value += (valueAdded * 1.0 - t.getPollution() * 1.0);
		} else if (z == ZoneType.INDUSTRIAL) {
			value += (valueAdded * 0.1 + t.getPollution() * 1.0);
		} else if (z == ZoneType.RESIDENTIAL) {
			value += (valueAdded * 1.0 - t.getPollution() * 1.0);
		}
		return value * GROWTH_RATE;
	}


	public static double getDemandForZoneType(ZoneType zt, World w) {
		int r = w.getCity().getZoneCount(ZoneType.RESIDENTIAL);
		double rRatio = ((double) r + 1) / ((double) TILE_COUNT);
		int c = w.getCity().getZoneCount(ZoneType.COMMERICAL);
		double cRatio = ((double) c + 1) / ((double) TILE_COUNT);
		int i = w.getCity().getZoneCount(ZoneType.INDUSTRIAL);
		double iRatio = ((double) i + 1) / ((double) TILE_COUNT);
		if (zt == ZoneType.RESIDENTIAL) {
			double homelessness = w.getCity().percentageHomeless();
			double ciZones = (iRatio + cRatio) * 0.5;
			double ratio = (((ciZones - rRatio) / ciZones) * 0.5) + 0.5;
			double result = (ratio + homelessness) / 2.0;
			return Math.max(result, R_DEMAND_BASE);
		} else if (zt == ZoneType.COMMERICAL) {
			double joblessness = w.getCity().percentageUnemployed();
			double ratio = (((iRatio - cRatio) / iRatio) * 0.5) + 0.5;
			double result = (ratio + joblessness) / 2.0;
			return Math.max(result, 0);
		} else if (zt == ZoneType.INDUSTRIAL) {
			double joblessness = w.getCity().percentageUnemployed();
			double ratio = (((cRatio - iRatio) / cRatio) * 0.5) + 0.5;
			double result = (ratio + joblessness) / 2.0;
			return Math.max(result, 0);
		}
		return 0;
	}

	/**
	 * Produces the BASE value of a zonetype on a given tiletype Not contingent
	 * on current Tile/Zone instances or their states This determines initial
	 * growth behaviour of a zoneType on a TileType Some zones will initially
	 * grow on some tiles, where others growth (without other mitigating
	 * factors) will not grow.
	 * 
	 * @param t
	 *            - TileType
	 * @param z
	 *            - ZoneType
	 * @return - double value to be the zone's initial growth state
	 */
	public static double getValueForZoneOnTile(TileType t, ZoneType z) {
		if (z == ZoneType.COMMERICAL) {
			double value = (t.getBaseLandValue() * 3.0 + t.getMaterialValue()) / 4;
			return Math.min(MAX, value);
		} else if (z == ZoneType.INDUSTRIAL) {
			double value = (t.getBaseLandValue() * 0.0 + t.getMaterialValue() * 1.0) / 1;
			return Math.min(MAX, value);
		} else if (z == ZoneType.RESIDENTIAL) {
			double value = (t.getBaseLandValue() * 1.0 + t.getMaterialValue() * 0) / 1;
			return Math.min(MAX, value);
		}
		return 0;
	}

	/**
	 * Produces score on Model state REQUIRES: Model is reduced form
	 * 
	 * @param m
	 *            - Model THAT HAS BEEN REDUCED
	 * @return double score that is some value based on success metrics
	 */
	public static double score(Model m) {
		Map<CityProperty, Double> data = m.getWorld().getCity().getData().getDataMap();
		double cityScore = 0;
		for (CityProperty prop : CityProperty.values()) {
			if (data.containsKey(prop)) {
				double value = data.get(prop);
				value = value * prop.getNormalizationFactor();
				if (prop.needsInversion()) {
					value = 1.0 - value;
				}
				cityScore += value;
			}
		}
		cityScore = cityScore / CityProperty.values().length;
		cityScore = Util.boundValue(cityScore, 0, 1);
		return cityScore;
	}

	/**
	 * Model Scoring algorithm with given Weightvector Each parameter of
	 * CityData from the current world state is scored, normalized and then
	 * weighted
	 * 
	 * @param model
	 *            - Model state to score
	 * @param weights
	 *            - WeightVector of CityProperties - typically from GUI
	 * @return double [0-1.0] where 1.0 is high score.
	 */
	public static double score(Model model, WeightVector<CityProperty> weights) {
		if (weights == null) {
			return score(model);
		}
		World w = model.getWorld();
		City c = w.getCity();
		CityData data = c.getData();
		double weightSum = weights.getSum();
		double cityScore = 0;
		for (CityProperty prop : CityProperty.values()) {
			if (data.getDataMap().containsKey(prop)) {
				double value = data.getDataMap().get(prop);
				value = value * prop.getNormalizationFactor();
				if (prop.needsInversion()) {
					value = 1.0 - value;
				}
				cityScore += value * weights.getWeightFor(prop);
			}
		}
		cityScore = cityScore / weightSum;
		cityScore = Util.boundValue(cityScore, 0, 1);

		return cityScore;
	}


}
