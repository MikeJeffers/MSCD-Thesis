package edu.mscd.thesis.nn;

import java.util.Arrays;
import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationLinear;
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
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class NeuralNet implements AI {

	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private Model state;
	private Random random = new Random();

	public NeuralNet(Model model) {
		this.state =  ModelStripper.reducedCopy(model);
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
	
	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 10);
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
		this.state = ModelStripper.reducedCopy(state);

	}

	@Override
	public void takeNextAction() {
		// TODO Auto-generated method stub
		//evaluate a finite set of random actions
		//pick one with highest value outcome
		World w = this.state.getWorld();
		int possibleActions = 10;
		Pos2D[] locations = new Pos2D[possibleActions];
		ZoneType[] zTypes = new ZoneType[possibleActions];
		double[] results = new double[possibleActions];
		for(int i=0; i<possibleActions; i++){
			locations[i] = randomPos();
			zTypes[i] = randomZone();
			w.setZoneAt(locations[i], zTypes[i]);
			results[i] = getOutput(getInputArrayFromWorld(w));
		}
		System.out.println(Arrays.toString(results));
		//Rules.score(state);
		

	}
	
	private Pos2D randomPos(){
		int x = random.nextInt(state.getWorld().width());
		int y = random.nextInt(state.getWorld().height());
		return new Pos2D(x, y);
	}
	
	private ZoneType randomZone(){
		return ZoneType.values()[random.nextInt(ZoneType.values().length)];
	}

	@Override
	public void train() {

	}

	@Override
	public void addCase(Model state, double score) {
		MLData trainingIn = new BasicMLData(getInputArrayFromWorld(state.getWorld()));
		MLData idealOut = new BasicMLData(new double[]{score});
		DATASET.add(trainingIn, idealOut);
		// TODO Auto-generated method stub
		this.trainResilient();

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
