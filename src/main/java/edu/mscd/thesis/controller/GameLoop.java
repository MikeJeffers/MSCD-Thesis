package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.World;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {
	private GraphicsContext graphics;
	private World world;
	private boolean gameStep;
	private boolean stepMode;

	public GameLoop(World w, GraphicsContext gc) {
		this.graphics = gc;
		this.world = w;
	}

	@Override
	public void handle(long now) {
		
		world.draw(graphics);
		if(gameStep && stepMode){
			gameStep=false;
			world.update();
			
			
		}
	}
	
	public void setStepMode(boolean isStepMode){
		this.stepMode = isStepMode;
	}
	
	public void step(){
		this.gameStep = true;
	}

}
