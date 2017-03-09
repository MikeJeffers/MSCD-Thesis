package edu.mscd.thesis.model;


import edu.mscd.thesis.controller.UserData;

public interface Model{
	/**
	 * Run internal model updates based on behaviors of model system
	 */
	public void update();
	
	/**
	 * Notify Model with user actions from GUI
	 * @param newSelectionData - UserData package 
	 */
	public void userStateChange(UserData newSelectionData);
	
	public World getWorld();

}
