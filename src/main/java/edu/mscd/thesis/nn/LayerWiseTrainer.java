package edu.mscd.thesis.nn;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.EngineArray;

/**
 * From: https://github.com/VincSch/NN-Classification-Java-Encog
 * Created by patrick on 17.12.14.
 */
public class LayerWiseTrainer {

    private static final float ERROR_RATE = 0.1f;

    private double[][] input;
    private double[][] ideal;
    private ResilientPropagation prop;
    private BasicNetwork network;
    private MLDataSet trainingsSet;
    //    private List<>

    public LayerWiseTrainer(BasicNetwork network, double[][] input) {
        this.network = network;
        this.input = input;
        this.ideal = input;
        this.trainingsSet = new BasicMLDataSet(this.input, this.input);
        this.prop = new ResilientPropagation(network, this.trainingsSet);
    }

    public LayerWiseTrainer(BasicNetwork network, double[][] input,
        double[][] ideal) {
        this(network, input);
        this.ideal = ideal;
        this.trainingsSet = new BasicMLDataSet(input, ideal);
        this.prop = new ResilientPropagation(network, this.trainingsSet);
    }

    public void train() {
        double[][] input = EngineArray.arrayCopy(this.input);
        FlatNetwork flat = this.network.getFlat();
        double[] weights = flat.getWeights();
        int[] weightIndex = flat.getWeightIndex();
        int[] layerNeuronCount = flat.getLayerFeedCounts();
        for (int i = 0; i < layerNeuronCount.length - 1; i += 1) {
            BasicNetwork temp = new BasicNetwork();
            ActivationSigmoid activation =
                i == 0 ? null : new ActivationSigmoid();
            int inputNeuronCount = layerNeuronCount[layerNeuronCount.length
                - (i + 1)];
            int hiddenNeuronCount = layerNeuronCount[layerNeuronCount.length - (
                i + 2)];
            Layer out = new BasicLayer(new ActivationSigmoid(), false,
                inputNeuronCount);
            double[][] output = new double[input.length][hiddenNeuronCount];

            // create layer to train
            temp.getStructure().getLayers()
                .add(new BasicLayer(activation, true, inputNeuronCount));
            temp.getStructure().getLayers()
                .add(new BasicLayer(new ActivationSigmoid(), true,
                    hiddenNeuronCount));
            temp.getStructure().getLayers().add(out);
            temp.getStructure().finalizeStructure();
            temp.reset();

            //train autoencoder
            BasicMLDataSet trainData = new BasicMLDataSet(input, input);
            this.prop = new ResilientPropagation(temp, trainData);
            do {
                this.prop.iteration();
            } while (this.prop.getError() > ERROR_RATE);
            this.prop.finishTraining();

            //copy the trained weights
            FlatNetwork tempFlat = temp.getFlat();
            double[] tempWeights = tempFlat.getWeights();
            int length = weightIndex.length - 2;
            int[] index = tempFlat.getWeightIndex();
            int offset = index[1];
            int end = index[2];
            System.arraycopy(tempWeights, offset, weights,
                weightIndex[length - i],
                end - offset);

            //Get the new input for the next layer (extracted features)
            for (int p = 0; p < input.length; p++) {
                temp.compute(new BasicMLData(input[p]));
                for (int j = 0; j < temp.getLayerNeuronCount(1); j++)
                    output[p][j] = temp.getLayerOutput(1, j);
                input[p] = output[p];
            }
        }
    }

    public void fineTune() {
        this.trainingsSet = new BasicMLDataSet(input, ideal);
        this.prop = new ResilientPropagation(this.network, this.trainingsSet);
        do {
            this.prop.iteration();
        } while (this.prop.getError() > ERROR_RATE);
    }
}
