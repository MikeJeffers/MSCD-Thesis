package edu.mscd.thesis.view.viewdata;

public interface ConfigData extends ViewData{
	public boolean isGameConfig();
	public boolean isAiConfig();
	public GameConfig getGameConfiguration();
	public AiConfig getAiConfig();

}
