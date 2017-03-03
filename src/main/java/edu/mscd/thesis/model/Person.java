package edu.mscd.thesis.model;

import java.io.Serializable;

import edu.mscd.thesis.model.bldgs.Building;

public interface Person extends Serializable{
	Pos2D getCurrentPos();

	Building getHome();
	boolean homeless();

	Building getWork();
	boolean employed();

	void fire();
	void evict();
	void employAt(Building b);

	void liveAt(Building b);

	int getHappiness();

	int getMoney();
	int getID();
	int getAge();
	void update();
	
}
