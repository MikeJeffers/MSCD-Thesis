package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;
import javafx.scene.canvas.GraphicsContext;

public interface World extends Model{
	
	boolean setZoneAt(Pos2D pos, ZoneType zt);
	boolean setAllZonesAround(Pos2D pos, ZoneType zt, int radius, boolean squareSelect);

	Tile getTileAt(Pos2D pos);

	Zone getZoneAt(Pos2D pos);
	
	City getCity();
	
	Tile[] getTiles();
	
}
