package edu.mscd.thesis.model.bldgs;

import java.util.ArrayList;
import java.util.Collection;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;

public abstract class Home extends AbstractBuilding {

	public Home(Pos2D pos, TileType tileType, ZoneType zoneType, Density density) {
		super(pos, tileType, zoneType, density);
	}



	@Override
	public void setMaxOccupancy(int max) {

		int diff = super.getOccupants().size() - max;
		if (diff > 0) {
			Collection<Person> toRemove = new ArrayList<Person>();
			for (Person p : super.getOccupants()) {
				toRemove.add(p);
				diff--;
				if (diff <= 0) {
					break;
				}
			}
			for (Person p : toRemove) {
				removeOccupant(p);
			}
		}
		super.setMaxOccupancy(max);
	}

	@Override
	public boolean removeOccupant(Person p) {
		p.evict();
		return super.getOccupants().remove(p);
	}

	@Override
	public void clear() {
		for (Person p : super.getOccupants()) {
			p.evict();
		}
		super.getOccupants().clear();
	}

	@Override
	public boolean addOccupant(Person p) {
		if (super.getMaxOccupants() <= this.currentOccupancy()) {
			return false;
		}
		boolean success = super.getOccupants().add(p);
		if(success){
			p.liveAt(this);
		}	
		return success;
	}

}
