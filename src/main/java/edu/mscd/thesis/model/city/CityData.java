package edu.mscd.thesis.model.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	@Override
	public String toString(){
		if(this.map!=null && !this.map.isEmpty()){
			StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
			sb.append(" has datamap:{");
			for(Entry<CityProperty, Double> pair: this.map.entrySet()){
				sb.append("[");
				sb.append(pair.getKey());
				sb.append(":");
				sb.append(pair.getValue());
				sb.append("]");
			}
			sb.append("}");
			return sb.toString();	
		}else{
			return this.getClass().getSimpleName()+" is empty";
		}
	}

}
