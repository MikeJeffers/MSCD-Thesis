package edu.mscd.thesis.model.bldgs;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;

public abstract class PlaceOfWork extends AbstractBuilding{
	
	public PlaceOfWork(Pos2D pos, TileType tileType, ZoneType zoneType) {
		super(pos, tileType, zoneType);
	}
	
	@Override
	public double update(double growthValue){
		if (this.getTileType().getMaxDensity().getDensityLevel() >= getDensity().getDensityLevel()) {
			if (growthValue > Rules.GROWTH_THRESHOLD && this.currentOccupancy()+1>this.getMaxOccupants()) {
				this.changeDensity(getDensity().getNextLevel());
				return growthValue-Rules.BASE_GROWTH_COST;
			}else if(growthValue < Rules.GROWTH_THRESHOLD || this.currentOccupancy()<this.getMaxOccupants()-2){
				this.changeDensity(getDensity().getPrevLevel());
				return growthValue+Rules.BASE_GROWTH_COST*2;
			}
		}
		return growthValue;
	}

	@Override
	public boolean addOccupant(Person p){
		if(p!=null && !p.employed()){
			if( super.addOccupant(p)){
				p.employAt(this);
				return true;
			}
		}
		return false;
	}

}
