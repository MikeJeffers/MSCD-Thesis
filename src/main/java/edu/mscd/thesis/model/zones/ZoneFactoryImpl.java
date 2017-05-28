package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.tiles.Tile;

public class ZoneFactoryImpl implements ZoneFactory {

	@Override
	public Zone createZone(ZoneType zType, Pos2D pos, Tile tile) {
		if (zType == null) {
			return null;
		}
		Zone z = new EmptyZone(pos, tile);
		if(!tile.isZonable()){
			return z;
		}
		switch (zType) {
		case RESIDENTIAL:
			z = new Residential(pos, tile);
			break;
		case COMMERICAL:
			z = new Commercial(pos, tile);
			break;
		case INDUSTRIAL:
			z = new Industrial(pos, tile);
			break;
		case EMPTY:
			z = new EmptyZone(pos, tile);
			break;
		default:
			z = new EmptyZone(pos, tile);
			break;
		}
		return z;
	}

}
