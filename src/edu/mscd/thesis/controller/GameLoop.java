package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.World;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {
	private GraphicsContext graphics;
	private World world;
	private double scale;

	public GameLoop(World w, GraphicsContext gc, double scaleFactor) {
		this.graphics = gc;
		this.world = w;
		this.scale = scaleFactor;
	}

	@Override
	public void handle(long now) {
		world.update();
		world.draw(graphics, scale);

	}

}
