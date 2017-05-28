package edu.mscd.thesis.view.viewdata;

public interface GameConfig {
	
	public boolean isPaused();
	public boolean isStep();
	public double getSpeed();
	public AiMode getAiMode();
	public DocumentMode getDocumentMode();
	public GameConfig copy();
	

}
