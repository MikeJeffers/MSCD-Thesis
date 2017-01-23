package edu.mscd.thesis.model;

public class Industrial extends AbstractZone{

	public Industrial(Pos2D pos) {
		super(pos);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.INDUSTRIAL;
	}
	

}
