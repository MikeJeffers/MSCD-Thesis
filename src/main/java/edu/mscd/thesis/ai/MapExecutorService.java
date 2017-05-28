package edu.mscd.thesis.ai;

import edu.mscd.thesis.model.Model;

public interface MapExecutorService {

	public double[] computeMap(Model state, double[] actionVec);
	
}
