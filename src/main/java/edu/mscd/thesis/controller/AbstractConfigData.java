package edu.mscd.thesis.controller;

public abstract class AbstractConfigData implements ConfigData{

	@Override
	public boolean isAction() {
		return false;
	}

	@Override
	public Action getAction() {
		// TODO throw Error
		return null;
	}

	@Override
	public boolean isConfig() {
		return true;
	}

	@Override
	public ConfigData getConfig() {
		return this;
	}


}
