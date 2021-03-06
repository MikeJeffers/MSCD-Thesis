package edu.mscd.thesis.model.tiles;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;

public class TileReduced implements Tile {
	private Selection select;
	private double overlay;
	private double zoneValue;
	private Density zoneDensity;
	private ZoneType zoneType;
	private Pos2D pos;
	private TileType type;
	private double materialValue;
	private double landValue;
	private double originalLandValue;
	private double pollution;
	private Zone zone;
	private String labelText;

	public TileReduced(Tile t) {
		this.zoneValue = t.getZoneValue();
		this.zoneDensity = t.getZoneDensity();
		this.zoneType = t.getZoneType();
		this.pos = t.getPos().copy();
		this.type = t.getType();
		this.materialValue = t.materialValue();
		this.landValue = t.getCurrentLandValue();
		this.originalLandValue = t.baseLandValue();
		this.pollution = t.getPollution();
		this.overlay = t.getOverlayValue();
		this.select = t.getSelection();
		this.zone = t.getZone();
		this.labelText = t.getLabelText();
	}

	@Override
	public double getZoneValue() {
		return this.zoneValue;
	}

	@Override
	public Density getZoneDensity() {
		return this.zoneDensity;
	}

	@Override
	public ZoneType getZoneType() {
		return this.zoneType;
	}

	@Override
	public Pos2D getPos() {
		return this.pos.copy();
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
		return this.materialValue;
	}

	@Override
	public double baseLandValue() {
		return this.originalLandValue;
	}

	@Override
	public double getPollution() {
		return this.pollution;
	}

	@Override
	public double getCurrentLandValue() {
		return this.landValue;
	}

	@Override
	public Density maxDensity() {
		return this.type.getMaxDensity();
	}

	@Override
	public TileType getType() {
		return this.type;
	}
	
	@Override
	public boolean setZone(ZoneType ztype) {
		this.zoneType =ztype;
		return true;
	}

	@Override
	public void modifyLandValue(double factor) {
		// TODO should fail; does nothing
	}

	@Override
	public void update() {
		// TODO should fail; does nothing
	}

	@Override
	public void pollute(double pollution) {
		// TODO should fail; does nothing
	}

	@Override
	public Zone getZone() {
		return this.zone;
	}

	@Override
	public double getOverlayValue() {
		return this.overlay;
	}

	@Override
	public void setOverlayValue(double value) {
		// TODO should fail
	}

	@Override
	public Selection getSelection() {
		return this.select;
	}

	@Override
	public void setSelection(Selection select) {
		// TODO should fail
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Tile){
			Tile o = (Tile) other;
			return o.getPos().equals(this.getPos())&&this.getType()==o.getType();
		}
		return false;
	}

	@Override
	public String getLabelText() {
		return this.labelText;
	}


}
