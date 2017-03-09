package edu.mscd.thesis.model;

import java.util.Collection;

import edu.mscd.thesis.model.zones.ZoneType;

public interface City{
	Collection<Person> getPopulation();
	int totalPopulation();
	double percentageHomeless();
	double percentageUnemployed();
	Collection<Person> getUnemployed();
	Collection<Person> getHomeless();

	int residentialDemand();

	int commercialDemand();

	int industrialDemand();

	int zoneCount(ZoneType zType);
	
	void update();

}
