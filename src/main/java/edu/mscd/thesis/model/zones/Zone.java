package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.Building;

public interface Zone {
	Pos2D getPos();

	Tile getTile();

	void deltaValue(double v);

	double getValue();

	ZoneType getZoneType();

	Building getBuilding();

	void update();

	void clear();
}
