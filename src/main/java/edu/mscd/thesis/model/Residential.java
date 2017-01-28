package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Residential extends AbstractZone {

	public Residential(Pos2D pos, Tile tile) {
		super(pos, tile);
		House home = new House(super.getPos());
		System.out.println(home);
		boolean success = super.addBuilding(home);
		System.out.println("Add :"+home.toString()+" Success?:"+success);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.RESIDENTIAL;
	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.setFill(new Color(0, 1.0, 0, 0.5));
		super.draw(g, scale);
		
	}

	@Override
	public String toString() {
		return "Zone{pos=" + getPos().toString() + ", type=" + this.getZoneType().toString() + "}";
	}
	
	@Override
	public void update(){
		//TODO add zone rule logic here to eval growth/decay of buildings in zone
		
	}

}
