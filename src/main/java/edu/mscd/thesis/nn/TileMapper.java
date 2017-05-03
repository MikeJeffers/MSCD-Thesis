package edu.mscd.thesis.nn;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ComputeNeuralMapService;
import edu.mscd.thesis.util.MapExecutorService;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.util.WeightVector;

/**
 * MLP Q-learner that outputs Q value for Tile neighborhood(state) and ZoneType
 * (action) Input Layer - NeighborhoodRadiusxTileAttributeVectors +
 * 1xZoneTypeVector Implements Mapper to produce Q-values for entire World-space
 * 
 * @author Mike
 */
public class TileMapper extends AbstractNetwork implements Learner, Mapper {

	private int neighborhoodSize = (int) Math.pow(conf.getObservationRadius() * 2 + 1, 2);
	private static final int ZONETYPES = ZoneType.values().length;
	private static final int TILE_ATTRIBUTES = ModelToVec.getTileAttributesAsVector(null).length;

	private MapExecutorService pool;

	public TileMapper(Model state) {
		this.neighborhoodSize = (int) Math.pow(conf.getObservationRadius() * 2 + 1, 2);
		super.inputLayerSize = ZONETYPES + TILE_ATTRIBUTES * neighborhoodSize;
		initNetwork();
		initTraining();
		train();
		this.pool = new ComputeNeuralMapService(this.network, this.conf, ModelToVec::getTileAttributesAsVector,
				TILE_ATTRIBUTES);
	}

	@Override
	protected void initTraining() {
		super.initTraining();
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
			DATASET.add(trainingIn, idealOut);
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

	@Override
	public double[] getMapOfValues(Model state, Action action) {
		ZoneType zoneAction = action.getZoneType();
		double[] zoneVector = ModelToVec.getZoneAsVector(zoneAction);
		return pool.computeMap(state, zoneVector);
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
		MLData in = new BasicMLData(input);
		MLData out = new BasicMLData(new double[] { normalizedScoreDiff });
		super.learn(new BasicMLDataPair(in, out));
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
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
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
		this.neighborhoodSize = (int) Math.pow(conf.getObservationRadius() * 2 + 1, 2);
		super.inputLayerSize = ZONETYPES + TILE_ATTRIBUTES * neighborhoodSize;
		super.configure(configuration);
		this.pool = new ComputeNeuralMapService(this.network, this.conf, ModelToVec::getTileAttributesAsVector,
				TILE_ATTRIBUTES);

	}

}
