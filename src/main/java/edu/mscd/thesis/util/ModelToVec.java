package edu.mscd.thesis.util;

import java.util.Map;

import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.tiles.Tile;
import edu.mscd.thesis.model.tiles.TileType;
import edu.mscd.thesis.model.zones.ZoneType;

public class ModelToVec {

	
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
		double[] attributes = new double[Util.TILE_ATTRIBUTES];
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
	
	public static double[] getTileTypeAsVector(TileType t){
		double[] attributes = new double[5];
		if(t==null){
			return attributes;
		}
		double[]normDomain = new double[]{0.0, 1.0};
		double[] srcDomain = new double[]{0, Rules.MAX};
		attributes[0]=Util.mapValue(t.getBaseLandValue(), srcDomain, normDomain);
		attributes[1]=Util.mapValue(t.getBaseLandValue(), srcDomain, normDomain);
		attributes[2]=Util.mapValue(0, srcDomain, normDomain);
		attributes[3]=Util.mapValue(t.getMaterialValue(), srcDomain, normDomain);
		attributes[4]=0;
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
