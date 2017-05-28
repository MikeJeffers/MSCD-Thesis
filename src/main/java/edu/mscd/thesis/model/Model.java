package edu.mscd.thesis.model;


import java.util.concurrent.locks.Lock;

import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.model.tiles.Overlay;
import edu.mscd.thesis.model.tiles.Selectable;
import edu.mscd.thesis.view.viewdata.Action;

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
	
	public Lock getLock();

}
