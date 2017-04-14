package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.city.CityData;

public class ModelListener implements Observer<CityData>{
	private Controller parent;
	
	public ModelListener(Controller parent){
		this.parent = parent;
	}

	@Override
	public void notifyNewData(CityData data) {
		this.parent.notifyModelEvent(data);
		
	}

}
