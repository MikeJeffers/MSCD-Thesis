package edu.mscd.thesis.model;

import edu.mscd.thesis.model.bldgs.Building;

public interface Person {
	Pos2D getCurrentPos();

	Building getHome();

	Building getWork();

	float getHappiness();

	float getMoney();
}
