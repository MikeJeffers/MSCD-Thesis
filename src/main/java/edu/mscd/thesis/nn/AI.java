package edu.mscd.thesis.nn;

import edu.mscd.thesis.model.Model;

public interface AI {
	
	void setWorldState(Model state);
	
	void takeNextAction();
	
	void train();
	
	void addCase(Model state, double score);
	

}
