package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.view.View;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer implements Controller {
	private Model model;
	private View<UserData> view;
	private UserData currentSelection = new UserData();
	private boolean step = true;
	private boolean draw = true;
	private boolean aiMode = true;
	private int aiObserveCounter;
	private UserData aiActionPrev;
	private Model prevModelState;
	
	private AI ai;

	public GameLoop(Model model, View<UserData> view, AI ai) {
		this.model = model;
		this.view = view;
		view.attachObserver(this);
		this.ai = ai;
		this.prevModelState = ModelStripper.reducedCopy(model);

	}

	@Override
	public void handle(long now) {

		if (currentSelection.isStepMode() && step) {
			aiObserveCounter++;
			step = false;
			model.update();
			view.renderView(model);
			ai.setWorldState(model);
			if (ai != null && aiMode && aiObserveCounter>5) {
				UserData nextAction = ai.takeNextAction();
				if(nextAction!=null){
					aiActionPrev = nextAction;
					ai.addCase(model, prevModelState);
					aiObserveCounter=0;
					this.makeAIMove(aiActionPrev);
				}
			}

		} else if (draw) {
			draw = false;
			view.renderView(model);
		}

	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void run() {
		this.start();
	}
	
	private void makeAIMove(UserData action){
		prevModelState = ModelStripper.reducedCopy(model);
		model.userStateChange(action);
		step = action.isTakeStep();
		draw = action.isDrawFlag();
	
	}

	@Override
	public synchronized void notifyNewData(UserData data) {
		Pos2D old = this.currentSelection.getClickLocation();
		Pos2D newClick = data.getClickLocation();
		// TODO this is messy, only update model on new canvas click?
		if (!old.equals(newClick)) {
			model.userStateChange(data);
		}
		this.currentSelection = data.copy();
		step = data.isTakeStep();
		draw = data.isDrawFlag();
	}

}
