package edu.mscd.thesis.view.viewdata;

public interface GameConfig {
	
	/**
	 * True if game should pause
	 * @return true if paused
	 */
	public boolean isPaused();
	
	/**
	 * True if Step was pressed, forcing turn to cycle
	 * Only effective if isPaused()
	 * @return True if Step pressed
	 */
	public boolean isStep();
	
	/**
	 * Speed factor for turns if game is not paused
	 * @return double speedfactor
	 */
	public double getSpeed();
	
	/**
	 * Determines AI interaction behavior in game
	 * @return AiMode 
	 */
	public AiMode getAiMode();
	
	/**
	 * Determines recording behavior of game.
	 * Will determine when screenshots and score-reports are logged, on what event, and how often
	 * @return mode to record game data
	 */
	public DocumentMode getDocumentMode();
	
	/**
	 * Only for DocuementMode.ON_INTERVAL
	 * @return int Interval to wait between game-captures
	 */
	public int getInterval();
	
	/**
	 * Deep-copy of configdata
	 * @return GameConfig
	 */
	public GameConfig copy();
	

}
