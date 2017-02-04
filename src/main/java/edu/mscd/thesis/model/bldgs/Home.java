package edu.mscd.thesis.model.bldgs;

import java.util.ArrayList;
import java.util.Collection;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.ZoneType;

public abstract class Home extends AbstractBuilding{

	public Home(Pos2D pos, TileType tileType, ZoneType zoneType) {
		super(pos, tileType, zoneType);
	}
	
	@Override
	public void clear(){
		Collection<Person> copy = new ArrayList<Person>();
		copy.addAll(super.getOccupants());
		for(Person p: copy){
			p.removeSelfFrom(this);
			p.removeSelfFrom(p.getWork());
		}
		super.getOccupants().clear();
	}
	
	@Override
	public void setMaxOccupancy(int max) {
		int diff = super.getOccupants().size()-max;
		if(diff>0){
			Collection<Person> toRemove = new ArrayList<Person>();
			for(Person p: getOccupants()){
				toRemove.add(p);
				diff--;
				if(diff<=0){
					break;
				}
			}
			for(Person p: toRemove){
				p.removeSelfFrom(this);
				p.removeSelfFrom(p.getWork());
			}
		}
		super.setMaxOccupancy(max);
	}


	@Override
	public boolean addOccupant(Person p){
		if(p!=null && p.homeless()){
			if( super.addOccupant(p)){
				p.liveAt(this);
				return true;
			}
		}
		return false;
	}

}
