package edu.mscd.thesis.controller;

import org.encog.engine.network.activation.ActivationFunction;

import edu.mscd.thesis.nn.ActivationFunctions;

public class AiConfigImpl extends AbstractConfigData implements AiConfig {

	private ActivationFunctions function;
	private int networkDepth;
	private int neuronDensity;
	private int radius;
	private int waitTime;

	
	public AiConfigImpl(){
		this.function = ActivationFunctions.SIGMOID;
		this.networkDepth = 2;
		this.neuronDensity = 5;
		this.radius = 1;
		this.waitTime = 5;
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

	public void setActivationFunc(ActivationFunctions f) {
		this.function = f;
	}

	public void setNetworkDepth(int depth) {
		if (depth > 0 && depth < 10) {
			this.networkDepth = depth;
		}
	}

	public void setNeuronDensity(int density) {
		if (density > 0 && density < 10) {
			this.neuronDensity = density;
		}
	}

	public void setObservationRadius(int radius) {
		if (radius >= 0 && radius < 10) {
			this.radius = radius;
		}
	}

	public void setObservationWaitTime(int numTurns) {
		if (numTurns > 0 && numTurns < 10) {
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
		sb.append("}");
		return sb.toString();
	}

}
