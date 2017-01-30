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
	public void draw(GraphicsContext g) {
		g.fillRect(this.getPos().getX(), this.getPos().getY(), 1, 1);
		if (buildings != null && !buildings.isEmpty()) {
			for (Building b : buildings) {
				b.render(g);
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

	@Override
	public Collection<Building> getBuildings() {
		return this.buildings;
	}
	
	public boolean addBuilding(Building b){
		return this.buildings.add(b);
	}

	public Tile getTile() {
		return tile;
	}

}
