package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.model.Model;

/**
 * AI component that is capable of exerting actions in Game-space
 * @author Mike
 */
public interface Actor {
	
	/**
	 * Request move(UserData) from AI system given current state and training of system.
	 * @return UserData of action to be taken on World
	 */
	public Action takeNextAction();
	
	/**
	 * Set current model-data for AI (non-learning)
	 * @param state - Model data (should be reduced or non-actionable copy)
	 */
	public void setState(Model state);

}
