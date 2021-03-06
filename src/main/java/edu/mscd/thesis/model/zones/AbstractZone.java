package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.tiles.Tile;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public abstract class AbstractZone implements Zone {
	private Pos2D pos;
	private Tile tile;
	private Building building;
	private double value;

	public AbstractZone(Pos2D pos, Tile tile) {
		this.pos = pos;
		this.tile = tile;
		this.value = Rules.getValueForZoneOnTile(tile.getType(), this.getZoneType());
	}
	
	@Override
	public void clear(){
		if(this.building==null){
			return;
		}
		this.building.clear();
	}

	@Override
	public void deltaValue(double v) {
		this.value += v;
		this.value = Util.boundValue(this.value, 0, Rules.MAX);
	}

	@Override
	public double getValue() {
		return this.value;
	}

	public void setValue(double v) {
		this.value = Util.boundValue(v, 0, Rules.MAX);
	}

	@Override
	public Pos2D getPos() {
		return this.pos.copy();
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("Zone{pos=");
		sb.append("Zone{type=");
		sb.append(getZoneType());
		sb.append(" Value=");
		sb.append(this.getValue());
		sb.append(" pos=");
		sb.append(getPos());
		sb.append(this.getBuilding());
		return sb.toString();
	}

	@Override
	public void update() {
		double deltaValue = Rules.getGrowthValue(this.getTile(), this.getZoneType());
		this.deltaValue(deltaValue);
		if(this.building!=null){
			this.setValue(this.building.update(this.getValue()));
		}
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
	
	@Override
	public String getLabelText(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getZoneType());
		sb.append("\nGrowthValue: ");
		sb.append(Util.truncateValueToDisplay(this.getValue(), 6));
		if(this.getBuilding()!=null){
			sb.append("\n--------");
			sb.append(this.getBuilding().getLabelText());
		}
		return sb.toString();
	}

}
