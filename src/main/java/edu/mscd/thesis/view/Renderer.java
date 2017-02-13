package edu.mscd.thesis.view;

import javafx.scene.canvas.GraphicsContext;

/**
 * Renderer interface separates drawing logic from model logic Requires:
 * Model!=null AND GraphicsContext!=null (minimize null checks on draw loop)
 * 
 * @author Mike
 *
 * @param <T>
 *            Type of Model object to be drawn
 */
public interface Renderer<T> {

	/**
	 * Draw method, draws model data on provided context
	 * 
	 * @param model
	 *            - model to draw
	 * @param g
	 *            - JavaFX graphics context, Canvas
	 */
	public void draw(T model, GraphicsContext g);

}
