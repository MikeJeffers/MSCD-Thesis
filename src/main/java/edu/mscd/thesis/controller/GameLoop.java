package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.nn.AI;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.view.View;
import javafx.animation.AnimationTimer;


public class GameLoop extends AnimationTimer implements Controller {


	private Model model;
	private View view;

	private boolean step = true;
	private boolean draw = true;

	private Model prevModelState;
	private long previousTime = System.currentTimeMillis();
	
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

		if (!gameConfig.isPaused() && now - previousTime > gameConfig.getSpeed() * Util.MAX_FRAME_DURATION) {
			System.out.println(now - previousTime);
			step = true;
			previousTime = now;
		}

		if (step) {
			step = false;
			turn();
			
		} else if (draw) {
			render();
			draw = false;
		}

	}
	
	private void turn(){
		turn++;
		ai.update(model, mostRecentlyAppliedAction, view.getWeightVector());
		model.update();
		render();
		if (takeScreen) {
			takeScreen = false;
			view.screenShot();
		}
		
	}

	private void getCurrentQValueMap(Action action) {
		double[] map = ai.getMapOfValues(model, action);
		double[] norm = new double[] { 0, 1 };
		map = Util.mapValues(map, norm);
		model.setOverlay(map);
		takeScreen = true;
	}

	private void render() {
		view.renderView(ModelStripper.reducedCopy(this.model));
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
		view.updateScore(Rules.score(model, view.getWeightVector()), turn);
	}

	@Override
	public void notifyViewEvent(ViewData data) {
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
			if(!a.isAI() || !(gameConfig.getAiMode()==AiMode.OFF)){
				model.notifyNewData(a);
				this.draw = true;
			}
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
