package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.city.CityData;

public interface Actor {
	
	public UserData takeNextAction();
	
	public void setState(Model<UserData, CityData> state);

}
