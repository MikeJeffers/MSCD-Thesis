package edu.mscd.thesis.controller;

import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
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

	private AI ai;

	public GameLoop(Model<UserData, CityData> model, View<UserData> view, AI ai) {
		this.modelData = new ArrayObservableList<CityData>();

		this.modelData.addListener(new ListChangeListener<CityData>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends CityData> c) {
				while (c.next()) {
					for (CityData additem : c.getAddedSubList()) {
						Map<CityProperty, Series<Number, Number>> dataMap = view.getDataStreams();
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
			view.renderView(model);
			ai.setState(model);
			if (ai != null && aiMode && aiObserveCounter > 5) {
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

	private void makeAIMove(UserData action) {
		prevModelState = ModelStripper.reducedCopy(model);
		model.notifyNewData(action);
	}

	@Override
	public synchronized void notifyModelEvent(CityData data) {
		data.setProperty(CityProperty.SCORE, (Rules.score(model)));
		modelData.add(data);
	}

	@Override
	public synchronized void notifyViewEvent(UserData data) {
		Pos2D old = this.currentSelection.getClickLocation();
		Pos2D newClick = data.getClickLocation();
		// TODO this is messy, only update model on new canvas click?
		if (!old.equals(newClick)) {
			model.notifyNewData(data);
		}
		this.currentSelection = data.copy();
		step = data.isTakeStep();
		draw = data.isDrawFlag();
	}

}
