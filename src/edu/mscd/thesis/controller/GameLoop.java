package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.World;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer{
	private GraphicsContext graphics;
	private World world;
	
	public GameLoop(World w, GraphicsContext gc){
		this.graphics = gc;
		this.world = w;
	}

	@Override
	public void handle(long now) {

		world.update();
		world.draw(graphics);
		
	}

}
