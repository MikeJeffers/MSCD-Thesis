package edu.mscd.thesis.model;


import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.ModelData;
import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.view.Overlay;
import edu.mscd.thesis.view.Selectable;

public interface Model extends Observer<Action>, Observable<ModelData>, Overlay, Selectable, Runnable{
	/**
	 * Run internal model updates based on behaviors of model system
	 */
	public void update();
	
	public World getWorld();
	
	/**
	 * Halt run loop, Call before Join!
	 */
	public void halt();

}
