package edu.mscd.thesis.model.bldgs;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.ZoneType;

public abstract class Home extends AbstractBuilding{

	public Home(Pos2D pos, TileType tileType, ZoneType zoneType) {
		super(pos, tileType, zoneType);
	}

	
	@Override
	public boolean addOccupant(Person p){
		if(p!=null && p.homeless()){
			if( super.addOccupant(p)){
				p.setHome(this);
				return true;
			}
		}
		return false;
	}

}
