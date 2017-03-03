package edu.mscd.thesis.model.zones;

import java.io.Serializable;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;

public interface ZoneFactory extends Serializable{
	public Zone createZone(ZoneType zType, Pos2D pos, Tile tile);

}
