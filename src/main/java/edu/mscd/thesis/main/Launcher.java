package edu.mscd.thesis.main;

import edu.mscd.thesis.controller.Controller;
import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.view.GUI;
import edu.mscd.thesis.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
	View view;
	Model model;
	Controller controller;

	public Launcher() {
		System.out.println(Thread.currentThread());
		model = initModel();
		view = initView();
		controller = initController(model, view);
		Thread t = new Thread(controller);
		t.run();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		view.initView(primaryStage);
	}

	private static Model initModel() {
		return new WorldImpl(Rules.WORLD_X, Rules.WORLD_Y);
	}

	private static Controller initController(Model model, View view) {
		return new GameLoop(model, view);
	}

	private static View initView() {
		return new GUI();
	}

}
