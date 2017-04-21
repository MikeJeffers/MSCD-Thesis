package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.ViewData;

/**
 * Wrapper Interface for full AI system
 * @author Mike
 */
public interface AI extends Actor, Learner, Mapper, Configurable, Observable<ViewData>,  Runnable{
	
	public void tick();
	
}
