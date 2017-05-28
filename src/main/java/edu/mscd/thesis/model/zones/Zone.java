package edu.mscd.thesis.model.zones;


import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.tiles.Tile;

public interface Zone{
	Pos2D getPos();

	Tile getTile();

	void deltaValue(double v);

	double getValue();

	ZoneType getZoneType();

	Building getBuilding();

	void update();

	void clear();

	String getLabelText();
}
