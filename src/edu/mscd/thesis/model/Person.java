package edu.mscd.thesis.model;

public interface Person {
	Pos2D getCurrentPos();

	Building getHome();

	Building getWork();

	float getHappiness();

	float getMoney();
}
