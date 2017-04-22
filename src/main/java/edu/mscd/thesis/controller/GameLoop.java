package edu.mscd.thesis.controller;

import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.model.Model;
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

	private ObservableList<ModelData> modelData;
	private Model model;
	private View view;

	private boolean step = true;
	private boolean draw = true;

	private Model prevModelState;
	private long previousTime = System.currentTimeMillis();
	private long timeStep = 1000000000;
	private int turn = 0;

	private Action currentAiMove;
	private Action previousAiMove;

	private Action currentUserMove = new UserAction();
	private Action mostRecentlyAppliedAction = new UserAction();

	private AiConfig aiConfig = new AiConfigImpl();
	private GameConfig gameConfig = new GameConfigImpl();

	private AI ai;

	private boolean takeScreen = false;

	public GameLoop(Model model, View view, AI ai) {
		this.modelData = new ArrayObservableList<ModelData>();
		this.modelData.addListener(new ListChangeListener<ModelData>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends ModelData> c) {
				while (c.next()) {
					for (ModelData additem : c.getAddedSubList()) {
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
		ai.attachObserver(new ViewListener(this));
		this.ai = ai;
		this.prevModelState = ModelStripper.reducedCopy(model);

	}

	@Override
	public void handle(long now) {

		if (!gameConfig.isPaused() && now - previousTime > gameConfig.getSpeed() * timeStep) {
			System.out.println(now - previousTime);
			step = true;
			previousTime = now;

		}

		if (step) {
			step = false;
			turn++;
			ai.update(model, mostRecentlyAppliedAction, view.getWeightVector());
			model.update();
			render();
			if (takeScreen) {
				takeScreen = false;
				view.screenShot();
			}
		} else if (draw) {
			render();
			draw = false;
		}

	}

	private void getCurrentQValueMap(Action action) {
		double[] map = ai.getMapOfValues(model, action);
		double[] norm = new double[] { 0, 1 };
		map = Util.mapValues(map, norm);
		model.setOverlay(map);
	}

	private void render() {
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
	public void notifyModelEvent(ModelData data) {
		modelData.add(data);
		view.updateScore(Rules.score(model, view.getWeightVector()), turn);
	}

	@Override
	public void notifyViewEvent(ViewData data) {
		System.out.println(data);
		if (data.isAction()) {
			Action a = data.getAction().copy();
			
			if(gameConfig.getAiMode()!=AiMode.OFF && a.isAI()){
				getCurrentQValueMap(a);
				view.updateAIMove(a);
				Action next = a;
				if (gameConfig.getAiMode() == AiMode.ON) {
					AiAction act = (AiAction) next;
					act.setMove(true);
					next = act;
				}
				prevModelState = ModelStripper.reducedCopy(model);
				previousAiMove = next;
			}else if(!a.isAI()){
				currentUserMove = a;
			}
			if (a.isMove()) {
				mostRecentlyAppliedAction = a;
			}
			model.notifyNewData(a);
			this.draw = true;

		} else if (data.isConfig()) {
			ConfigData config = data.getConfig();
			if (config.isAiConfig()) {
				this.aiConfig = (AiConfig) config.getAiConfig().copy();
				this.ai.configure(aiConfig);
			} else if (config.isGameConfig()) {
				this.gameConfig = (GameConfig) config.getGameConfiguration().copy();
				this.step = gameConfig.isStep() && gameConfig.isPaused();
			}
		}

	}

}
