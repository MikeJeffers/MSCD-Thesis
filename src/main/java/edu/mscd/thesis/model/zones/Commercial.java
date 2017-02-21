package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.Shop;

public class Commercial extends AbstractZone {

	public Commercial(Pos2D pos, Tile tile) {
		super(pos, tile);
		super.setBuilding(new Shop(pos, tile.getType(), getZoneType(), Density.NONE));
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.COMMERICAL;
	}

}
