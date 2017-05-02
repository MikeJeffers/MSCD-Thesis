package edu.mscd.thesis.nn;


import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.RegularizationStrategy;

import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.controller.AiConfigImpl;
import edu.mscd.thesis.util.NNConstants;

public abstract class AbstractNetwork implements Configurable {
	protected AiConfig conf = new AiConfigImpl();
	protected BasicNetwork network = new BasicNetwork();
	protected MLDataSet DATASET = new BasicMLDataSet();
	protected ResilientPropagation train;
	protected int lastIteration;
	protected int inputLayerSize;
	protected final int OUTPUT_LAYER_SIZE = 1;

	protected void initTraining() {
		DATASET.close();
		DATASET = new BasicMLDataSet();
	}

	protected void train() {
		this.train = new ResilientPropagation(network, DATASET);
		Strategy reg = new RegularizationStrategy(0.00000001);
		train.addStrategy(reg);
		int epoch = 1;
		do {
			train.iteration();
			epoch++;
		} while (train.getError() > conf.getMaxError() && epoch < conf.getMaxTrainingEpochs());
		System.out.println("Epochs Required:" + epoch + " to achieve Error:" + train.getError());
		lastIteration = train.getIteration();
	}

	protected void initNetwork() {
		network.addLayer(new BasicLayer(null, true, inputLayerSize));
		for (int i = 0; i < this.conf.getLayerCount() - 1; i++) {
			int neuronCount = NNConstants.getNeuronCountByFactor(inputLayerSize, conf.getNeuralDensities().get(i));
			network.addLayer(new BasicLayer(conf.getActivationFunctions().get(i).getFunction(), true, neuronCount));
		}
		network.addLayer(new BasicLayer(conf.getActivationFunctions().get(conf.getLayerCount() - 1).getFunction(),
				false, OUTPUT_LAYER_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
		System.out.println(network.toString());
		for (int i = 0; i < network.getLayerCount(); i++) {
			System.out.println(network.getLayerNeuronCount(i));
		}
	}

	@Override
	public void configure(AiConfig configuration) {
		this.conf = configuration;
		this.initNetwork();
		this.initTraining();
		this.train();
	}


	protected void learn(MLDataPair pair) {
		int epoch=0;
		DATASET.add(pair);
		while (train.getError() > conf.getMaxError() && epoch<conf.getMaxTrainingEpochs() || epoch<1) {
			epoch++;
			train.iteration();
		}
		System.out.println("Epochs Required:" + epoch + " to achieve Error:" + train.getError());
	}
	
	protected void shutdown(){
		this.train.finishTraining();
		Encog.getInstance().shutdown();
	}

}
