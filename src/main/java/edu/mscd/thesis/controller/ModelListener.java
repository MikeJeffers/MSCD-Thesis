package edu.mscd.thesis.controller;

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
