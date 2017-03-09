package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.view.View;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer implements Controller {
	private Model model;
	private View<UserData> view;
	private UserData currentSelection = new UserData();
	private boolean step = true;
	private boolean draw = true;
	private AI ai;

	public GameLoop(Model model, View<UserData> view, AI ai) {
		this.model = model;
		this.view = view;
		view.attachObserver(this);
		this.ai = ai;
		System.out.println(ai);

	}

	@Override
	public void handle(long now) {

		if (currentSelection.isStepMode() && step) {
			step = false;
			model.update();
			view.renderView(model);
			if (ai != null) {
				ai.takeNextAction();
				ai.addCase(model, Rules.score(model));
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
