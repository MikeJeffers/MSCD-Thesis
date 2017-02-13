package edu.mscd.thesis.controller;

/**
 * Wrapper interface for Runnable Observer that contains game-loop
 * AnimationTimer
 * 
 * @author Mike
 */
public interface Controller extends Observer<UserData>, Runnable {
	public void start();

	public void stop();
}
