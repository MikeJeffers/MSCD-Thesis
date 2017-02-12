package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EmptyZone extends AbstractZone {

	public EmptyZone(Pos2D pos, Tile tile) {
		super(pos, tile);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.EMPTY;
	}


	@Override
	public String toString() {
		return "Zone{pos=" + getPos().toString() + ", type=" + this.getZoneType().toString() + "}";
	}

}
