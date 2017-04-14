package edu.mscd.thesis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;

public class CityDataWeightVector implements WeightVector<CityProperty>{
	private Map<CityProperty, Double> weightMap = new HashMap<CityProperty, Double>();
	
	public CityDataWeightVector(){
	}

	@Override
	public void setWeightFor(CityProperty key, double value) {
		this.weightMap.put(key, value);
	}

	@Override
	public double getWeightFor(CityProperty key) {
		return this.weightMap.get(key);
	}

	@Override
	public double getSum() {
		double sum= 0;
		for(Entry<CityProperty, Double> pair: this.weightMap.entrySet()){
			sum+=pair.getValue();
		}
		return sum;
	}
	
	

}
