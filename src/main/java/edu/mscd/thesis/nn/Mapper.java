package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.model.Model;

public interface Mapper {
	
	public double[] getMapOfValues(Model state, Action action);

}
