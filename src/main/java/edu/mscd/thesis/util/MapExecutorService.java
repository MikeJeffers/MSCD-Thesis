package edu.mscd.thesis.util;

import edu.mscd.thesis.model.Model;

public interface MapExecutorService {

	public double[] computeMap(Model state, double[] actionVec);
	
}
