package edu.mscd.thesis.nn;

import edu.mscd.thesis.model.World;

public interface AI {
	
	void setWorldState(World state);
	
	void takeNextAction();
	
	void train();
	
	void addCase(World state, double score);
	

}
