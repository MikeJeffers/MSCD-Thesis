package edu.mscd.thesis.controller;

public interface GameConfig {
	
	public boolean isPaused();
	public boolean isStep();
	public int getSpeed();
	public AiMode getAiMode();
	
	public GameConfig copy();
	

}
