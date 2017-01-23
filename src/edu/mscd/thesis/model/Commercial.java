package edu.mscd.thesis.model;

public class Commercial extends AbstractZone{

	public Commercial(Pos2D pos) {
		super(pos);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.COMMERICAL;
	}

}
