package edu.mscd.thesis.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
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
	private boolean aiMode = true;
	private int aiObserveCounter;
	private UserData aiActionPrev;
	private Model<UserData, CityData> prevModelState;
	private long previousTime = System.currentTimeMillis();
	private long timeStep = 500000000;
	private int turn = 0;
	private int aiMoveObserveWaitTime = 10;

	private AI ai;
	private boolean isAIon = true;

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
			turn++;
			aiObserveCounter++;
			step = false;
			model.update();
			render();
			ai.setState(model);
			if (ai != null && aiMode && aiObserveCounter > aiMoveObserveWaitTime) {
				UserData nextAction = ai.takeNextAction();
				if (nextAction != null) {
					if (aiActionPrev != null) {
						ai.addCase(model, prevModelState, aiActionPrev, 0.5);
					}
					if (!nextAction.equals(aiActionPrev)) {
						this.makeAIMove(nextAction);
						aiObserveCounter = 0;
					} else {
						System.out.println("AI repeat move ignored");
					}
					aiActionPrev = nextAction;
				}
			}
			view.screenShot();
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

	private void makeAIMove(UserData action) {
		prevModelState = ModelStripper.reducedCopy(model);
		model.notifyNewData(action);
		view.updateAIMove(action);
	}

	@Override
	public synchronized void notifyModelEvent(CityData data) {
		modelData.add(data);
		view.updateScore(Rules.score(model, view.getWeightVector()));
	}

	@Override
	public synchronized void notifyViewEvent(UserData data) {
		if (data.isMakeMove()) {
			model.notifyNewData(data);
		}
		this.currentSelection = data.copy();
		step = data.isTakeStep();
		draw = data.isDrawFlag();
	}

}
