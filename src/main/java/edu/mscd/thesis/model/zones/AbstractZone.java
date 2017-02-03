package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractZone implements Zone {
	private Pos2D pos;
	private Tile tile;
	private Building building;
	private double value;

	public AbstractZone(Pos2D pos, Tile tile) {
		this.pos = pos;
		this.tile = tile;
	}

	@Override
	public void deltaValue(double v) {
		this.value += v;
		Math.max(this.value, Util.MAX);
	}

	@Override
	public double getValue() {
		return this.value;
	}

	public void setValue(double v) {
		this.value = v;
		Math.max(this.value, Util.MAX);
	}

	@Override
	public Pos2D getPos() {
		return this.pos.copy();
	}

	@Override
	public void draw(GraphicsContext g) {
		g.fillRect(this.getPos().getX(), this.getPos().getY(), 1, 1);
		if (building != null) {
			building.render(g);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("Zone{pos=");
		sb.append(getPos());
		sb.append(", type=");
		sb.append(getZoneType());
		sb.append(this.getBuilding());
		return sb.toString();
	}

	@Override
	public void update() {

	}

	@Override
	public Building getBuilding() {
		return this.building;
	}

	public void setBuilding(Building newBuilding) {
		this.building = newBuilding;
	}

	@Override
	public Tile getTile() {
		return tile;
	}

}
