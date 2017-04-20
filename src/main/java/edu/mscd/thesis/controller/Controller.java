package edu.mscd.thesis.controller;



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
