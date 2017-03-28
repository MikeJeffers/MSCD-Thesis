package edu.mscd.thesis.nn;

import java.util.Arrays;
import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationElliott;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSIN;
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

import edu.mscd.thesis.controller.UserData;
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
	
	private int INPUT_SIZE;
	private Model state;
	private Model trueModel;
	private Random random = new Random();
	
	//TODO nested nets
	private AI zonePicker;
	private AutoEncoder encoder;
	private MapEncoder mapper;

	public NeuralNet(Model model) {
		trueModel = model;
		this.state = ModelStripper.reducedCopy(model);
		INPUT_SIZE = this.state.getWorld().getTiles().length;

		initEncoder();

		initNetwork();
		initTrainingDataSet();
		//trainBackProp();
		
		this.zonePicker = new ZoneDecider(this.state);
		this.mapper = new MapEncoder(this.state);
	}

	private void initEncoder() {
		
		encoder = new AutoEncoder(INPUT_SIZE, INPUT_SIZE/4, 2);

		double[][] inputData = { this.getInputArrayFromWorld(state.getWorld()) };
		double[][] idealData = { this.getInputArrayFromWorld(state.getWorld()) };
		// train the neural network
		final LayerWiseTrainer train = new LayerWiseTrainer(encoder, inputData, idealData);
		train.train();
		train.fineTune();

		// test the neural network
		for (int i = 0; i < inputData.length; i++) {
			MLData output = encoder.compute(new BasicMLData(inputData[i]));
			System.out.println(Arrays.toString(inputData[i]));
			System.out.println(Arrays.toString(output.getData()));
		}
		Encog.getInstance().shutdown();
	}
	
	private double[] encode(double[] input){
		MLData output = encoder.compute(new BasicMLData(input));
		return output.getData();
	}

	private void initTrainingDataSet() {
		double[][] input = { getInputArrayFromWorld(state.getWorld())};
		double[][] idealout = { { Rules.score(state) } };
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
		} while (train.getError() > 0.01 && epoch < 100);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			// System.out.println("Epoch #" + epoch + " Error:" +
			// train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, INPUT_SIZE));
		network.addLayer(new BasicLayer(new ActivationLOG(), true, 64));
		network.addLayer(new BasicLayer(new ActivationLOG(), true, 16));
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
		this.zonePicker.setWorldState(state);
		this.mapper.learn(state);

	}

	@Override
	public UserData takeNextAction() {
		// TODO Auto-generated method stub
		// evaluate a finite set of random actions
		// pick one with highest value outcome
		World w = this.state.getWorld();
		Tile[] tiles = w.getTiles();
		int possibleActions = tiles.length * 4;
		Pos2D[] locations = new Pos2D[possibleActions];
		ZoneType[] zTypes = new ZoneType[possibleActions];
		double[] results = new double[possibleActions];
		int maxIndex = 0;
		int minIndex = 0;
		double maxScore = 0;
		double minScore = Rules.MAX;

		for (int i = 0; i < tiles.length; i++) {
			int zoneCounter = 0;
			Pos2D pos = tiles[i].getPos();
			for (ZoneType zt : ZoneType.values()) {
				int index = i * 4 + zoneCounter;
				locations[index] = pos;
				zTypes[index] = zt;
				w = state.getWorld();//Get copy of self
				w.setAllZonesAround(locations[index], zTypes[index], 1, true);
				results[index] = getOutput(getInputArrayFromWorld(w));
				if (results[index] > maxScore) {
					maxScore = results[index];
					maxIndex = index;
				}
				if (results[index] < minScore) {
					minIndex = index;
					minScore = results[index];
				}
				zoneCounter++;
			}
		}
		
		System.out.println("Possible actions based on some bullshit Score domain["+results[minIndex]+","+results[maxIndex]+"]");
		System.out.print("Best move:{");
		System.out.print(locations[maxIndex]);
		System.out.println(" " + zTypes[maxIndex]);
		System.out.print("Worst move:{");
		System.out.print(locations[minIndex]);
		System.out.println(" " + zTypes[minIndex]);
		
		
		double[] map = this.mapper.scoreWorldState(this.state);
		
		maxIndex = 0;
		minIndex = 0;
		maxScore = 0;
		minScore = Rules.MAX;
		for(int i=0; i<map.length; i++){
			if(map[i]<minScore){
				minScore = map[i];
				minIndex = i;
			}
			if(map[i]>maxScore){
				maxScore = map[i];
				maxIndex = i;
			}
		}
		
		System.out.println("Possible actions based on Mapped Score domain["+map[minIndex]+","+map[maxIndex]+"]");
		System.out.print("Best move:{");
		System.out.print(locations[maxIndex]);
		System.out.println(" " + zTypes[maxIndex]);
		System.out.print("Worst move:{");
		System.out.print(locations[minIndex]);
		System.out.println(" " + zTypes[minIndex]);
		
		if(results[minIndex]==results[maxIndex]){
			System.out.println("AI does not make move!");
			return null;
		}
		
		

		UserData zoneChoice = this.zonePicker.takeNextAction();
		zoneChoice.getZoneSelection();
		System.out.print("ZoneDecider picked:{");
		System.out.print(zoneChoice.getZoneSelection()+" vs ");
		System.out.println(zTypes[maxIndex]+"}");
		// Rules.score(state);
		UserData fake = new UserData();
		fake.setClickLocation(locations[maxIndex]);
		fake.setZoneSelection(zoneChoice.getZoneSelection());
		fake.setRadius(1);
		fake.setSquare(true);
		fake.setTakeStep(false);
		fake.setDrawFlag(true);
		fake.setAI(true);
		return fake;

	}


	@Override
	public void addCase(Model state, Model prev, UserData action) {
		this.zonePicker.addCase(state, prev, action);
		double currentScore = Rules.score(state);
		double prevScore = Rules.score(prev);
		MLData trainingIn = new BasicMLData(getInputArrayFromWorld(state.getWorld()));
		MLData idealOut = new BasicMLData(new double[] { currentScore });
		DATASET.add(trainingIn, idealOut);
		// this.trainBackProp();
		this.trainResilient();
		if (currentScore > prevScore) {
			System.out.println("AI move improvedScore! " + currentScore + " from " + prevScore);

		} else {
			System.out.println("AI move dropped score =( " + currentScore + " from " + prevScore);
		}

	}

	private double[] getInputArrayFromWorld(World w) {
		double[] vals = WorldRepresentation.getWorldAsZoneVector(w);
		/*
		Tile[] tiles = w.getTiles();
		double[] vals = new double[tiles.length];
		for(int i=0; i<vals.length; i++){
			vals[i] = getInputValueFromTile(tiles[i]);
		}
		*/
		double[] encoded = encode(vals);
		return encoded;
	}

	private double getInputValueFromTile(Tile t) {
		if (t == null) {
			return 0;
		}
		double numTypes = ZoneType.values().length;
		return (Rules.score(t)+t.getZoneType().ordinal()/numTypes)/2.0;
	}

}
