package edu.mscd.thesis.model.bldgs;

import java.util.Collection;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.view.Sprite;

public interface Building extends Sprite{

	Pos2D getPos();
	Collection<Person> getOccupants();
	int getMaxOccupants();
	int currentOccupancy();
	int getWealth();
	Density getDensity();
	void changeDensity(Density density);
	double update(double growthValue);

}
