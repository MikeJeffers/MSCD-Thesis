package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;

public interface Mapper {
	

	public double[] getMapOfValues(Model<UserData, CityData> state, UserData action);

}
