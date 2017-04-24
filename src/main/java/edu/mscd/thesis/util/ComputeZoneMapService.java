package edu.mscd.thesis.util;

import java.util.concurrent.ForkJoinPool;

import org.encog.neural.networks.BasicNetwork;

import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Tile;

public class ComputeZoneMapService implements MapExecutorService {

	private ForkJoinPool pool;
	private AiConfig config;
	private BasicNetwork network;

	public ComputeZoneMapService(BasicNetwork network, AiConfig conf) {
		this.pool = new ForkJoinPool();
		this.config = conf;
		this.network = network;
	}

	@Override
	public double[] computeMap(Model state, double[] actionVec) {
		Tile[] tiles = state.getWorld().getTiles();
		double[] map = pool.invoke(new RecursiveZoneMapComputer(network, config, state.getWorld(), actionVec, 0, tiles.length));
		return map;
	}



}
