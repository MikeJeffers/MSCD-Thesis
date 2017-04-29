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
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ComputeNeuralMapService;
import edu.mscd.thesis.util.MapExecutorService;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.NNConstants;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.util.WeightVector;

/**
 * Input layer:[STATE(9xZoneVectors)+Action(ZoneVector)] where ZoneVector is
 * signal[R,C,I,0] where only one input is 1.0 Output is Q-score of (STATE,
 * ACTION) pair
 * 
 * @author Mike
 *
 */
public class ZoneMapper implements Learner, Mapper, Configurable {

	private AiConfig conf = new AiConfigImpl();
	private int neighborhoodSize = (int)Math.pow(conf.getObservationRadius()*2+1, 2);
	private static final int ZONETYPES = ZoneType.values().length;
	private int inputLayerSize = ZONETYPES+ZONETYPES*neighborhoodSize;
	private static final int OUTPUT_LAYER_SIZE = 1;
	private BasicNetwork network = new BasicNetwork();
	private MLDataSet dataSet = new BasicMLDataSet();
	private MapExecutorService pool;


	public ZoneMapper(Model state) {
		initNetwork();
		initTraining();
		trainResilient();
		this.pool = new ComputeNeuralMapService(this.network, this.conf, ModelToVec::getTileAsZoneVector, Util.ZONETYPES);
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
		double[][] input = new double[9][inputLayerSize];
		double[][] output = new double[9][OUTPUT_LAYER_SIZE];
		double[] r = ModelToVec.getZoneAsVector(ZoneType.RESIDENTIAL);
		double[] c = ModelToVec.getZoneAsVector(ZoneType.COMMERICAL);
		double[] indy = ModelToVec.getZoneAsVector(ZoneType.INDUSTRIAL);
		double[] empty = ModelToVec.getZoneAsVector(ZoneType.EMPTY);
		input[0] = constructSampleInput(r, empty);
		output[0] = new double[] { 0 };
		input[1] =  constructSampleInput(c, empty);
		output[1] = new double[] { 0 };
		input[2] =  constructSampleInput(indy, empty);
		output[2] = new double[] { 1 };
		input[3] =  constructSampleInput(empty, empty);
		output[3] = new double[] { 0 };
		input[4] =  constructSampleInput(r, indy);
		output[4] = new double[] { 0.1 };
		input[5] =  constructSampleInput(indy, r);
		output[5] = new double[] { 0.9 };
		input[6] =  constructSampleInput(empty, r);
		output[6] = new double[] { 1 };
		input[7] =  constructSampleInput(empty, c);
		output[7] = new double[] { 1 };
		input[8] =  constructSampleInput(empty, indy);
		output[8] = new double[] { 1 };


		for (int i = 0; i < input.length; i++) {
			MLData trainingIn = new BasicMLData(input[i]);
			MLData idealOut = new BasicMLData(output[i]);
			dataSet.add(trainingIn, idealOut);
		}

	}

	private double[] constructSampleInput(double[] zoneVector, double[] action) {
		double[] inputSet = new double[inputLayerSize];
		for (int cell = 0; cell < this.neighborhoodSize; cell++) {
			for (int v = 0; v < zoneVector.length; v++) {
				inputSet[cell * zoneVector.length + v] = zoneVector[v];
			}
		}
		for (int i = 0; i < action.length; i++) {
			inputSet[inputSet.length - action.length + i] = action[i];
		}
		return inputSet;
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
		double[] input = Util.appendVectors(getInputAroundTile(prev.getWorld(), pos),ModelToVec.getZoneAsVector(zoneAct));
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
		double[] vals = new double[tiles.length * ZONETYPES];
		for (int i = 0; i < tiles.length; i++) {
			double[] zVector = ModelToVec.getTileAsZoneVector(tiles[i]);
			int index = i * ZONETYPES;
			for (int j = 0; j < ZONETYPES; j++) {
				vals[index + j] = zVector[j];
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
		this.inputLayerSize = ZONETYPES+ZONETYPES*neighborhoodSize;
		this.initNetwork();
		this.initTraining();
		this.trainResilient();
		this.pool = new ComputeNeuralMapService(this.network, this.conf, ModelToVec::getTileAsZoneVector, Util.ZONETYPES);
	}

}
