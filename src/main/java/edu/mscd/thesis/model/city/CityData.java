package edu.mscd.thesis.model.city;

import java.util.HashMap;
import java.util.Map;
import edu.mscd.thesis.controller.ModelData;

public class CityData implements ModelData {
	private Map<CityProperty, Double> map;

	public CityData() {
		map = new HashMap<CityProperty, Double>();
	}

	@Override
	public Map<CityProperty, Double> getDataMap() {
		return this.map;
	}

	public void setProperty(CityProperty prop, double value) {
		map.put(prop, value);
	}

}
