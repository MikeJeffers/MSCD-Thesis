package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;
import javafx.scene.canvas.GraphicsContext;

public interface Tile {
	Pos2D getPos();

	Zone getZone();

	boolean setZone(ZoneType ztype);

	boolean isZonable();

	boolean isPassable();

	double materialValue();

	double baseLandValue();

	double maxDensity();

	void draw(GraphicsContext g);
	
	void setMouseOver(boolean over);
	
	void update();

}
