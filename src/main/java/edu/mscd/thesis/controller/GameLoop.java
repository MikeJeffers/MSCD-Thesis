package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.view.View;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer implements Controller{
	private Model model;
	private View view;
	private UserData currentSelection = new UserData();
	private boolean step = true;

	public GameLoop(Model model, View view) {
		this.model = model;
		this.view = view;
		view.attachObserver(this);
		
	}

	@Override
	public void handle(long now) {
		
		if(currentSelection.isStepMode() && step){
			step = false;
			model.update();
			view.renderView(model);
			
		}
		
	}


	@Override
	public synchronized void notifyUserDataChange(UserData data) {
		this.currentSelection = data;
		model.userStateChange(data);
		step = true;
		
	}
	
	@Override
	public void start(){
		super.start();
		
	}
	
	
	@Override
	public void stop(){
		super.stop();
	}

	@Override
	public void run() {
		this.start();
	}
	
	

}
