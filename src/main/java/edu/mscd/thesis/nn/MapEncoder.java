package edu.mscd.thesis.nn;

import java.util.Arrays;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;

public class MapEncoder {
	public static final BasicNetwork network = new BasicNetwork();
	public static final MLDataSet DATASET = new BasicMLDataSet();
	private double[] map;
	private Model<UserData, CityData> state;

	public MapEncoder(Model<UserData, CityData> state) {
		this.state = ModelStripper.reducedCopy(state);
		initNetwork();
		learn(this.state);
	}

	private void initNetwork() {
		network.addLayer(new BasicLayer(null, true, 27));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 41));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 16));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9));
		network.getStructure().finalizeStructure();
		network.reset();
	}
	
	private void initTraining(){
		double [] input = new double[27];
		input[0] = 1.0;
		input[0] = 1.0;
		input[0] = 1.0;
		
	}

	public void learn(Model<UserData, CityData> state) {
		Tile[] tiles = state.getWorld().getTiles();

		double[][] inputData = new double[tiles.length][27];
		double[][] idealData = new double[tiles.length][9];
		for (int i = 0; i < inputData.length; i++) {
			inputData[i] = this.getInputAroundTile(state.getWorld(), tiles[i].getPos());
			idealData[i] = this.getOutputAroundTile(state.getWorld(), tiles[i].getPos());
		}
		for (int i = 0; i < inputData.length; i++) {
			MLData trainingIn = new BasicMLData(inputData[i]);
			MLData idealOut = new BasicMLData(idealData[i]);
			DATASET.add(trainingIn, idealOut);
		}

		train();
	}
	private void train(){
		ResilientPropagation train = new ResilientPropagation(network, DATASET);
		int epoch = 1;
		do {
			train.iteration();
			epoch++;
		} while (train.getError() > 0.01 && epoch < 50);
		train.finishTraining();

		Encog.getInstance().shutdown();
	}

	public double[] scoreWorldState(Model<UserData, CityData> state) {

		World temp = ModelStripper.reducedCopy(state).getWorld();
		Tile[] tiles = temp.getTiles();
		map = new double[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			double[] outArray = getOutput(this.getInputAroundTile(temp, tiles[i].getPos()));
			Tile[] neighbors = getNeighbors(temp, tiles[i].getPos());
			for (int j = 0; j < neighbors.length; j++) {
				if (neighbors[j] == null) {
					continue;
				} else {
					int index = Util.<Tile>getIndexOf(neighbors[j], tiles);
					if(index>-1&&index<tiles.length){
						map[index] += outArray[j];
					}
				}
			}

		}
		System.out.println(Arrays.toString(map));
		return map;
	}

	private double[] getOutput(double[] input) {
		MLData output = network.compute(new BasicMLData(input));
		return output.getData();
	}

	private double[] getInputAroundTile(World w, Pos2D p) {
		Tile[] tiles = getNeighbors(w, p);
		double[] vals = new double[tiles.length * 3];
		for (int i = 0; i < tiles.length; i++) {
			if(tiles[i]!=null){
				vals[i + tiles.length*0] = tiles[i].getZoneValue()/Rules.MAX;
				vals[i + tiles.length*1] = tiles[i].getCurrentLandValue()/Rules.MAX;
				vals[i + tiles.length*2] = tiles[i].getPollution()/Rules.MAX;
			}else{
				vals[i + tiles.length*0] = 0;
				vals[i + tiles.length*1] = 0;
				vals[i + tiles.length*2] = 0;
			}
			
		}
		return vals;
	}

	private double[] getOutputAroundTile(World w, Pos2D p) {
		Tile[] tiles = getNeighbors(w, p);
		double[] vals = new double[tiles.length];
		for (int i = 0; i < tiles.length; i += 2) {
			vals[i] = Rules.score(tiles[i]);
		}
		return vals;
	}

	private Tile[] getNeighbors(World w, Pos2D p) {
		Tile[] tiles = new Tile[9];
		int index = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				Pos2D nLoc = new Pos2D(p.getX() + i, p.getY() + j);
				tiles[index] = w.getTileAt(nLoc);
				index++;
			}
		}
		return tiles;
	}

}
