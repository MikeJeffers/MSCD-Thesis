package edu.mscd.thesis.model;

import java.util.List;

public interface City {
	List<Person> getPopulation();
	int residentialDemand();
	int commercialDemand();
	int industrialDemand();
	
	

}
