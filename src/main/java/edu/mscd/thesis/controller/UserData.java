package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.zones.ZoneType;

public class UserData {
	private int radius;
	private boolean isSquare;
	private ZoneType zoneSelection;

	public UserData() {

	}

	public boolean isSquare() {
		return isSquare;
	}

	public UserData setSquare(boolean isSquare) {
		this.isSquare = isSquare;
		return this;
	}

	public int getRadius() {
		return radius;
	}

	public UserData setRadius(int radius) {
		this.radius = radius;
		return this;
	}

	public ZoneType getZoneSelection() {
		return zoneSelection;
	}

	public UserData setZoneSelection(ZoneType zoneSelection) {
		this.zoneSelection = zoneSelection;
		return this;
	}

}
