package edu.mscd.thesis.controller;

import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.util.ArrayObservableList;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.view.View;
import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class GameLoop extends AnimationTimer implements Controller {

	private ObservableList<CityData> modelData;
	private Model<UserData, CityData> model;
	private View<UserData> view;
	private UserData currentSelection = new UserData();
	private boolean step = true;
	private boolean draw = true;
	private AiMode aiMode = AiMode.ON;
	private int aiObserveCounter;
	private UserData aiActionPrev;
	private Model<UserData, CityData> prevModelState;
	private long previousTime = System.currentTimeMillis();
	private long timeStep = 500000000;
	private int turn = 0;
	private int aiMoveObserveWaitTime = 5;

	private AI ai;
	
	private boolean takeScreen = false;


	public GameLoop(Model<UserData, CityData> model, View<UserData> view, AI ai) {
		this.modelData = new ArrayObservableList<CityData>();
		this.modelData.addListener(new ListChangeListener<CityData>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends CityData> c) {
				while (c.next()) {
					for (CityData additem : c.getAddedSubList()) {
						Map<CityProperty, Series<Number, Number>> dataMap = view.getCityChartData();
						Map<CityProperty, Double> data = additem.getDataMap();
						for (Entry<CityProperty, Series<Number, Number>> pair : dataMap.entrySet()) {
							Double value = data.get(pair.getKey());
							if (value != null) {
								pair.getValue().getData().add(new Data<Number, Number>(turn, value));
							}
						}
					}
				}
			}
		});


		this.model = model;
		this.view = view;
		view.attachObserver(new ViewListener(this));
		model.attachObserver(new ModelListener(this));
		this.ai = ai;
		this.prevModelState = ModelStripper.reducedCopy(model);

	}

	@Override
	public void handle(long now) {
		if (!currentSelection.isStepMode() && now - previousTime > timeStep) {
			System.out.println(now - previousTime);
			step = true;
			previousTime = now;

		}

		if (step) {
			step = false;
			turn++;
			aiObserveCounter++;
			if(aiMode!=AiMode.OFF && aiMoveObserveWaitTime<aiObserveCounter){
				ai.setState(model);
				prevModelState = ModelStripper.reducedCopy(model);
				UserData nextAction = this.currentSelection;
				if(aiMode!=AiMode.OBSERVE){
					nextAction = ai.takeNextAction();
				}
				if (nextAction != null &&aiActionPrev != null) {
					ai.addCase(model, prevModelState, aiActionPrev, view.getWeightVector());
				}
				view.updateAIMove(nextAction);
				if(aiMode==AiMode.ON){
					model.notifyNewData(nextAction);
				}
				aiActionPrev = nextAction;
				aiObserveCounter=0;
				takeScreen = true;
			}
			model.update();
			render();
			if(takeScreen){
				takeScreen=false;
				view.screenShot();
			}
		} else if(draw){
			render();
			draw=false;
		}

	}

	
	private void render(){
		double[] map = ai.getMapOfValues(model, currentSelection);
		double[] norm = new double[]{0,1};
		map = Util.mapValues(map, norm);
		model.setOverlay(map);
		view.renderView(model);
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
	public synchronized void notifyModelEvent(CityData data) {
		modelData.add(data);
		view.updateScore(Rules.score(model, view.getWeightVector()), turn);
	}

	@Override
	public synchronized void notifyViewEvent(UserData data) {
		if (data.isMakeMove()) {
			model.notifyNewData(data);
		}
		this.currentSelection = data.copy();
		step = data.isTakeStep();
		draw = data.isDrawFlag();
		aiMode = data.isAiMode();
	}

}
