package edu.mscd.thesis.util;

import java.util.Map;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;

public class ModelToVec {

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
	 * Produce score of Tile's lack of potential density
	 * Where 1.0 indicates a empty tile, 0 indicates a tile that is fully utilized.
	 * @param t - Tile to produce value for
	 * @return double value [0.0-1.0]
	 */
	public static double getTileDensityScore(Tile t){
		if(t==null || !t.getType().isZonable()){
			return 0;
		}
		int maxDensity = t.getType().getMaxDensity().getDensityLevel();
		int currentDensity = t.getZoneDensity().getDensityLevel();
		double densityScore = 1.0-((1.0*currentDensity)/(1.0*maxDensity));
		return densityScore;
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
	
	
	public static double[] getTileAttributesAsVector(Tile t){
		double[] attributes = new double[5];
		if(t==null){
			return attributes;
		}
		double[]normDomain = new double[]{0.0, 1.0};
		double[] srcDomain = new double[]{0, Rules.MAX};
		attributes[0]=Util.mapValue(t.baseLandValue(), srcDomain, normDomain);
		attributes[1]=Util.mapValue(t.getCurrentLandValue(), srcDomain, normDomain);
		attributes[2]=Util.mapValue(t.getPollution(), srcDomain, normDomain);
		attributes[3]=Util.mapValue(t.materialValue(), srcDomain, normDomain);
		attributes[4]=getTileDensityScore(t);
		return attributes;
		
	}
	
	
	public static double[] getCityDataVector(CityData data){
		double[] vector = new double[CityProperty.values().length];
		if(data!=null){
			Map<CityProperty, Double> map = data.getDataMap();
			for(CityProperty prop: CityProperty.values()){
				if(map.containsKey(prop)){
					double value = data.getDataMap().get(prop);
					value = value*prop.getNormalizationFactor();
					if(prop.needsInversion()){
						value = 1.0-value;
					}
					vector[prop.ordinal()]=value;
				}
			}
		}
		return vector;
	}

}
