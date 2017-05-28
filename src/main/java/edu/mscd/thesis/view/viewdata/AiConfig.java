package edu.mscd.thesis.view.viewdata;

import java.util.Map;

import edu.mscd.thesis.ai.activationfunctions.ActivationFunctions;

public interface AiConfig {
	
	public Map<Integer, ActivationFunctions> getActivationFunctions();
	public Map<Integer, Integer> getNeuralDensities();
	public int getLayerCount();
	public int getObservationRadius();
	public int getObservationWaitTime();
	public int getMaxTrainingEpochs();
	public double getMaxError();
	public double getUserMoveBias();
	public int getRegularizationFactor();
	public boolean isLearnFromUser();
	
	
	public AiConfig copy();
	
}
