package edu.mscd.thesis.model.zones;


import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.tiles.Tile;

public interface ZoneFactory{
	public Zone createZone(ZoneType zType, Pos2D pos, Tile tile);

}
