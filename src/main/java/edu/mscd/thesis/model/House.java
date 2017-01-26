package edu.mscd.thesis.model;

import java.util.Collection;
import java.util.HashSet;

public class House extends AbstractBuilding {
	private Collection<Person> occupants;
	private Tile tile;
	private Zone zone;
	private Pos2D pos;

	public House(Pos2D site, Tile land, Zone zoning, String imageFile) {
		this.pos = site;
		this.tile = land;
		this.zone = zoning;
		this.occupants = new HashSet<Person>();
		super.setImage(imageFile);
	}

	@Override
	public Collection<Person> getOccupants() {
		// TODO Auto-generated method stub
		return occupants;
	}

}
