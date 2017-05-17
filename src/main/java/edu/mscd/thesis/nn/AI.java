package edu.mscd.thesis.nn;

import java.util.concurrent.locks.Lock;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.ViewData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.util.WeightVector;

/**
 * Wrapper Interface for full AI system
 * @author Mike
 */
public interface AI extends Actor, Learner, Mapper, Configurable, Observable<ViewData>,  Runnable{
	
	public void update(Model state, Action action, WeightVector<CityProperty> weights);
	
	public void forceUpdate();
	
	/**
	 * Halt run loop, Call before Join!
	 */
	public void halt();
	
	
	public Lock getLock();
}
