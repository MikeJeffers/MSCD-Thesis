package edu.mscd.thesis.nn;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;

public class WorldRepresentation {

	public static double[] getWorldAsEnumeratedInputData(World w) {
		Tile[] tiles = w.getTiles();
		double[] repr = new double[tiles.length * 4];
		for (int i = 0; i < tiles.length; i += 4) {
			double zoneVal = tiles[i].getZoneValue() / Rules.MAX;
			TileType type = tiles[i].getType();
			ZoneType zType = tiles[i].getZoneType();
			double densityRating = 0;
			if (type.isZonable()) {
				densityRating = tiles[i].getZoneDensity().getDensityLevel() / type.getMaxDensity().getDensityLevel();
			}

			repr[i] = zoneVal;
			repr[i + 1] = densityRating;
			repr[i + 2] = type.ordinal() / TileType.values().length;
			repr[i + 3] = zType.ordinal() / ZoneType.values().length;
		}
		return repr;
	}

	public static double[] getWorldAsZoneVector(World w) {
		Tile[] tiles = w.getTiles();
		double[] repr = new double[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			ZoneType zType = tiles[i].getZoneType();
			repr[i] = ((double) zType.ordinal()) / ((double) ZoneType.values().length);
		}
		return repr;
	}

	/**
	 * Create NN input-layer friendly representation of Zonetypes from a given
	 * Tile
	 * 
	 * @param t
	 *            Tile to represent as zonetype vector of doubles
	 * @return double array of length of total zoneTypes where value of 1.0
	 *         indicates signal of Zonetype Commercial Zone represented as: [0,
	 *         1.0, 0, 0]
	 */
	public static double[] getTileAsZoneVector(Tile t) {
		double[] repr = new double[ZoneType.values().length];
		if (t == null) {
			repr[ZoneType.EMPTY.ordinal()] = 1.0;
		} else {
			repr[t.getZoneType().ordinal()] = 1.0;
		}
		return repr;
	}

	public static double[] getZoneAsVector(ZoneType z) {
		double[] repr = new double[ZoneType.values().length];
		repr[z.ordinal()] = 1.0;
		return repr;
	}

}
