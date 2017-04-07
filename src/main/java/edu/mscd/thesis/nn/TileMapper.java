package edu.mscd.thesis.nn;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
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

public class TileMapper implements AI, Mapper {
	/**
	 * input is a single Tile Representation(decomposition of its attributes) and a Zone vector
	 * MLP follows the form of a Q-learning approximation function
	 * output is [0-1.0] where high values indicate optimal locations to place Zone of Action, given current state
	 * 
	 */
	private static final int ZONETYPES = ZoneType.values().length;
	private static final int TILE_ATTRIBUTES = WorldRepresentation.getTileAttributesAsVector(null).length;
	private static final int INPUT_LAYER_SIZE = ZONETYPES+TILE_ATTRIBUTES;
	private static final int OUTPUT_LAYER_SIZE = 1;
	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private Model state;
	private ZoneType zone;


	public TileMapper(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		initNetwork();
		initTraining();
		trainResilient();
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, INPUT_LAYER_SIZE));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (INPUT_LAYER_SIZE * 3) / 2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, (INPUT_LAYER_SIZE / 2)));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, OUTPUT_LAYER_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
	}

	private void initTraining() {
		Tile[] tiles = this.state.getWorld().getTiles();
		double[][] input = new double[tiles.length][INPUT_LAYER_SIZE];
		double[][] output = new double[tiles.length][OUTPUT_LAYER_SIZE];
		
		double[] src = new double[]{0, Rules.MAX};
		double[] target = new double[]{0.0, 1.0};
		for(int i=0; i<tiles.length; i++){
			Tile t = tiles[i];
			ZoneType z = ZoneType.values()[i%ZONETYPES];
			double[] tileRepr =  WorldRepresentation.getTileAttributesAsVector(t);
			double[] zoneRepr = WorldRepresentation.getZoneAsVector(z);
			input[i] = Util.appendVectors(tileRepr, zoneRepr);
			output[i] = new double[]{Util.mapValue(Rules.getValueForZoneOnTile(t.getType(), z), src, target)};
		}
		
		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(output[i]);
			DATASET.add(trainingIn, idealOut);
		}

	}
	


	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	@Override
	public void setWorldState(Model state) {
		this.state = ModelStripper.reducedCopy(state);

	}

	private double getOutput(double[] input) {
		MLData output = network.compute(new BasicMLData(input));
		return output.getData()[0];
	}
	
	public void setZoneOfAction(ZoneType zone){
		this.zone = zone;
	}
	
	
	@Override
	public double[] getMapOfValues() {
		ZoneType zoneAction = this.zone;
		double[] zoneVector = WorldRepresentation.getZoneAsVector(zoneAction);
		World w = this.state.getWorld();
		Tile[] tiles = w.getTiles();
		double[] map = new double[tiles.length];
		Pos2D[] locations = new Pos2D[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = tiles[i].getPos();
			locations[i] = p;
			double[] tileRepr =  WorldRepresentation.getTileAttributesAsVector(tiles[i]);
			double[] input = Util.appendVectors(tileRepr, zoneVector);
			double output = getOutput(input);
			map[i]=output;
		}
		return map;
	}

	@Override
	public UserData takeNextAction() {
		ZoneType zoneAction = this.zone;
		double[] zoneVector = WorldRepresentation.getZoneAsVector(zoneAction);
		World w = this.state.getWorld();
		Tile[] tiles = w.getTiles();
		double[] map = new double[tiles.length];
		Pos2D[] locations = new Pos2D[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = tiles[i].getPos();
			locations[i] = p;
			double[] tileRepr =  WorldRepresentation.getTileAttributesAsVector(tiles[i]);
			double[] input = Util.appendVectors(tileRepr, zoneVector);
			double output = getOutput(input);
			map[i]=output;
		}
		
		int maxIndex = 0;
		int minIndex = 0;
		double maxScore = 0;
		double minScore = Rules.MAX;
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
		System.out.println();
		System.out.print("Worst move:{");
		System.out.print(locations[minIndex]);
		System.out.println();
		
		System.out.println("ZoneDecider picked:{"+zoneAction+"}");
		if(minIndex==maxIndex){
			System.out.println("AI can not find ideal move to make");
			return null;
		}

		UserData fake = new UserData();
		fake.setClickLocation(locations[maxIndex]);
		fake.setZoneSelection(zoneAction);
		fake.setRadius(0);
		fake.setSquare(true);
		fake.setTakeStep(false);
		fake.setDrawFlag(true);
		fake.setAI(true);
		return fake;
	}

	@Override
	public void addCase(Model state, Model prev, UserData action) {


		Pos2D pos = action.getClickLocation();
		Tile targetTile = prev.getWorld().getTileAt(pos);
		ZoneType zoneAct = action.getZoneSelection();
		double prevScore = Rules.score(prev.getWorld().getTileAt(pos));
		double currentScore = Rules.score(state.getWorld().getTileAt(pos));
		double normalizedScoreDiff = ((currentScore - prevScore) / 2.0) + 0.5;
		double[] input = Util.appendVectors(WorldRepresentation.getTileAttributesAsVector(targetTile), WorldRepresentation.getZoneAsVector(zoneAct));
		learn(input, new double[] { normalizedScoreDiff });

	}

	private void learn(double[] input, double[] output) {
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		DATASET.add(trainingIn, idealOut);
		trainResilient();
	}




}
