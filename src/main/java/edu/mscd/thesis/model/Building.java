package edu.mscd.thesis.model;

import java.util.Collection;

public interface Building {

	Pos2D getPos();
	Collection<Person> getOccupants();

}
