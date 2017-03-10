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

public class NeuralNet implements AI {

	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private Model state;
	private Model trueModel;
	private Random random = new Random();

	public NeuralNet(Model model) {
		trueModel = model;
		this.state =  ModelStripper.reducedCopy(model);
		initNetwork();
		initTrainingDataSet();
		trainBackProp();
	}
	
	private void initTrainingDataSet(){
		double[][] input = {this.getInputArrayFromWorld(state.getWorld())};
		double[][] idealout = {{Rules.score(state)}};
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
			//System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

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
	public UserData takeNextAction() {
		// TODO Auto-generated method stub
		//evaluate a finite set of random actions
		//pick one with highest value outcome
		World w = this.state.getWorld();
		Tile[] tiles = w.getTiles();
		int possibleActions = tiles.length*3;
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
				int index = i+zoneCounter;
				locations[index] = pos;
				zTypes[index] = zt;
				w.setAllZonesAround(locations[index], zTypes[index], 1, true);
				results[index] = getOutput(getInputArrayFromWorld(w));
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
		
		
		/* --Random--
		for(int i=0; i<possibleActions; i++){
			locations[i] = randomPos();
			zTypes[i] = randomZone();
			w.setAllZonesAround(locations[i], zTypes[i], 1, true);
			results[i] = getOutput(getInputArrayFromWorld(w));
			if(results[i]>maxScore){
				maxScore = results[i];
				maxIndex=i;
			}
			if(results[i]<minScore){
				minIndex=i;
				minScore = results[i];
			}
		}
		*/
		
		System.out.println("Possible actions Score domain["+results[minIndex]+","+results[maxIndex]+"]");
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
	public void addCase(Model state, Model prev) {
		double currentScore = Rules.score(state);
		double prevScore = Rules.score(prev);
		MLData trainingIn = new BasicMLData(getInputArrayFromWorld(state.getWorld()));
		MLData idealOut = new BasicMLData(new double[]{currentScore});
		DATASET.add(trainingIn, idealOut);
		this.trainBackProp();
		//this.trainResilient();
		if(currentScore>prevScore){
			System.out.println("AI move improvedScore! " +currentScore+" from "+prevScore);
			
		}else{
			System.out.println("AI move dropped score =( " +currentScore+" from "+prevScore);
		}

	}
	
	private double[] getInputArrayFromWorld(World w){
		Tile[] tiles = w.getTiles();
		double[] vals = new double[tiles.length];
		for(int i=0; i<tiles.length; i++){
			vals[i] = getInputValueFromTile(tiles[i]);
		}
		return vals;
	}


	private double getInputValueFromTile(Tile t) {
		double numTypes = ZoneType.values().length;
		return t.getZoneType().ordinal()/numTypes;
	}
	
	
	

}
