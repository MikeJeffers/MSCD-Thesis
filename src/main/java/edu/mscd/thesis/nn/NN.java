package edu.mscd.thesis.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.AiAction;
import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.controller.AiConfigImpl;
import edu.mscd.thesis.controller.ModelData;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.controller.ViewData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.CityDataWeightVector;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.ModelToVec;
import edu.mscd.thesis.util.NNConstants;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.util.WeightVector;
import javafx.application.Platform;

/**
 * Primary AI component and neural network Contains TileMapper, ZoneMapper, and
 * ZoneDecider MLP Q-learner merges TileZone/maps based on ZoneDecision
 * 
 * @author Mike
 */
public class NN implements AI {
	private int counter;
	private BlockingQueue<AiConfig> queue = new LinkedBlockingQueue<AiConfig>();

	private Model state;
	private Model prev;
	private Action act;
	private WeightVector<CityProperty> weights;
	
	private TileMapper tileMap;
	private ZoneDecider zoneDecider;
	private ZoneMapper zoneMap;

	private AiConfig conf = new AiConfigImpl();
	private Collection<Observer<ViewData>> observers = new ArrayList<Observer<ViewData>>();

	private BasicNetwork network = new BasicNetwork();
	private MLDataSet DATASET = new BasicMLDataSet();
	private int inputLayerSize = 2 + ZoneType.values().length;
	private static final int OUTPUT_LAYER_SIZE = 1;

	public NN(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		this.prev = ModelStripper.reducedCopy(state);

		this.zoneMap = new ZoneMapper(this.state);
		this.tileMap = new TileMapper(this.state);
		this.zoneDecider = new ZoneDecider(this.state);

		this.initNetwork();
		this.initTraining();
		this.trainResilient();
	}

	private void initTraining() {
		double[][] input = new double[4][inputLayerSize];
		double[][] output = new double[4][OUTPUT_LAYER_SIZE];
		int i = 0;
		for (ZoneType zone : ZoneType.values()) {
			double[] modelVec = new double[] { 1.0, 0.0 };
			input[i] = Util.appendVectors(modelVec, ModelToVec.getZoneAsVector(zone));
			output[i] = new double[] { 1.0 };
			i++;
		}

		for (int j = 0; j < input.length; j++) {
			MLData trainingIn = new BasicMLData(input[j]);
			MLData idealOut = new BasicMLData(output[j]);
			DATASET.add(trainingIn, idealOut);
		}
	}

