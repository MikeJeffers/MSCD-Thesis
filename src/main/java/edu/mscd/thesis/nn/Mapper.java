package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;

public interface Mapper {
	

	public double[] getMapOfValues(Model state, UserData action);

}
