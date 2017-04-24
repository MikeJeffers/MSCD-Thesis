package edu.mscd.thesis.util;

import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import org.encog.neural.networks.BasicNetwork;

import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Tile;

public class ComputeNeuralMapService implements MapExecutorService {

	private ForkJoinPool pool;
	private AiConfig config;
	private BasicNetwork network;
	private Function<Tile, double[]> modelToVec;
	private int modelVectorSize;

	public ComputeNeuralMapService(BasicNetwork network, AiConfig conf, Function<Tile, double[]>modelToVector, int modelSize) {
		this.pool = new ForkJoinPool();
		this.config = conf;
		this.network = network;
		this.modelToVec = modelToVector;
		this.modelVectorSize = modelSize;
	}

	@Override
	public double[] computeMap(Model state, double[] actionVec) {
		Tile[] tiles = state.getWorld().getTiles();
		double[] map = pool.invoke(new RecursiveNeuralMapperTask(network, config, state.getWorld(), actionVec, 0, tiles.length, modelToVec, modelVectorSize));
		return map;
	}



}
