package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractZone implements Zone {
	private Pos2D pos;

	public AbstractZone(Pos2D pos) {
		this.pos = pos;
	}

	@Override
	public Pos2D getPos() {
		return this.pos.copy();
	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.fillRect(this.pos.getX() * scale, this.getPos().getY() * scale, scale, scale);
	}

	@Override
	public String toString() {
		return "Zone{pos=" + pos.toString() + ", type=abstract}";
	}

}
