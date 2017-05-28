package edu.mscd.thesis.controller;

import edu.mscd.thesis.view.viewdata.ViewData;

public class ViewListener implements Observer<ViewData>{
	private Controller parent;
	
	public ViewListener(Controller parent){
		this.parent = parent;
	}

	@Override
	public void notifyNewData(ViewData data) {
		this.parent.notifyViewEvent(data);
		
	}

}
