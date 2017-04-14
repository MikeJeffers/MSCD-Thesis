package edu.mscd.thesis.model.city;

import java.util.HashMap;
import java.util.Map;

public class CityData {
	private Map<CityProperty, Double> map;

	public CityData() {
		map = new HashMap<CityProperty, Double>();
	}

	public Map<CityProperty, Double> getDataMap() {
		return this.map;
	}
	
	public void setProperty(CityProperty prop, double value){
		map.put(prop, value);
	}

}
