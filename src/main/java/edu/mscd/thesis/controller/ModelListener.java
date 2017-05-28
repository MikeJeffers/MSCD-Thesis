package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.ModelData;

public class ModelListener implements Observer<ModelData>{
	private Controller parent;
	
	public ModelListener(Controller parent){
		this.parent = parent;
	}

	@Override
	public void notifyNewData(ModelData data) {
		this.parent.notifyModelEvent(data);
		
	}

}
