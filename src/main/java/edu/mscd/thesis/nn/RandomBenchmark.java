package edu.mscd.thesis.nn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.AiAction;
import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.controller.AiConfigImpl;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.controller.ViewData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.WeightVector;
import javafx.application.Platform;

public class RandomBenchmark implements AI {
	private volatile boolean isRunning = true;
	private int counter;
	private BlockingQueue<AiConfig> queue = new LinkedBlockingQueue<AiConfig>();

	private AiConfig conf = new AiConfigImpl();
	private Random r = new Random();

	private Collection<Observer<ViewData>> observers = new ArrayList<Observer<ViewData>>();

	private Lock lock;

	public RandomBenchmark(Model state) {
		this.lock = new ReentrantLock();

	}

	@Override
	public Action takeNextAction() {

		Pos2D randomLoc = new Pos2D(r.nextInt(Rules.WORLD_X), r.nextInt(Rules.WORLD_Y));
		ZoneType randomZone = ZoneType.values()[r.nextInt(ZoneType.values().length)];
		int radius = r.nextInt(3);

		AiAction move = new AiAction();
		move.setTarget(randomLoc);
		move.setZoneType(randomZone);
		move.setRadius(radius);
		move.setSquare(false);
		move.setMove(false);
		return move;
	}

	@Override
	public void addCase(Model prev, Model current, Action action, WeightVector<CityProperty> weights) {

	}

	@Override
	public void setState(Model state) {

	}

	@Override
	public double[] getMapOfValues(Model state, Action action) {
		return new double[state.getWorld().getTiles().length];
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
		this.conf = configuration;
		System.out.println("RANDOM BENCHMARK configured");
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
			if (this.counter > conf.getObservationWaitTime()) {
				this.lock.lock();
				try {
					counter = 0;
					Action act = this.takeNextAction();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							notifyObserver(act);
						}
					});
				} finally {
					this.lock.unlock();
				}

			}
		}
	}

	@Override
	public void update(Model state, Action action, WeightVector<CityProperty> weights) {
		this.lock.lock();
		try {
			counter++;
			this.setState(state);
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
		// Does nothing on randombenchmark
	}
}
