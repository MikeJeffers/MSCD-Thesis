package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EmptyZone extends AbstractZone {

	public EmptyZone(Pos2D pos) {
		super(pos);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.EMPTY;
	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.setFill(new Color(0.1, 0.1, 0.1, 0.1));
		super.draw(g, scale);
	}

	@Override
	public String toString() {
		return "Zone{pos=" + getPos().toString() + ", type=" + this.getZoneType().toString() + "}";
	}

}
