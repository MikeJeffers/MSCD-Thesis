package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneFactory;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;


public class TileImpl implements Tile {
	private Pos2D pos;
	private TileType type;
	private Zone zoning;
	private ZoneFactory factory;
	private double landValue;
	private double pollution;


	public TileImpl(Pos2D pos, TileType type, ZoneFactory factory) {
		this.pos = pos;
		this.type = type;
		this.factory = factory;
		this.zoning = this.factory.createZone(ZoneType.EMPTY, pos, this);
		this.landValue = this.baseLandValue();
		this.pollution = 0;
	}
	
	@Override
	public void update(){
		if(zoning!=null){
			zoning.update();
		}
		this.pollution = pollutionDecay(this.pollution);
		this.landValue = landValueDecay(this.landValue);
		
		
	}
	
	private double pollutionDecay(double pollution){
		double value = pollution-(pollution/(2*Rules.POLLUTION_HALFLIFE));
		return Util.boundValue(value, 0, Rules.MAX);
		
	}
	
	private double landValueDecay(double currentValue){
		double value = currentValue - (currentValue/(Rules.LANDVALUE_DECAY));
		return Util.boundValue(value, this.baseLandValue(), Rules.MAX);
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
		StringBuilder sb = new StringBuilder();
		sb.append("Tile{");
		sb.append(this.type.toString());
		sb.append(" zonetype:");
		sb.append(this.getZone().getZoneType());
		sb.append(" MaterialValue:");
		sb.append(this.materialValue());
		sb.append(" LandValue:");
		sb.append(this.landValue);
		sb.append(" pollution:");
		sb.append(this.pollution);
		sb.append(" at:");
		sb.append(this.getPos());
		sb.append(" }");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof Tile){
			Tile o = (Tile) other;
			return o.getPos().equals(this.getPos())&&o.getZone().equals(this.getZone())&&this.getType()==o.getType();
		}
		return false;
	}

	@Override
	public double getPollution() {
		return this.pollution;
	}

	@Override
	public void pollute(double pollution) {
		this.pollution+=pollution;
		Util.boundValue(pollution, 0, Rules.MAX);
		
	}

	@Override
	public double getCurrentLandValue() {
		return this.landValue;
	}

	@Override
	public void modifyLandValue(double factor) {
		this.landValue+=factor;
		Util.boundValue(landValue, this.baseLandValue(), Rules.MAX);
		
	}

}
