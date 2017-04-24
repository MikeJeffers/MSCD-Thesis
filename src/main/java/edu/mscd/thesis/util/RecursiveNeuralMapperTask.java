package edu.mscd.thesis.util;

import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import edu.mscd.thesis.controller.AiConfig;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;

public class RecursiveNeuralMapperTask extends RecursiveTask<double[]>{
	private static final long serialVersionUID = 1L;
	private BasicNetwork net;
	private AiConfig config;
	private double[] actionVector;
	private int low;
	private int high;
	private World world;
	private int neighborSize;
	private Function<Tile, double[]> modelToVector;
	private int modelSize;

	public RecursiveNeuralMapperTask(BasicNetwork net, AiConfig config, World world, double[] actionVector, int lo, int hi, Function<Tile, double[]> modelToVec, int modelSize) {
		this.low = lo;
		this.high = hi;
		this.net = net;
		this.world = world;
		this.config = config;
		this.actionVector = actionVector;
		this.neighborSize = (int)Math.pow(this.config.getObservationRadius()*2+1, 2);
		this.modelToVector = modelToVec;
		this.modelSize = modelSize;
	}

	@Override
	protected double[] compute() {
		int diff = high - low;
		double[] subMap = new double[diff];
		Tile[] tiles = this.world.getTiles();
		if (diff < Util.MAX_SEQUENTIAL) {
			int j=0;
			for (int i = low; i < high; i++) {
				Pos2D p = tiles[i].getPos();
				double[] input = Util.appendVectors(getInputAroundTile(this.world, p), actionVector);
				double output = getOutput(input);
				subMap[j] += output;
				j++;
			}
			return subMap;
		}
		int mid = low + (high - low) / 2;
		RecursiveNeuralMapperTask left = new RecursiveNeuralMapperTask(net, config, world, actionVector, low, mid, modelToVector, modelSize);
		RecursiveNeuralMapperTask right = new RecursiveNeuralMapperTask(net, config, world, actionVector, mid, high, modelToVector, modelSize);
		left.fork();
		double[]rightMap = right.compute();
		double[]leftMap= left.join();
		return Util.appendVectors(leftMap, rightMap);
	}

	private double getOutput(double[] input) {
		synchronized(this.net){
			MLData output = net.compute(new BasicMLData(input));
			return output.getData()[0];
		}
		
		
	}

	private double[] getInputAroundTile(World w, Pos2D p) {
		Tile[] subSet = getNeighbors(w, p);
		double[] vals = new double[subSet.length * modelSize];
		for (int i = 0; i < subSet.length; i++) {
			double[] modelVec = modelToVector.apply(subSet[i]);
			//double[] zVector = ModelToVec.getTileAsZoneVector(subSet[i]);
			int index = i * modelSize;
			for (int j = 0; j < modelSize; j++) {
				vals[index + j] = modelVec[j];
			}

		}
		return vals;
	}

	private Tile[] getNeighbors(World w, Pos2D p) {
		Tile[] subSet = new Tile[this.neighborSize];
		int r = config.getObservationRadius();
		int index = 0;
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				Pos2D nLoc = new Pos2D(p.getX() + i, p.getY() + j);
				subSet[index] = w.getTileAt(nLoc);
				index++;
			}
		}
		return subSet;
	}

}
