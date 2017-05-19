package edu.mscd.thesis.nn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.AiAction;
import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.controller.ViewData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.ModelStripper;
import edu.mscd.thesis.util.ModelToVec;
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
public class NN extends AbstractNetwork implements AI {
	private volatile boolean isRunning = true;
	private int counter;
	private BlockingQueue<AiConfig> queue = new LinkedBlockingQueue<AiConfig>();

	private Model state;
	private Model prev;
	private Action act;
	private WeightVector<CityProperty> weights;

	private TileMapper tileMap;
	private ZoneDecider zoneDecider;
	private ZoneMapper zoneMap;

	private Collection<Observer<ViewData>> observers = new ArrayList<Observer<ViewData>>();

	private Lock lock;
	private volatile boolean forceUpdate = false;

	public NN(Model state) {
		this.lock = new ReentrantLock();
		inputLayerSize = 2 + ZoneType.values().length;
		this.state = ModelStripper.reducedCopy(state);
		this.prev = ModelStripper.reducedCopy(state);

		this.zoneMap = new ZoneMapper(this.state);
		this.tileMap = new TileMapper(this.state);
		this.zoneDecider = new ZoneDecider(this.state);

		initNetwork();
		initTraining();
		train();
	}

	@Override
	protected void initTraining() {
		super.initTraining();
		Random r = new Random();
		int limit = 100;
		double[][] input = new double[limit][inputLayerSize];
		double[][] output = new double[limit][OUTPUT_LAYER_SIZE];

		for (int i = 0; i < limit; i++) {
			double tileValue = r.nextDouble();
			double zoneValue = r.nextDouble();
			double[] modelVec = new double[] { tileValue, zoneValue };
			ZoneType zone = ZoneType.values()[r.nextInt(ZoneType.values().length)];
			input[i] = Util.appendVectors(modelVec, ModelToVec.getZoneAsVector(zone));
			output[i] = new double[] { (tileValue + zoneValue) / 2.0 };
		}

		for (int j = 0; j < input.length; j++) {
			MLData trainingIn = new BasicMLData(input[j]);
			MLData idealOut = new BasicMLData(output[j]);
			DATASET.add(trainingIn, idealOut);
		}
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
		System.out.println("Difference:" + (maxScore - minScore));
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
		int index = Util.getIndexOf(prev.getWorld().getTileAt(action.getTarget()), prev.getWorld().getTiles());

		if (index < 0) {
			return;
		}
		this.zoneDecider.addCase(prev, current, action, weights);
		this.tileMap.addCase(prev, current, action, weights);
		this.zoneMap.addCase(prev, current, action, weights);
		System.out.print("Learning on:");
		System.out.print(action);

		double[] tileValues = this.tileMap.getMapOfValues(prev, action);
		double[] zoneValues = this.tileMap.getMapOfValues(prev, action);

		double actionScore = getActionScore(prev, current, action, weights);
		System.out.println(" ");
		System.out.println(" with score " + actionScore);
		System.out.println(" ");
		double[] modelVec = new double[] { tileValues[index], zoneValues[index] };
		double[] actionVec = ModelToVec.getZoneAsVector(action.getZoneType());
		double[] input = Util.appendVectors(modelVec, actionVec);
		MLData in = new BasicMLData(input);
		MLData out = new BasicMLData(new double[] { actionScore });
		super.learn(new BasicMLDataPair(in, out));
	}

	@Override
	public void setState(Model state) {
		this.state = ModelStripper.reducedCopy(state);
		this.zoneDecider.setState(this.state);

	}

	@Override
	public double[] getMapOfValues(Model state, Action action) {
		Model copy = ModelStripper.reducedCopy(state);
		Action actCopy = action.copy();
		double[] mapA = this.tileMap.getMapOfValues(copy, actCopy);
		double[] mapB = this.zoneMap.getMapOfValues(copy, actCopy);
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

	private void processNewConfig(AiConfig configuration) {
		System.out.println("CONFIGURING AI SYSTEM....");
		this.zoneDecider.configure(configuration);
		this.tileMap.configure(configuration);
		this.zoneMap.configure(configuration);
		super.configure(configuration);
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
		while (isRunning) {
			AiConfig msg;
			while ((msg = queue.poll()) != null) {
				this.lock.lock();
				try {
					processNewConfig(msg);
				} finally {
					this.lock.unlock();
				}
			}
			if (this.counter > conf.getObservationWaitTime() || this.forceUpdate) {
				this.lock.lock();
				try {
					this.addCase(this.prev, this.state, this.act, this.weights);
					this.forceUpdate = false;
					if (this.counter > conf.getObservationWaitTime()) {
						counter = 0;
						Action act = this.takeNextAction();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								notifyObserver(act);
							}
						});
						this.prev = this.state;
					}
				} finally {
					this.lock.unlock();
				}
			}
		}
		this.zoneDecider.shutdown();
		this.zoneMap.shutdown();
		this.tileMap.shutdown();
		this.shutdown();
	}

	@Override
	public void update(Model state, Action action, WeightVector<CityProperty> weights) {
		this.lock.lock();
		try {
			if (counter == 0) {
				this.prev = ModelStripper.reducedCopy(state);
				this.act = action.copy();
				this.weights = weights;
			}
			this.setState(state);
			counter++;
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void halt() {
		this.isRunning = false;
	}

	@Override
	public Lock getLock() {
		return this.lock;
	}

	@Override
	public void forceUpdate() {
		this.forceUpdate = true;
	}

}
