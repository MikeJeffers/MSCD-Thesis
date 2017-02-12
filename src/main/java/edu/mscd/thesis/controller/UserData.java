package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;

public class UserData {
	private int radius;
	private boolean isSquare;
	private ZoneType zoneSelection;
	private boolean isStepMode;
	private Pos2D clickLocation;

	public UserData() {
		setDefault();
	}

	public void setDefault() {
		setSquare(false);
		setRadius(1);
		setClickLocation(new Pos2D(0,0));
		setStepMode(true);
		setZoneSelection(ZoneType.EMPTY);
	}

	public boolean isSquare() {
		return isSquare;
	}

	public void setSquare(boolean isSquare) {
		this.isSquare = isSquare;

	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;

	}

	public ZoneType getZoneSelection() {
		return zoneSelection;
	}

	public void setZoneSelection(ZoneType zoneSelection) {
		this.zoneSelection = zoneSelection;

	}


	public boolean isStepMode() {
		return isStepMode;
	}

	public void setStepMode(boolean isStepMode) {
		this.isStepMode = isStepMode;
	}

	public Pos2D getClickLocation() {
		return clickLocation;
	}

	public void setClickLocation(Pos2D clickLocation) {
		this.clickLocation = clickLocation;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Click at:");
		sb.append(clickLocation);
		sb.append(" Zonetype:");
		sb.append(zoneSelection);
		sb.append(" Square?:");
		sb.append(isSquare);
		sb.append(" Radius=");
		sb.append(radius);
		sb.append(" StepMode?:");
		sb.append(isStepMode);
		sb.append("");
		return sb.toString();
	}

}
