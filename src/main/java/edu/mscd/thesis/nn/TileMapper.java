package edu.mscd.thesis.nn;

import java.util.ArrayList;
import java.util.List;

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
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.city.CityData;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class TileMapper implements Learner, Mapper {
	/**
	 * input is a single Tile Representation(decomposition of its attributes) and a Zone vector
	 * MLP follows the form of a Q-learning approximation function
	 * output is [0-1.0] where high values indicate optimal locations to place Zone of Action, given current state
	 * 
	 */
	private static final int ZONETYPES = ZoneType.values().length;
	private static final int TILE_ATTRIBUTES = ModelToVec.getTileAttributesAsVector(null).length;
	private static final int INPUT_LAYER_SIZE = ZONETYPES+TILE_ATTRIBUTES*9;
	private static final int OUTPUT_LAYER_SIZE = 1;
	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();



	public TileMapper(Model<UserData, CityData> state) {
		initNetwork();
		initTraining(state);
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

	private void initTraining(Model<UserData, CityData> initialState) {
		Tile[] tiles = initialState.getWorld().getTiles();
		int total = TileType.values().length*ZoneType.values().length;
		double[][] input = new double[total][INPUT_LAYER_SIZE];
		double[][] output = new double[total][OUTPUT_LAYER_SIZE];
		
		double[] src = new double[]{0, Rules.MAX};
		double[] target = new double[]{0.0, 1.0};
		/*
		for(int i=0; i<tiles.length; i++){
			Tile t = tiles[i];
			ZoneType z = ZoneType.values()[i%ZONETYPES];

			double[] zoneRepr = ModelToVec.getZoneAsVector(z);
			input[i] = Util.appendVectors(getInputAroundTile(initialState.getWorld(), t.getPos()), zoneRepr);
			output[i] = new double[]{Util.mapValue(Rules.getValueForZoneOnTile(t.getType(), z), src, target)};
		}
		*/
		int j=0;
		for(TileType t: TileType.values()){
			double[] tileRepr = ModelToVec.getTileTypeAsVector(t);
			for(ZoneType z: ZoneType.values()){
				double[] zoneAct = ModelToVec.getZoneAsVector(z);
				input[j]=constructSampleInput(tileRepr, zoneAct);
				output[j] = new double[]{Util.mapValue(Rules.getValueForZoneOnTile(t, z), src, target)};
				j++;
			}
		}
	 
		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(output[i]);
			DATASET.add(trainingIn, idealOut);
		}

	}
	
	private double[] constructSampleInput(double[] tileVector, double[] action) {
		double[] inputVec = new double[0];
		for (int cell = 0; cell < 9; cell++) {
			inputVec = Util.appendVectors(inputVec, tileVector);
		}
		inputVec = Util.appendVectors(inputVec, action);
		return inputVec;
	}
	


	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;
		do {
			train.iteration();
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();
		Encog.getInstance().shutdown();
	}

	private double getOutput(double[] input) {
		MLData output = network.compute(new BasicMLData(input));
		return output.getData()[0];
	}

	
	@Override
	public double[] getMapOfValues(Model<UserData, CityData> state, UserData action) {
		ZoneType zoneAction = action.getZoneSelection();
		double[] zoneVector = ModelToVec.getZoneAsVector(zoneAction);
		World w = state.getWorld();
		Tile[] tiles = w.getTiles();
		double[] map = new double[tiles.length];
		Pos2D[] locations = new Pos2D[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = tiles[i].getPos();
			locations[i] = p;
			double[] input = Util.appendVectors(getInputAroundTile(w, p), zoneVector);
			double output = getOutput(input);
			map[i]=output;
		}
		return map;
	}



	@Override
	public void addCase(Model<UserData, CityData> state, Model<UserData, CityData> prev, UserData action, double userRating) {
		Pos2D pos = action.getClickLocation();
		Tile targetTile = prev.getWorld().getTileAt(pos);
		ZoneType zoneAct = action.getZoneSelection();
		double prevScore = Rules.score(prev);
		double currentScore = Rules.score(state);
		double normalizedScoreDiff = ((currentScore - prevScore) / 2.0) + 0.5;
		double[] input = Util.appendVectors(getInputAroundTile(prev.getWorld(), pos), ModelToVec.getZoneAsVector(zoneAct));
		learn(input, new double[] { currentScore });

	}

	private void learn(double[] input, double[] output) {
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		DATASET.add(trainingIn, idealOut);
		trainResilient();
	}
	
	private double[] getInputAroundTile(World w, Pos2D p) {
		Tile[] tiles = getNeighbors(w, p);
		double[] vals = new double[tiles.length * TILE_ATTRIBUTES];
		for (int i = 0; i < tiles.length; i++) {
			double[] tileVec = ModelToVec.getTileAttributesAsVector(tiles[i]);
			int index = i * tileVec.length;
			for (int j = 0; j < tileVec.length; j++) {
				vals[index + j] = tileVec[j];
			}

		}
		return vals;
	}
	
	private Tile[] getNeighbors(World w, Pos2D p) {
		Tile[] tiles = new Tile[9];
		int index = 0;
		for (int i = -1; i<=1; i++) {
			for (int j = -1; j<=1; j++) {
				Pos2D nLoc = new Pos2D(p.getX() + i, p.getY() + j);
				tiles[index] = w.getTileAt(nLoc);
				index++;
			}
		}
		return tiles;
	}





}
