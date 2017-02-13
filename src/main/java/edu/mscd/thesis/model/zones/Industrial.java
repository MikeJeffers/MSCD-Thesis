package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.Factory;

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
