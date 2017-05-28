package edu.mscd.thesis.model.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.util.WeightVector;

public class CityDataWeightVector implements WeightVector<CityProperty>{
	private Map<CityProperty, Double> weightMap = new HashMap<CityProperty, Double>();
	
	public CityDataWeightVector(){
	}

	@Override
	public void setWeightFor(CityProperty key, double value) {
		this.weightMap.put(key, Math.max(value, 0.000001));
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

	@Override
	public int getNumWeights() {
		return this.weightMap.size();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" {");
		for(Entry<CityProperty, Double> pair: this.weightMap.entrySet()){
			sb.append("[");
			sb.append(pair.getKey());
			sb.append(":");
			sb.append(pair.getValue());
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}
	
	

}
