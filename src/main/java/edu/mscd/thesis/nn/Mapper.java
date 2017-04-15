package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.city.CityData;

public interface Mapper {
	

	public double[] getMapOfValues(Model<UserData, CityData> state, UserData action);

}
