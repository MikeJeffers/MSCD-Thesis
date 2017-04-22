package edu.mscd.thesis.nn;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.controller.AiConfigImpl;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.NNConstants;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.util.WeightVector;

/**
 * MLP Q-learner that outputs Q value for Tile neighborhood(state) and ZoneType
 * (action) Input Layer - NeighborhoodRadiusxTileAttributeVectors + 1xZoneTypeVector Implements
 * Mapper to produce Q-values for entire World-space
 * 
 * @author Mike
 */
public class TileMapper implements Learner, Mapper, Configurable {

	private AiConfig conf = new AiConfigImpl();
	private int neighborhoodSize = (int)Math.pow(conf.getObservationRadius()*2+1, 2);
	private static final int ZONETYPES = ZoneType.values().length;
	private static final int TILE_ATTRIBUTES = ModelToVec.getTileAttributesAsVector(null).length;
	private static final int OUTPUT_LAYER_SIZE = 1;
	private int inputLayerSize = ZONETYPES + TILE_ATTRIBUTES * neighborhoodSize;
	
	private BasicNetwork network = new BasicNetwork();
	private MLDataSet dataSet = new BasicMLDataSet();

	public TileMapper(Model state) {
		initNetwork();
		initTraining();
		trainResilient();
	}

	private void initNetwork() {
		int firstLayerSize =(int) Math.round(NNConstants.getInputLayerSizeFactor(inputLayerSize, conf.getNeuronDensity()));
		int stepSize = (firstLayerSize-OUTPUT_LAYER_SIZE-1)/conf.getNetworkDepth();
		network.addLayer(new BasicLayer(null, true, inputLayerSize));
		for(int i=0; i<this.conf.getNetworkDepth(); i++){
			network.addLayer(new BasicLayer(conf.getActivationFunc(), true, firstLayerSize-(stepSize*i)));
		}
		network.addLayer(new BasicLayer(conf.getActivationFunc(), false, OUTPUT_LAYER_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
		System.out.println(network.toString());
		for(int i=0; i<network.getLayerCount(); i++){
			System.out.println(network.getLayerNeuronCount(i));
		}
	}

	private void initTraining() {
		dataSet.close();
		dataSet = new BasicMLDataSet();
		int total = TileType.values().length * ZoneType.values().length;
		double[][] input = new double[total][inputLayerSize];
		double[][] output = new double[total][OUTPUT_LAYER_SIZE];

		double[] src = new double[] { 0, Rules.MAX };
		double[] target = new double[] { 0.0, 1.0 };
		int j = 0;
		for (TileType t : TileType.values()) {
			double[] tileRepr = ModelToVec.getTileTypeAsVector(t);
			for (ZoneType z : ZoneType.values()) {
				double[] zoneAct = ModelToVec.getZoneAsVector(z);
				input[j] = constructSampleInput(tileRepr, zoneAct);
				output[j] = new double[] { Util.mapValue(Rules.getValueForZoneOnTile(t, z), src, target) };
				j++;
			}
		}

		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(output[i]);
			dataSet.add(trainingIn, idealOut);
		}

	}

	private double[] constructSampleInput(double[] tileVector, double[] action) {
		double[] inputVec = new double[0];
		for (int cell = 0; cell < this.neighborhoodSize; cell++) {
			inputVec = Util.appendVectors(inputVec, tileVector);
		}
		inputVec = Util.appendVectors(inputVec, action);
		return inputVec;
	}

	private void trainResilient() {
		ResilientPropagation train = new ResilientPropagation(network, dataSet);
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
	public double[] getMapOfValues(Model state, Action action) {
		ZoneType zoneAction = action.getZoneType();
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
			map[i] = output;
		}
		return map;
	}

	@Override
	public void addCase(Model prev, Model current, Action action, WeightVector<CityProperty> weights) {
		Pos2D pos = action.getTarget();
		ZoneType zoneAct = action.getZoneType();
		double prevScore = Rules.score(prev, weights);
		double currentScore = Rules.score(current, weights);
		double normalizedScoreDiff = Util.getNormalizedDifference(currentScore, prevScore);
		double[] input = Util.appendVectors(getInputAroundTile(prev.getWorld(), pos),
				ModelToVec.getZoneAsVector(zoneAct));
		learn(input, new double[] { normalizedScoreDiff });

	}

	private void learn(double[] input, double[] output) {
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		dataSet.add(trainingIn, idealOut);
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
		Tile[] tiles = new Tile[this.neighborhoodSize];
		int r = conf.getObservationRadius();
		int index = 0;
		for (int i = -r; i<=r; i++) {
			for (int j = -r; j<=r; j++) {
				Pos2D nLoc = new Pos2D(p.getX() + i, p.getY() + j);
				tiles[index] = w.getTileAt(nLoc);
				index++;
			}
		}
		return tiles;
	}

	@Override
	public void configure(AiConfig configuration) {
		this.conf = configuration;
		this.neighborhoodSize = (int)Math.pow(conf.getObservationRadius()*2+1, 2);
		this.inputLayerSize = ZONETYPES + TILE_ATTRIBUTES * neighborhoodSize;
		this.initNetwork();
		this.initTraining();
		this.trainResilient();

	}



}
