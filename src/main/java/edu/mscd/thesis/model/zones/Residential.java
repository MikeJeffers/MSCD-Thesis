package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Residential extends AbstractZone {

	public Residential(Pos2D pos, Tile tile) {
		super(pos, tile);
		//House home = new House(super.getPos());
		//super.setBuilding(home);
		
		//Residential zone tile-bias
		double tileValues = (tile.baseLandValue()+(tile.materialValue()/2.0))/1.5;
		super.setValue(super.getValue()+tileValues);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.RESIDENTIAL;
	}

	@Override
	public void draw(GraphicsContext g) {
		g.setFill(new Color(0, 1.0, 0, 0.5));
		super.draw(g);
		
	}

	@Override
	public String toString() {
		return "Zone{pos=" + getPos().toString() + ", type=" + this.getZoneType().toString() + "}";
	}
	
	@Override
	public void update(){
		//TODO add zone rule logic here to eval growth/decay of buildings in zone
		
		if(super.getValue()>Util.GROWTH_THRESHOLD){
			
			super.setValue(super.getValue()-1);
		}
		
		
	}

}
