package edu.mscd.thesis.controller;

import java.util.Map;

import edu.mscd.thesis.nn.ActivationFunctions;

public interface AiConfig {
	
	public Map<Integer, ActivationFunctions> getActivationFunctions();
	public Map<Integer, Integer> getNeuralDensities();
	public int getLayerCount();
	public int getObservationRadius();
	public int getObservationWaitTime();
	public int getMaxTrainingEpochs();
	public double getMaxError();
	public double getUserMoveBias();
	public boolean isLearnFromUser();
	
	
	public AiConfig copy();
	
}
