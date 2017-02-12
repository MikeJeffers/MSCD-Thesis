package edu.mscd.thesis.view;

import javafx.scene.canvas.GraphicsContext;

public interface Renderer<T> {
	
	public void draw(T model, GraphicsContext g);

}
