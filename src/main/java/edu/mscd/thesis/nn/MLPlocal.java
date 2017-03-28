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

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class MLPlocal implements AI {
	/**
	 * input layer is 9-cell neighborhood including center tile, and Moore's
	 * Neighbors output layer is 1 value which is predictive Q-lookup of
	 * eventual game-score as result of action on state score estimate will be
	 * tile-based only, no city data
	 */

	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private Model state;
	private Random random = new Random();

	public MLPlocal(Model model) {
		this.state = ModelStripper.reducedCopy(model);
		initNetwork();
		initTrainingDataSet();
		trainBackProp();
	}

	private void initTrainingDataSet(){
		//getInputAroundTile
		Tile[] tiles = state.getWorld().getTiles();
		
		double[][] input = new double[tiles.length][9];
		double[][] idealout = new double[tiles.length][1];
		for(int i=0; i<input.length; i++){
			input[i] = this.getInputAroundTile(state.getWorld(), tiles[i].getPos());
			idealout[i]=new double[]{Rules.score(tiles[i])};
		}
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
		network.addLayer(new BasicLayer(null, true, 9));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 32));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 15));
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
	public UserData takeNextAction() {
		// TODO Auto-generated method stub
		//evaluate a finite set of random actions
		//pick one with highest value outcome
		World w = this.state.getWorld();
		Tile[] tiles = w.getTiles();
		int possibleActions = tiles.length*4;
		Pos2D[] locations = new Pos2D[possibleActions];
		ZoneType[] zTypes = new ZoneType[possibleActions];
		double[] results = new double[possibleActions];
		int maxIndex = 0;
		int minIndex = 0;
		double maxScore = 0;
		double minScore = Rules.MAX;
		
		for(int i=0 ;i<tiles.length; i++){
			int zoneCounter=0;
			Pos2D pos = tiles[i].getPos();
			for(ZoneType zt: ZoneType.values()){
				int index = i*4+zoneCounter;
				locations[index] = pos;
				zTypes[index] = zt;
				w.setAllZonesAround(locations[index], zTypes[index], 1, true);
				results[index] = getOutput(getInputAroundTile(w, pos));
				if(results[index]>maxScore){
					maxScore = results[index];
					maxIndex=index;
				}
				if(results[index]<minScore){
					minIndex=index;
					minScore = results[index];
				}
				zoneCounter++;
			}
		}
		System.out.println("Possible actions Score domain["+results[minIndex]+","+results[maxIndex]+"]");
		if(results[minIndex]==results[maxIndex]){
			System.out.println("AI does not make move!");
			return null;
		}
		
		System.out.print("Best move:{");
		System.out.print(locations[maxIndex]);
		System.out.println(" "+zTypes[maxIndex]);
		System.out.print("Worst move:{");
		System.out.print(locations[minIndex]);
		System.out.println(" "+zTypes[minIndex]);
		//Rules.score(state);
		UserData fake = new UserData();
		fake.setClickLocation(locations[maxIndex]);
		fake.setZoneSelection(zTypes[maxIndex]);
		fake.setRadius(1);
		fake.setSquare(true);
		fake.setTakeStep(false);
		fake.setDrawFlag(true);
		fake.setAI(true);
		return fake;

	}

	@Override
	public void addCase(Model state, Model prev, UserData action) {
		double currentScore = Rules.score(state);
		double prevScore = Rules.score(prev);
		MLData trainingIn = new BasicMLData(getInputArrayFromWorld(state.getWorld()));
		MLData idealOut = new BasicMLData(new double[] { currentScore });
		DATASET.add(trainingIn, idealOut);
		this.trainBackProp();
		// this.trainResilient();
		if (currentScore > prevScore) {
			System.out.println("AI move improvedScore! " + currentScore + " from " + prevScore);

		} else {
			System.out.println("AI move dropped score =( " + currentScore + " from " + prevScore);
		}

	}

	private double[] getInputAroundTile(World w, Pos2D p) {
		Tile[] tiles = new Tile[9];
		int index = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				Pos2D nLoc = new Pos2D(p.getX() + i, p.getY() + j);
				tiles[index] = w.getTileAt(nLoc);
				index++;
			}
		}
		double[] vals = new double[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			vals[i] = getInputValueFromTile(tiles[i]);
		}
		return vals;
	}

	private double[] getInputArrayFromWorld(World w) {
		Tile[] tiles = w.getTiles();
		double[] vals = new double[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			vals[i] = getInputValueFromTile(tiles[i]);
		}
		return vals;
	}

	private double getInputValueFromTile(Tile t) {
		if (t == null) {
			return 0;
		}
		double numTypes = ZoneType.values().length;
		return (Rules.score(t)+t.getZoneType().ordinal()/numTypes)/2.0;
	}
	

}
