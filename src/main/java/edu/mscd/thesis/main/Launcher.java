package edu.mscd.thesis.main;

import java.util.Map;

import edu.mscd.thesis.controller.Controller;
import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.nn.NN;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.view.GUI;
import edu.mscd.thesis.view.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * JavaFX Application class to init all necessary objects
 * 
 * @author Mike
 *
 */
public class Launcher extends Application {
	private Map<String, String> args;

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
		this.args = super.getParameters().getNamed();
		

		model = initModel();
		ai = initAi(model);
		view = initView();
		controller = initController(model, view, ai);
		System.out.println(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		view.initView(primaryStage);
		
		controllerThread = new Thread(controller);
		controllerThread.start();
		initTestThread();
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
		AI ai = new NN(model);
		aiThread = new Thread(ai);
		aiThread.start();
		return ai;

	}
	
	/**
	 * Just for testing boot and close of application
	 */
	private void initTestThread(){
		if(args.containsKey("TEST")){
			if(args.get("TEST").equals("true")){
				Thread killer = new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}finally{
							System.out.println("stopping application..");
							Platform.exit();
						}
					}
				});
				killer.start();
			}
		}
	}

}
