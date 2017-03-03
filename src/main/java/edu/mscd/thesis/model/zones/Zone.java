package edu.mscd.thesis.model.zones;

import java.io.Serializable;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.Building;

public interface Zone extends Serializable{
	Pos2D getPos();

	Tile getTile();

	void deltaValue(double v);

	double getValue();

	ZoneType getZoneType();

	Building getBuilding();

	void update();

	void clear();
}
