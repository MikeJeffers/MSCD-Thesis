package edu.mscd.thesis.model;

public class ZoneFactoryImpl implements ZoneFactory {

	@Override
	public Zone createZone(ZoneType zType, Pos2D pos) {
		if (zType == null) {
			return null;
		}
		Zone z = new EmptyZone(pos);
		switch (zType) {
		case RESIDENTIAL:
			z = new Residential(pos);
			break;
		case COMMERICAL:
			z = new Commercial(pos);
			break;
		case INDUSTRIAL:
			z = new Industrial(pos);
			break;
		case EMPTY:
			z = new EmptyZone(pos);
			break;
		default:
			z = new EmptyZone(pos);
			break;
		}
		return z;
	}

}
