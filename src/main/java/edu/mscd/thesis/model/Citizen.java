package edu.mscd.thesis.model;

import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.bldgs.Home;
import edu.mscd.thesis.model.bldgs.PlaceOfWork;

public class Citizen implements Person {
	private Building home;
	private Building work;
	private Pos2D currentLocation;

	
	public Citizen(){
		
	}
	@Override
	public Pos2D getCurrentPos() {
		return currentLocation;
	}

	@Override
	public Building getHome() {
		return home;
	}

	@Override
	public Building getWork() {
		return work;
	}

	@Override
	public void employAt(Building b) {
		work = b;
	}

	@Override
	public void liveAt(Building b) {
		home = b;
	}

	@Override
	public float getHappiness() {
		return 1;
	}

	@Override
	public float getMoney() {
		return 1;
	}
	@Override
	public boolean homeless() {
		return this.home==null;
	}
	@Override
	public boolean employed() {
		return this.work!=null;
	}
	@Override
	public void fire() {
		this.work = null;
	}
	@Override
	public void evict() {
		this.home=null;
	}
	@Override
	public void removeSelfFrom(Building b) {
		b.removeOccupant(this);
	}

}
