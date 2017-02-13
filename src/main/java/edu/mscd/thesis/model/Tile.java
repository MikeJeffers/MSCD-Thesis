package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;


public interface Tile {
	Pos2D getPos();

	Zone getZone();

	boolean setZone(ZoneType ztype);

	boolean isZonable();

	boolean isPassable();

	double materialValue();

	double baseLandValue();

	Density maxDensity();
	
	void update();
	
	TileType getType();

}
