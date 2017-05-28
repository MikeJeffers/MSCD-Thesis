package edu.mscd.thesis.model;

import java.util.Map;

import edu.mscd.thesis.model.city.CityProperty;

public interface ModelData{
	
	public Map<CityProperty, Double> getDataMap();
	
}
