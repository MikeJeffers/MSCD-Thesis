package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;

public abstract class AbstractAction implements Action {
	private Pos2D location;
	private ZoneType zone;
	private int radius;
	private boolean isSquare;
	private boolean isMove;

	public AbstractAction() {
		initDefault();
	}

	private void initDefault() {
		this.location = new Pos2D(-1,-1);
		this.zone = ZoneType.EMPTY;
		this.radius = 1;
		this.isSquare =false;
		this.isMove = false;
	}

	@Override
	public Pos2D getTarget() {
		return location;
	}

	@Override
	public ZoneType getZoneType() {
		return zone;
	}

	@Override
	public int getRadius() {
		return radius;
	}

	@Override
	public boolean isSquare() {
		return this.isSquare;
	}

	@Override
	public boolean isMove() {
		return isMove;
	}
	

	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}

	public void setTarget(Pos2D loc) {
		this.location = loc.copy();
	}


	public void setZoneType(ZoneType zone) {
		this.zone = zone;

	}


	public void setRadius(int radius) {
		this.radius = radius;

	}


	public void setSquare(boolean isSquare) {
		this.isSquare = isSquare;

	}

	@Override
	public boolean isAction() {
		return true;
	}

	@Override
	public Action getAction() {
		return this;
	}

	@Override
	public boolean isConfig() {
		return false;
	}

	@Override
	public ConfigData getConfig() {
		// TODO throw error
		return null;
	}
	
	@Override
	public String getLabelText(){
		StringBuilder sb = new StringBuilder();
		sb.append("Target:");
		sb.append(this.getTarget());
		sb.append(", ");
		sb.append("Zone:");
		sb.append(this.getZoneType());
		sb.append(", ");
		sb.append("Radius");
		sb.append(this.getRadius());
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Action){
			Action o = (Action) other;
			return o.getZoneType()==this.getZoneType() && o.getRadius()==this.getRadius() && o.getTarget().equals(this.getTarget()) && o.isSquare()==this.isSquare();
		}
		return false;
	}
	
	@Override
	public String toString(){
		return this.getLabelText();
	}


	
	






}
