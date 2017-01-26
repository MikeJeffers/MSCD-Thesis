package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;

public interface Zone {
	Pos2D getPos();

	ZoneType getZoneType();

	void draw(GraphicsContext g, double scale);
	void update();
}
