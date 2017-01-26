package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Industrial extends AbstractZone {

	public Industrial(Pos2D pos) {
		super(pos);
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.INDUSTRIAL;
	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.setFill(new Color(1.0, 1.0, 0, 0.5));
		super.draw(g, scale);
	}

	@Override
	public String toString() {
		return "Zone{pos=" + getPos().toString() + ", type=" + this.getZoneType().toString() + "}";
	}
}
