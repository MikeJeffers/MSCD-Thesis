package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;

/**
 * Data packet class to encapsulate all possible user actions and communicate them to one listener as one type
 * @author Mike
 */
public class UserData {
	private int radius;
	private boolean isSquare;
	private ZoneType zoneSelection;
	private boolean isStepMode;
	private boolean takeStep;
	private Pos2D clickLocation;
	private boolean drawFlag;

	public UserData() {
		setDefault();
	}

	//TODO Find better way to set common defaults across view and controller on init
	public void setDefault() {
		setSquare(false);
		setRadius(1);
		setClickLocation(new Pos2D(0,0));
		setStepMode(true);
		setZoneSelection(ZoneType.EMPTY);
		setDrawFlag(true);
		setTakeStep(true);
	}

	/**
	 * Is the radius a Manhattan Distance or radial-distance?
	 * @return true if manhattan (square)
	 */
	public boolean isSquare() {
		return isSquare;
	}

	public void setSquare(boolean isSquare) {
		this.isSquare = isSquare;

	}

	/**
	 * Radius of zoning
	 * @return radius of effect
	 */
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;

	}

	/**
	 * ZoneType to set on tiles in radius
	 * @return ZoneType
	 */
	public ZoneType getZoneSelection() {
		return zoneSelection;
	}

	public void setZoneSelection(ZoneType zoneSelection) {
		this.zoneSelection = zoneSelection;

	}

	/**
	 * Is the game Paused, set to stepMode?
	 * @return True if game in StepMode
	 */
	public boolean isStepMode() {
		return isStepMode;
	}

	public void setStepMode(boolean isStepMode) {
		this.isStepMode = isStepMode;
	}

	/**
	 * Location of user MousePress on Canvas
	 * @return {@link Pos2D} location in model-scale
	 */
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
		sb.append(" TakeTurn?:");
		sb.append(takeStep);
		sb.append(" ForceDraw?:");
		sb.append(drawFlag);
		sb.append("");
		return sb.toString();
	}
	
	public UserData copy(){
		UserData data = new UserData();
		data.setSquare(this.isSquare);
		data.setRadius(this.radius);
		data.setClickLocation(this.clickLocation.copy());
		data.setStepMode(this.isStepMode);
		data.setZoneSelection(this.zoneSelection);
		data.setDrawFlag(this.drawFlag);
		data.setTakeStep(this.takeStep);
		return data;
		
	}

	public boolean isDrawFlag() {
		return drawFlag;
	}

	public void setDrawFlag(boolean drawFlag) {
		this.drawFlag = drawFlag;
	}

	public boolean isTakeStep() {
		return takeStep;
	}

	public void setTakeStep(boolean takeStep) {
		this.takeStep = takeStep;
	}

}