	private void initNetwork() {
		int firstLayerSize = (int) Math
				.round(NNConstants.getInputLayerSizeFactor(inputLayerSize, conf.getNeuronDensity()));
		int stepSize = (firstLayerSize - OUTPUT_LAYER_SIZE - 1) / conf.getNetworkDepth();
		network.addLayer(new BasicLayer(null, true, inputLayerSize));
		for (int i = 0; i < this.conf.getNetworkDepth(); i++) {
			network.addLayer(new BasicLayer(conf.getActivationFunc(), true, firstLayerSize - (stepSize * i)));
		}
		network.addLayer(new BasicLayer(conf.getActivationFunc(), false, OUTPUT_LAYER_SIZE));
		network.getStructure().finalizeStructure();
		network.reset();
		System.out.println(network.toString());
		for (int i = 0; i < network.getLayerCount(); i++) {
			System.out.println(network.getLayerNeuronCount(i));
		}
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

	@Override
	public Action takeNextAction() {
		Action zoneAction = this.zoneDecider.takeNextAction();
		ZoneType zoneType = zoneAction.getZoneType();
		int radius = zoneAction.getRadius();
		Pos2D[] locations = new Pos2D[this.state.getWorld().getTiles().length];
		double[] mapA = this.tileMap.getMapOfValues(this.state, zoneAction);
		double[] mapB = this.zoneMap.getMapOfValues(this.state, zoneAction);
		double[] combined = new double[mapA.length];
		int maxIndex = 0;
		int minIndex = 0;
		double maxScore = -Rules.MAX;
		double minScore = Rules.MAX;
		assert (mapA.length == mapB.length && locations.length == mapA.length);
		double[] zoneVec = ModelToVec.getZoneAsVector(zoneType);
		List<Integer> ties = new ArrayList<Integer>();
		for (int i = 0; i < mapA.length; i++) {
			locations[i] = this.state.getWorld().getTiles()[i].getPos();
			double[] modelMapValues = new double[] { mapA[i], mapB[i] };
			double[] inputVec = Util.appendVectors(modelMapValues, zoneVec);
			MLData input = new BasicMLData(inputVec);
			double value = network.compute(input).getData(0);
			combined[i] = value;
			if (value <= minScore) {
				minScore = value;
				minIndex = i;
			}
			if (value >= maxScore) {
				if (value > maxScore) {
					ties.clear();
				}
				maxScore = value;
				maxIndex = i;
				ties.add(i);
			}
		}
		maxIndex = ties.get((int) Math.random() * ties.size());

		System.out.println(
				"Possible actions based on Mapped Score domain[" + combined[minIndex] + "," + combined[maxIndex] + "]");
		System.out.print("Best move:{");
		System.out.print(locations[maxIndex]);
		System.out.println();
		System.out.print("Worst move:{");
		System.out.print(locations[minIndex]);
		System.out.println();

		System.out.println("ZoneDecider picked:{" + zoneType + "}");
		if (minIndex == maxIndex) {
			System.out.println("AI can not find ideal move to make");
			return null;
		}

		AiAction move = new AiAction();
		move.setTarget(locations[maxIndex]);
		move.setZoneType(zoneType);
		move.setRadius(radius);
		move.setSquare(false);
		move.setMove(false);
		return move;
	}

	@Override
	public void addCase(Model prev, Model current, Action action, WeightVector<CityProperty> weights) {
		if (!Util.isWeightVectorValid(weights) || !Util.isActionValid(action)) {
			return;
		}
		this.zoneDecider.addCase(prev, current, action, weights);
		this.tileMap.addCase(prev, current, action, weights);
		this.zoneMap.addCase(prev, current, action, weights);

		double[] tileValues = this.tileMap.getMapOfValues(prev, action);
		double[] zoneValues = this.tileMap.getMapOfValues(prev, action);

		int index = Util.getIndexOf(prev.getWorld().getTileAt(action.getTarget()), prev.getWorld().getTiles());

		if(index<0){
			return;
		}
		double prevScore = Rules.score(prev, weights);
		double currentScore = Rules.score(state, weights);
		double normalizedScoreDiff = Util.getNormalizedDifference(currentScore, prevScore);
		double[] output = new double[] { normalizedScoreDiff };
		double[] modelVec = new double[] { tileValues[index], zoneValues[index] };
		double[] actionVec = ModelToVec.getZoneAsVector(action.getZoneType());
		double[] input = Util.appendVectors(modelVec, actionVec);
		MLData trainingIn = new BasicMLData(input);
		MLData idealOut = new BasicMLData(output);
		DATASET.add(trainingIn, idealOut);
		this.trainResilient();

	}

	@Override
	public void setState(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		this.zoneDecider.setState(this.state);

	}

	@Override
	public double[] getMapOfValues(Model state, Action action) {
		double[] mapA = this.tileMap.getMapOfValues(state, action);
		double[] mapB = this.zoneMap.getMapOfValues(state, action);
		double[] combined = new double[mapA.length];
		double[] zoneVec = ModelToVec.getZoneAsVector(action.getZoneType());
		for (int i = 0; i < mapA.length; i++) {
			double[] modelMapValues = new double[] { mapA[i], mapB[i] };
			double[] inputVec = Util.appendVectors(modelMapValues, zoneVec);
			MLData input = new BasicMLData(inputVec);
			double value = network.compute(input).getData(0);
			combined[i] = value;
		}
		return combined;
	}

	@Override
	public void configure(AiConfig configuration) {
		try {
			this.queue.put(configuration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	private void processNewConfig(AiConfig configuration){
		System.out.println("CONFIGURING AI SYSTEM....");
		this.conf = configuration;
		this.zoneDecider.configure(configuration);
		this.tileMap.configure(configuration);
		this.zoneMap.configure(configuration);
		this.initNetwork();
		this.initTraining();
		this.trainResilient();
		System.out.println("...CONFIGURATION COMPLETE");
	}

	@Override
	public void attachObserver(Observer<ViewData> obs) {
		this.observers.add(obs);

	}

	@Override
	public void detachObserver(Observer<ViewData> obs) {
		this.observers.remove(obs);

	}

	@Override
	public void notifyObserver(ViewData action) {
		for (Observer<ViewData> obs : this.observers) {
			obs.notifyNewData(action);
		}

	}

	@Override
	public void run() {
		while (true) {
			AiConfig msg;
			while ((msg = queue.poll()) != null) {
				processNewConfig(msg);
			}
			if (this.counter > conf.getObservationWaitTime()) {
				this.addCase(this.prev, this.state, this.act, this.weights);
				counter = 0;
				Action act = this.takeNextAction();
				Platform.runLater(new Runnable() {
		            @Override public void run() {
		            	notifyObserver(act);
		            }
		        });
				this.prev = this.state;
			}
		}

	}

	@Override
	public synchronized void update(Model state, Action action, WeightVector<CityProperty> weights){
		if(counter==0){
			this.prev=ModelStripper.reducedCopy(state);
			this.act = action.copy();
			this.weights = weights;
		}
		this.setState(state);
		counter++;
	}

}
