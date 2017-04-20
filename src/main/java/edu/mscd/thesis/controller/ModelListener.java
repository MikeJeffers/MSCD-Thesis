package edu.mscd.thesis.controller;


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
