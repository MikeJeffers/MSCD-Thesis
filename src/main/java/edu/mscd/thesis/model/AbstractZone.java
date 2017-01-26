package edu.mscd.thesis.model;

import java.util.Collection;
import java.util.HashSet;

import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractZone implements Zone {
	private Pos2D pos;
	private Tile tile;
	private Collection<Building> buildings;

	public AbstractZone(Pos2D pos, Tile tile) {
		this.pos = pos;
		this.tile = tile;
		this.buildings = new HashSet<Building>();
	}

	@Override
	public Pos2D getPos() {
		return this.pos.copy();
	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.fillRect(this.pos.getX(), this.getPos().getY(), scale, scale);
		if (buildings != null && !buildings.isEmpty()) {
			for (Building b : buildings) {
				if (b != null && b.getImage()!=null && b.getPos()!=null) {
					g.drawImage(b.getImage(), b.getPos().getX(), b.getPos().getY(), scale*5, scale*5);
				}

			}
		}

	}

	@Override
	public String toString() {
		return "Zone{pos=" + pos.toString() + ", type=abstract}";
	}

	@Override
	public void update() {

	}

	public Collection<Building> getBuildings() {
		return this.buildings;
	}

	public Tile getTile() {
		return tile;
	}

}
