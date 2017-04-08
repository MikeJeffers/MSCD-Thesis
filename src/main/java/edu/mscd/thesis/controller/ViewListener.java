package edu.mscd.thesis.controller;

public class ViewListener implements Observer<UserData>{
	private Controller parent;
	
	public ViewListener(Controller parent){
		this.parent = parent;
	}

	@Override
	public void notifyNewData(UserData data) {
		this.parent.notifyViewEvent(data);
		
	}

}
