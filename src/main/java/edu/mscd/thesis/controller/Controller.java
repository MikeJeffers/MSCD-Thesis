package edu.mscd.thesis.controller;

/**
 * Wrapper interface for Runnable Observer that contains game-loop
 * AnimationTimer
 * 
 * @author Mike
 */
public interface Controller extends Runnable {
	public void start();

	public void stop();
	
	public void notifyViewEvent(UserData data);
	
	public void notifyModelEvent(CityData data);
}
