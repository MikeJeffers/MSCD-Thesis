package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;

public interface Actor {
	
	public UserData takeNextAction();
	
	public void setState(Model<UserData, CityData> state);

}
