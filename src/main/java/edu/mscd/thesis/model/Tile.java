package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;

public interface Tile {
	Pos2D getPos();

	Zone getZone();

	boolean setZone(ZoneType ztype);

	boolean isZonable();

	boolean isPassable();

	int materialValue();

	int baseLandValue();

	int maxDensity();

	void draw(GraphicsContext g);
	
	void setMouseOver(boolean over);
	
	void update();

}
