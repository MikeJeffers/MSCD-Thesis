package edu.mscd.thesis.model;

import java.util.Collection;

import edu.mscd.thesis.view.Sprite;

public interface Building extends Sprite{

	Pos2D getPos();
	Collection<Person> getOccupants();

}
