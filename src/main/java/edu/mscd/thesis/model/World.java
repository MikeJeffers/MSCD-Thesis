package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;

public interface World {
	public void update();

	public void draw(GraphicsContext g);

	Tile getTileAt(Pos2D pos);

	Zone getZoneAt(Pos2D pos);
	
}
