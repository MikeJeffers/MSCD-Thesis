package edu.mscd.thesis.main;

import edu.mscd.thesis.controller.Controller;
import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.nn.NeuralNet;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.view.GUI;
import edu.mscd.thesis.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX Application class to init all necessary objects
 * 
 * @author Mike
 *
 */
public class Launcher extends Application {
	View<UserData> view;
	Model model;
	Controller controller;
	AI ai;

	public Launcher() {
	}
	
	@Override
	public void init(){
		model = initModel();
		view = initView();
		controller = initController(model, view);
		ai = new NeuralNet(model);
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		view.initView(primaryStage);
		Thread t = new Thread(controller);
		t.run();
	}

	private static Model initModel() {
		return new WorldImpl(Rules.WORLD_X, Rules.WORLD_Y);
	}

	private static Controller initController(Model model, View<UserData> view) {
		return new GameLoop(model, view);
	}

	private static View<UserData> initView() {
		return new GUI();
	}

}
