package edu.mscd.thesis.nn;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class NeuralNet implements AI {

	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private Model state;

	public NeuralNet(Model model) {
		this.state =  (Model) Util.copy(model);
		initNetwork();
		initTrainingDataSet();
		trainBackProp();
	}
	
	private void initTrainingDataSet(){
		double[][] input = {this.getInputArrayFromWorld(state.getWorld())};
		double[][] idealout = {{0.0}};
		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(idealout[i]);
			DATASET.add(trainingIn, idealOut);
		}
	}

	private void trainBackProp() {
		Backpropagation train = new Backpropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 1000);
		train.finishTraining();

		// test the neural network
		System.out.println("Neural Network Results:");
		for (MLDataPair pair : DATASET) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + ", actual="
					+ output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}

		Encog.getInstance().shutdown();
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, state.getWorld().height() * state.getWorld().width()));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, 25));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, 15));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private static double getOutput(double[] input) {
		MLData data = new BasicMLData(input);
		MLData out = network.compute(data);
		double[] output = out.getData();
		return output[0];
	}

	@Override
	public void setWorldState(Model state) {
		this.state = state;

	}

	@Override
	public void takeNextAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void train() {

	}

	@Override
	public void addCase(Model state, double score) {
		// TODO Auto-generated method stub

	}
	
	private double[] getInputArrayFromWorld(World w){
		Tile[] tiles = w.getTiles();
		double[] vals = new double[tiles.length];
		for(int i=0; i<tiles.length; i++){
			vals[i] = getInputValueFromTile(tiles[i]);
		}
		return vals;
	}

	private double[][] getInputFromWorld(World w) {
		int rows = w.height();
		int cols = w.width();
		Tile[] tiles = w.getTiles();
		double[][] inputMap = new double[rows][cols];
		int row = -1;
		for (int i = 0; i < tiles.length; i++) {
			if (i % cols == 0) {
				row++;
			}
			inputMap[row][i % cols] = getInputValueFromTile(tiles[i]);
		}
		return inputMap;
	}

	private double getInputValueFromTile(Tile t) {
		return (double) t.getCurrentLandValue() / (double) Rules.MAX;
	}
	
	
	

}
