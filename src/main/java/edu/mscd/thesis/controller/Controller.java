package edu.mscd.thesis.controller;

import edu.mscd.thesis.model.ModelData;
import edu.mscd.thesis.view.viewdata.ViewData;

/**
 * Wrapper interface for Runnable that contains game-loop AnimationTimer and observers of Model and View
 * 
 * 
 * @author Mike
 */
public interface Controller extends Runnable{
	public void start();

	public void stop();
	
	public void notifyViewEvent(ViewData data);
	
	public void notifyModelEvent(ModelData data);
	
}
