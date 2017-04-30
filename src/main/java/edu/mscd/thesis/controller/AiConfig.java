package edu.mscd.thesis.controller;

import org.encog.engine.network.activation.ActivationFunction;

public interface AiConfig {
	
	public ActivationFunction getActivationFunc();
	public int getNetworkDepth();
	public int getNeuronDensity();
	public int getObservationRadius();
	public int getObservationWaitTime();
	public int getMaxTrainingEpochs();
	
	
	public AiConfig copy();
	
}
