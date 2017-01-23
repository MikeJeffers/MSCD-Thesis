package edu.mscd.thesis.model;

public interface Tile {
	Pos2D getPos();
	Zone getZone();
	boolean setZone(ZoneType ztype);
	boolean isZonable();
	boolean isPassable();
	int materialValue();
	int baseLandValue();
	int maxDensity();

}
