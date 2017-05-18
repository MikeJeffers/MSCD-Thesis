package edu.mscd.thesis.main;

import edu.mscd.thesis.controller.Controller;
import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.nn.NN;
import edu.mscd.thesis.nn.RandomBenchmark;
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
	private View view;
	private Model model;
	private Controller controller;
	private AI ai;

	private Thread modelThread;
	private Thread controllerThread;
	private Thread aiThread;

	public Launcher() {
	}

	@Override
	public void init() {
		model = initModel();
		ai = initAi(model);
		view = initView();
		controller = initController(model, view, ai);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		view.initView(primaryStage);
		controllerThread = new Thread(controller);
		controllerThread.start();
	}

	@Override
	public void stop() {
		System.out.println("Closing application....");
		model.halt();
		ai.halt();
		try {
			modelThread.join();
			controllerThread.join();
			aiThread.join();
		} catch (InterruptedException e) {

		}
		System.out.println("...Application stopped!");
	}

	private Model initModel() {
		Model m = new WorldImpl(Rules.WORLD_X, Rules.WORLD_Y);
		modelThread = new Thread(m);
		modelThread.start();
		return m;
	}

	private Controller initController(Model model, View view, AI ai) {
		return new GameLoop(model, view, ai);
	}

	private View initView() {
		return new GUI();
	}

	private AI initAi(Model initialState) {
		//AI ai = new RandomBenchmark(initialState);
		AI ai = new NN(initialState);
		aiThread = new Thread(ai);
		aiThread.start();
		return ai;

	}



}
