package edu.mscd.thesis.util;


import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldReduced;
import edu.mscd.thesis.model.city.CityData;

public class ModelStripper {

	public static Model<UserData, CityData> reducedCopy(Model<UserData, CityData> m) {
		Model<UserData, CityData> copy = new WorldReduced(m.getWorld());
		return copy;
	}


}
