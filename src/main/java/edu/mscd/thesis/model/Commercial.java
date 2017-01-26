package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Commercial extends AbstractZone{

	public Commercial(Pos2D pos, Tile tile) {
		super(pos, tile);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.COMMERICAL;
	}
	
	@Override 
	public void draw(GraphicsContext g, double scale){
		g.setFill(new Color(0, 0, 1.0, 0.5));
		super.draw(g, scale);
	}
	
	@Override
	public String toString() {
		return "Zone{pos=" + getPos().toString() + ", type=" + this.getZoneType().toString() + "}";
	}
	
	@Override
	public void update(){
		//TODO add zone rule logic here to eval growth/decay of buildings in zone
		//super.getBuildings().add(new House(super.getPos(), super.getTile(), ZoneType.RESIDENTIAL));
	}

}
