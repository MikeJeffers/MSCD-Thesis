package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneFactory;
import edu.mscd.thesis.model.zones.ZoneType;


public class TileImpl implements Tile {
	private Pos2D pos;
	private TileType type;
	private Zone zoning;
	private ZoneFactory factory;


	public TileImpl(Pos2D pos, TileType type, ZoneFactory factory) {
		this.pos = pos;
		this.type = type;
		this.factory = factory;
		this.zoning = this.factory.createZone(ZoneType.EMPTY, pos, this);
	}
	
	@Override
	public void update(){
		if(zoning!=null){
			zoning.update();
		}
		
	}
	
	@Override
	public TileType getType() {
		return this.type;
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
	public double materialValue() {
		return this.type.getBaseLandValue();
	}

	@Override
	public double baseLandValue() {
		return this.type.getBaseLandValue();
	}

	@Override
	public Density maxDensity() {
		return this.type.getMaxDensity();
	}

	@Override
	public Zone getZone() {
		return this.zoning;
	}

	@Override
	public boolean setZone(ZoneType zType) {
		if (this.isZonable() && !zType.equals(this.zoning.getZoneType())) {
			this.zoning.clear();
			this.zoning = this.factory.createZone(zType, this.pos, this);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Tile{at=" + this.getPos() + ", type=" + this.type.toString() + ", zone=" + this.getZone().toString() + "}";
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof Tile){
			Tile o = (Tile) other;
			return o.getPos().equals(this.getPos())&&o.getZone().equals(this.getZone())&&this.getType()==o.getType();
		}
		return false;
	}

}
