package edu.mscd.thesis.controller;

public interface GameConfig {
	
	public boolean isPaused();
	public boolean isStep();
	public double getSpeed();
	public AiMode getAiMode();
	
	public GameConfig copy();
	

}
