package edu.mscd.thesis.model.bldgs;

import java.util.ArrayList;
import java.util.Collection;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.people.Person;
import edu.mscd.thesis.model.tiles.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;

public abstract class PlaceOfWork extends AbstractBuilding {

	public PlaceOfWork(Pos2D pos, TileType tileType, ZoneType zoneType, Density density) {
		super(pos, tileType, zoneType, density);

	}
	
	@Override
	public double update(double growthValue){
		payOccupants();
		return growthValue = super.update(growthValue);
	}
	
	private void payOccupants(){
		for(Person p: this.getOccupants()){
			double distFactor = 1.0;
			if(p.getWork()!=null && p.getHome()!=null){
				double dist = p.getWork().getPos().distBetween(p.getHome().getPos());
				distFactor = Rules.distanceDecayFunction(dist);
			}
			p.pay((int) ((Rules.WEALTH_UNIT*this.getDensity().getDensityLevel())*distFactor));
		}
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
		p.fire();
		return super.getOccupants().remove(p);
	}

	@Override
	public void clear() {
		for (Person p : super.getOccupants()) {
			p.fire();
		}
		super.getOccupants().clear();
	}

	@Override
	public boolean addOccupant(Person p) {
		if (super.getMaxOccupants() <= this.currentOccupancy()) {
			return false;
		}
		boolean success = super.getOccupants().add(p);
		if (success) {
			p.employAt(this);
		}
		return success;
	}

}
