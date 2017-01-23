package edu.mscd.thesis.model;

public class Residential extends AbstractZone{

	public Residential(Pos2D pos) {
		super(pos);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.RESIDENTIAL;
	}

}
