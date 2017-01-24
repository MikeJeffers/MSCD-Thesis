package edu.mscd.thesis.model;

import javafx.scene.canvas.GraphicsContext;

public class TileImpl implements Tile {
	private Pos2D pos;
	private TileType type;
	private Zone zoning;
	private ZoneFactory factory;

	public TileImpl(Pos2D pos, TileType type, ZoneFactory factory) {
		this.pos = pos;
		this.type = type;
		this.factory = factory;
		this.zoning = this.factory.createZone(ZoneType.EMPTY, pos);
	}

	@Override
	public Pos2D getPos() {
		return this.pos;
	}

	@Override
	public boolean isZonable() {
		return this.type.isZonable();
	}

	@Override
	public boolean isPassable() {
		return this.type.isPassable();
	}

	@Override
	public int materialValue() {
		return this.type.getBaseLandValue();
	}

	@Override
	public int baseLandValue() {
		return this.type.getBaseLandValue();
	}

	@Override
	public int maxDensity() {
		return this.type.getMaxDensity();
	}

	@Override
	public Zone getZone() {
		return this.zoning;
	}

	@Override
	public boolean setZone(ZoneType zType) {
		if (this.isZonable() && !zType.equals(this.zoning.getZoneType())) {
			this.zoning = this.factory.createZone(zType, this.pos);
			return true;
		}
		return false;
	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.setFill(this.type.getColor());
		g.fillRect(this.pos.getX() * scale, this.getPos().getY() * scale, scale, scale);
		if (this.zoning != null) {
			this.zoning.draw(g, scale);
		}
	}

	@Override
	public String toString() {
		return "Tile{at=" + this.getPos() + ", type=" + this.type.toString() + ", zone=" + this.getZone() + "}";
	}

}
