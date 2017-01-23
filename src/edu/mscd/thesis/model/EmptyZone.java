package edu.mscd.thesis.model;

public class EmptyZone extends AbstractZone{

	public EmptyZone(Pos2D pos) {
		super(pos);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.EMPTY;
	}

}
