package edu.mscd.thesis.util;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
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
	public static final int MAX = 255;
	public static final int GROWTH_THRESHOLD = 55;
	public static final int BASE_GROWTH_COST = 10;
	public static final int STARTING_POPULATION = 100;
	public static final int BIRTH_RATE = 3;
	public static final int POLLUTION_HALFLIFE = 10;
	public static final int LANDVALUE_DECAY = 5;

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

}
