package edu.mscd.thesis.controller;

import java.util.HashMap;
import java.util.Map;

import edu.mscd.thesis.nn.ActivationFunctions;
import edu.mscd.thesis.util.NNConstants;
import edu.mscd.thesis.util.Util;

public class AiConfigImpl extends AbstractConfigData implements AiConfig {

	private Map<Integer, ActivationFunctions> actFunctions;
	private Map<Integer, Integer> neuralDensities;
	private int layerCount;
	private int radius;
	private int waitTime;
	private int maxEpochs;
	private double maxError;

	public AiConfigImpl() {
		this.actFunctions = new HashMap<Integer, ActivationFunctions>();
		this.neuralDensities = new HashMap<Integer, Integer>();

		this.maxError = 0.025;
		this.layerCount = 3;
		this.radius = 1;
		this.waitTime = 10;
		this.maxEpochs = 150;
		for (int i = 0; i < this.layerCount; i++) {
			this.actFunctions.put(i, ActivationFunctions.SIGMOID);
			if (i + 1 == this.layerCount) {
				this.neuralDensities.put(i, 1);
			} else {
				this.neuralDensities.put(i, this.layerCount - i);
			}
		}
	}

	@Override
	public int getLayerCount() {
		return this.layerCount;
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

	@Override
	public double getMaxError() {
		return this.maxError;
	}

	@Override
	public Map<Integer, ActivationFunctions> getActivationFunctions() {
		return this.actFunctions;
	}

	@Override
	public Map<Integer, Integer> getNeuralDensities() {
		return this.neuralDensities;
	}

	public void setActivationFunctions(Map<Integer, ActivationFunctions> funcs) {
		this.actFunctions = funcs;
	}

	public void setNeuralDensities(Map<Integer, Integer> densities) {
		this.neuralDensities = densities;
	}

	public void setNeuralDensity(int index, int density) {
		if (density >= NNConstants.MIN_DENSITY && density <= NNConstants.MAX_DENSITY) {
			this.neuralDensities.put(index, density);
		}
	}

	public void setActivationFunc(int index, ActivationFunctions func) {
		this.actFunctions.put(index, func);
	}

	public void setMaxError(double error) {
		this.maxError = Util.boundValue(error, NNConstants.MIN_ERROR_RATE, NNConstants.MAX_ERROR_RATE);
	}

	public void setMaxTrainingEpochs(int epochs) {
		this.maxEpochs = (int) Util.boundValue(epochs, NNConstants.MIN_EPOCHS, NNConstants.MAX_EPOCHS);
	}

	public void setNumLayers(int depth) {
		if (depth >= NNConstants.MIN_LAYERS && depth <= NNConstants.MAX_LAYERS) {
			this.layerCount = depth;
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
		a.setNumLayers(this.getLayerCount());
		a.setActivationFunctions(this.getActivationFunctions());
		a.setNeuralDensities(this.getNeuralDensities());
		a.setObservationRadius(this.getObservationRadius());
		a.setObservationWaitTime(this.getObservationWaitTime());
		a.setMaxTrainingEpochs(this.getMaxTrainingEpochs());
		a.setMaxError(this.getMaxError());
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append("{");
		sb.append(" Layers:");
		sb.append(this.getLayerCount());
		for(int i=0; i<this.getLayerCount(); i++){
			sb.append("Layer"+i);
			sb.append(" NeuronDensity:");
			sb.append(this.getNeuralDensities().get(i));
			sb.append(" Activiation:");
			sb.append(this.getActivationFunctions().get(i).name());
		}
		sb.append(" ObserveRadius:");
		sb.append(this.getObservationRadius());
		sb.append(" ObserveTime:");
		sb.append(this.getObservationWaitTime());
		sb.append(" TrainingEpochs:");
		sb.append(this.getMaxTrainingEpochs());
		sb.append(" MaxError:");
		sb.append(this.getMaxError());
		sb.append("}");
		return sb.toString();
	}

}
