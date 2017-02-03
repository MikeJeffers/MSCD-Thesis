package edu.mscd.thesis.model;

import edu.mscd.thesis.model.bldgs.Building;

public interface Person {
	Pos2D getCurrentPos();

	Building getHome();

	Building getWork();
	
	void setWork(Building b);
	
	void setHome(Building b);

	float getHappiness();

	float getMoney();
}
