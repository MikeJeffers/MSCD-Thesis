package edu.mscd.thesis.nn;


import org.encog.Encog;
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
	protected int inputLayerSize;
	protected final int OUTPUT_LAYER_SIZE = 1;

	protected void initTraining() {
		DATASET.close();
		DATASET = new BasicMLDataSet();
	}

	protected void train() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		Strategy reg = new RegularizationStrategy(0.00000001);
		train.addStrategy(reg);
		int epoch = 1;
		do {
			//System.out.println(train.getLastGradient()[0]);
			train.iteration();
			if(Double.isNaN(train.getError())){
				System.out.println(train.getLastGradient()[0]);
				System.out.println(train.getUpdateValues()[0]);
			}
			epoch++;
		} while (train.getError() > conf.getMaxError() && epoch < conf.getMaxTrainingEpochs());
		train.finishTraining();
		System.out.println("Epochs Required:" + epoch + " to achieve Error:" + train.getError());
		/*
		System.out.println("Neural Network Results:");
		for (MLDataPair pair : DATASET) {
			final MLData output = network.compute(pair.getInput());
			System.out
					.println(Arrays.toString(output.getData()) + ",ideal=" + Arrays.toString(pair.getIdeal().getData())
							+ "; From:" + Arrays.toString(pair.getInput().getData()));
		}
		*/
		Encog.getInstance().shutdown();
	}

	protected void initNetwork() {
		network.addLayer(new BasicLayer(null, true, inputLayerSize));
		for (int i = 0; i < this.conf.getLayerCount()-1; i++) {
			int neuronCount = NNConstants.getNeuronCountByFactor(inputLayerSize, conf.getNeuralDensities().get(i));
			network.addLayer(new BasicLayer(conf.getActivationFunctions().get(i).getFunction(), true, neuronCount));
		}
		network.addLayer(new BasicLayer(conf.getActivationFunctions().get(conf.getLayerCount()-1).getFunction(), false, OUTPUT_LAYER_SIZE));
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

}
