package edu.mscd.thesis.model;


import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.Observer;

public interface Model<T,U> extends Observer<T>, Observable<U>, Overlay{
	/**
	 * Run internal model updates based on behaviors of model system
	 */
	public void update();
	
	public World getWorld();

}
