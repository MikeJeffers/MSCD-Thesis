package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.Factory;
import edu.mscd.thesis.model.bldgs.House;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Industrial extends AbstractZone {

	public Industrial(Pos2D pos, Tile tile) {
		super(pos, tile);
		super.setBuilding(new Factory(pos, tile.getType(), getZoneType(), Density.NONE));
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.INDUSTRIAL;
	}



}
