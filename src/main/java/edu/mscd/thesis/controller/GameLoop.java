package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.nn.RandomBenchmark;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.view.View;
import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer implements Controller {

	private Model model;
	private View view;

	private boolean step = true;
	private boolean draw = true;

	private long previousTime = System.currentTimeMillis();

	private int turn = 0;

	private Action mostRecentlyAppliedAction = new UserAction();
	private Action prevUserAct = new UserAction();

	private AiConfig aiConfig = new AiConfigImpl();
	private GameConfig gameConfig = new GameConfigImpl();

	private AI ai;

	private boolean takeScreen = false;

	public GameLoop(Model model, View view, AI ai) {
		this.model = model;
		this.view = view;
		view.attachObserver(new ViewListener(this));
		model.attachObserver(new ModelListener(this));
		ai.attachObserver(new ViewListener(this));
		this.ai = ai;
	}

	@Override
	public void handle(long now) {

		if (!gameConfig.isPaused() && now - previousTime > gameConfig.getSpeed() * Util.MAX_FRAME_DURATION) {
			System.out.println(now - previousTime);
			step = true;
			previousTime = now;
		}

		if (step) {
			step = false;
			turn();
		} else if (draw) {
			draw = false;
			render();
			if (takeScreen) {
				takeScreen = false;
				view.screenShot();
			}
		}

	}

	private void turn() {
		turn++;
		ai.getLock().lock();
		try {
			ai.update(model, mostRecentlyAppliedAction, view.getWeightVector());
		} finally {
			ai.getLock().unlock();
		}
		model.getLock().lock();
		try {
			model.update();
		} finally {
			model.getLock().unlock();
		}
		draw = true;
	}

	private void getCurrentQValueMap(Action action) {
		ai.getLock().lock();
		double[] map = new double[Rules.TILE_COUNT];
		try {
			map = ai.getMapOfValues(model, action);
		} finally {
			ai.getLock().unlock();
		}
		double[] norm = new double[] { 0, 1 };
		map = Util.mapValues(map, norm);
		model.setOverlay(map);
		takeScreen = true;
	}

	private void render() {
		model.getLock().lock();
		try {
			view.renderView(model);
		} finally {
			model.getLock().unlock();
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
	public void notifyModelEvent(ModelData data) {
		view.updateCityData(data, turn);
		double score = Rules.score(model, view.getWeightVector());
		view.updateScore(score, turn);
		reportScore(score, turn);
	}

	@Override
	public void notifyViewEvent(ViewData data) {
		AiMode mode = gameConfig.getAiMode();
		if (data.isAction()) {
			Action a = data.getAction().copy();
			if (mode != AiMode.OFF && a.isAI()) {
				view.updateAIMove(a);
				Action next = a;
				if (mode == AiMode.ON || mode == AiMode.ON_FOLLOW) {
					AiAction act = (AiAction) next;
					act.setMove(true);
					System.out.println("Displaying Q-Map for AI move");
					getCurrentQValueMap(a);
				}
			} else if (!a.isAI()) {
				if (!a.isMove()) {
					view.setTileToolTip(model.getWorld().getTileAt(a.getTarget()).getLabelText());
				} else {
					if (gameConfig.isPaused()) {
						step = true;
					}
					if (mode == AiMode.ASSIST_FOLLOW || mode == AiMode.ON_FOLLOW) {
						ai.forceUpdate();
					}
				}
				if (prevUserAct.getZoneType() != a.getZoneType()) {
					System.out.println("Displaying Q-Map for user move or selection");
					getCurrentQValueMap(a);
				}
				prevUserAct = a;
			}
			if (a.isMove()) {
				mostRecentlyAppliedAction = a;
			}
			if (!a.isAI() || !(mode == AiMode.OFF)) {
				model.notifyNewData(a);
				this.draw = true;
			}
		} else if (data.isConfig()) {
			ConfigData config = data.getConfig();
			if (config.isAiConfig()) {
				this.aiConfig = (AiConfig) config.getAiConfig().copy();
				this.ai.configure(aiConfig);
				reportNewConfig(aiConfig, this.turn);
			} else if (config.isGameConfig()) {
				this.gameConfig = (GameConfig) config.getGameConfiguration().copy();
				this.step = gameConfig.isStep() && gameConfig.isPaused();
			}
		}
	}

	private void reportNewConfig(AiConfig conf, int turnCount) {
		if(Util.REPORT){
			StringBuilder sb = new StringBuilder("Turn:");
			sb.append(turnCount);
			if(ai instanceof RandomBenchmark){
				sb.append("--RANDOM--");
			}else{
				sb.append("--NewConfig--");
				sb.append(conf.toString());
			}
			sb.append("\n--Weights--");
			sb.append(view.getWeightVector().toString());
			Util.report(sb.toString());
			
		}
	}
	
	private void reportScore(double score, int turn){
		if(Util.REPORT){
			Util.report("Turn:" + turn + "; Score=" + score);
		}
	}

}
