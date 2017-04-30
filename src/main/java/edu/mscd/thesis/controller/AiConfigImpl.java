package edu.mscd.thesis.controller;

import org.encog.engine.network.activation.ActivationFunction;

import edu.mscd.thesis.nn.ActivationFunctions;
import edu.mscd.thesis.util.NNConstants;
import edu.mscd.thesis.util.Util;

public class AiConfigImpl extends AbstractConfigData implements AiConfig {

	private ActivationFunctions function;
	private int networkDepth;
	private int neuronDensity;
	private int radius;
	private int waitTime;
	private int maxEpochs;

	
	public AiConfigImpl(){
		this.function = ActivationFunctions.SIGMOID;
		this.networkDepth = 2;
		this.neuronDensity = 3;
		this.radius = 1;
		this.waitTime = 5;
		this.maxEpochs = 100;
	}

	@Override
	public ActivationFunction getActivationFunc() {
		return this.function.getFunction();
	}

	@Override
	public int getNetworkDepth() {
		return this.networkDepth;
	}

	@Override
	public int getNeuronDensity() {
		return this.neuronDensity;
	}

	@Override
	public int getObservationRadius() {
		return this.radius;
	}

	@Override
	public int getObservationWaitTime() {
		return this.waitTime;
	}
	
	@Override
	public int getMaxTrainingEpochs() {
		return this.maxEpochs;
	}
	
	public void setMaxTrainingEpochs(int epochs){
		this.maxEpochs = (int) Util.boundValue(epochs, NNConstants.MIN_EPOCHS, NNConstants.MAX_EPOCHS);
	}

	public void setActivationFunc(ActivationFunctions f) {
		this.function = f;
	}

	public void setNetworkDepth(int depth) {
		if (depth >= NNConstants.MIN_DEPTH && depth <= NNConstants.MAX_DEPTH) {
			this.networkDepth = depth;
		}
	}

	public void setNeuronDensity(int density) {
		if (density >=NNConstants.MIN_DENSITY && density <= NNConstants.MAX_DENSITY) {
			this.neuronDensity = density;
		}
	}

	public void setObservationRadius(int radius) {
		if (radius >= NNConstants.MIN_RADIUS && radius <= NNConstants.MAX_RADIUS) {
			this.radius = radius;
		}
	}

	public void setObservationWaitTime(int numTurns) {
		if (numTurns >= NNConstants.MIN_WAIT && numTurns <= NNConstants.MAX_WAIT) {
			this.waitTime = numTurns;
		}
	}

	@Override
	public AiConfig copy() {
		AiConfigImpl a = new AiConfigImpl();
		a.setActivationFunc(this.function);
		a.setNetworkDepth(this.getNetworkDepth());
		a.setNeuronDensity(this.getNeuronDensity());
		a.setObservationRadius(this.getObservationRadius());
		a.setObservationWaitTime(this.getObservationWaitTime());
		a.setMaxTrainingEpochs(this.getMaxTrainingEpochs());
		return a;
	}

	@Override
	public boolean isGameConfig() {
		return false;
	}

	@Override
	public boolean isAiConfig() {
		return true;
	}

	@Override
	public GameConfig getGameConfiguration() {
		// TODO throw error
		return null;
	}

	@Override
	public AiConfig getAiConfig() {
		return this;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append("{");
		sb.append("ActivationFunc:");
		sb.append(this.getActivationFunc().getClass().getName());
		sb.append(" Layers:");
		sb.append(this.getNetworkDepth());
		sb.append(" NeuronCount:");
		sb.append(this.getNeuronDensity());
		sb.append(" ObserveRadius:");
		sb.append(this.getObservationRadius());
		sb.append(" ObserveTime:");
		sb.append(this.getObservationWaitTime());
		sb.append(" TrainingEpochs:");
		sb.append(this.getMaxTrainingEpochs());
		sb.append("}");
		return sb.toString();
	}

	

}
