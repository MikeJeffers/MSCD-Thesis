package edu.mscd.thesis.model.city;

import java.util.Collection;

import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.zones.ZoneType;

public interface City {
	public CityData getData();
	
	Collection<Person> getPopulation();

	int totalPopulation();

	double percentageHomeless();

	double percentageUnemployed();

	double averageHappiness();

	double averageWealth();

	Collection<Person> getUnemployed();

	Collection<Person> getHomeless();

	double residentialDemand();

	double commercialDemand();

	double industrialDemand();
	
	int getZoneCount(ZoneType zt);
	
	double densityRating();

	void update();

}
