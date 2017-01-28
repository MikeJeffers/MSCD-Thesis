package edu.mscd.thesis.model;

import java.util.Collection;

import javafx.scene.canvas.GraphicsContext;

public interface Zone {
	Pos2D getPos();

	ZoneType getZoneType();
	
	Collection<Building> getBuildings();

	void draw(GraphicsContext g, double scale);
	void update();
}
