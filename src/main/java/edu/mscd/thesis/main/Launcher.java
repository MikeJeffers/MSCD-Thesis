package edu.mscd.thesis.main;

import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.ai.AI;
import edu.mscd.thesis.ai.NN;
import edu.mscd.thesis.ai.RandomBenchmark;
import edu.mscd.thesis.controller.Controller;
import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
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

	private Map<String, String> args;
	private static final String RANDOM_BENCH = "rand";
	private static final String MAP_FROM_FILE = "map";
	private static final String SEED_CITY = "seed";

	public Launcher() {
	}

	@Override
	public void init() {
		this.args = this.getParameters().getNamed();
		this.parseArgs();
		model = initModel();
		ai = initAi(model);
		view = initView();
		controller = initController(model, view, ai);
	}
	
	private void parseArgs(){
		if(Util.REPORT){
			StringBuilder sb = new StringBuilder();
			for(Entry<String,String> entry: this.args.entrySet()){
				sb.append("_");
				sb.append(entry.getKey());
				sb.append("-");
				sb.append(entry.getValue());
				sb.append("_");
			}
			Util.title = sb.toString();
			Util.report("ARGS="+sb.toString());
		}
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
		Model m = null;
		if(args.containsKey(MAP_FROM_FILE)){
			boolean seedCity = false;
			if(args.containsKey(SEED_CITY) && args.get(SEED_CITY).equalsIgnoreCase("true")){
				seedCity = true;
			}
			m = new WorldImpl(Rules.WORLD_X, Rules.WORLD_Y, args.get(MAP_FROM_FILE), seedCity);
		}else{
			m = new WorldImpl(Rules.WORLD_X, Rules.WORLD_Y, "", false);
		}
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
		AI ai = null;
		if (args.containsKey(RANDOM_BENCH)&& args.get(RANDOM_BENCH).equalsIgnoreCase("true")) {
			ai = new RandomBenchmark(initialState);
		} else {
			ai = new NN(initialState);
		}
		aiThread = new Thread(ai);
		aiThread.start();
		return ai;

	}

}
