package edu.mscd.thesis.util;

public class NNConstants {
	
	public static final int MAX_DENSITY = 5;
	public static final int MIN_DENSITY = 1;
	public static final int MAX_LAYERS = 5;
	public static final int MIN_LAYERS= 2;
	public static final int MIN_RADIUS = 0;
	public static final int MAX_RADIUS = 4;
	public static final int MIN_WAIT = 1;
	public static final int MAX_WAIT = 20;
	public static final int MIN_EPOCHS = 50;
	public static final int MAX_EPOCHS = 1000;
	public static final double MIN_ERROR_RATE = 0.001;
	public static final double MAX_ERROR_RATE = 0.1;
	
	
	public static int getNeuronCountByFactor(int inputSize, int factor){
		int neuronCount = (int)Math.round(inputSize*Util.mapValue(factor, new double[]{MIN_DENSITY,  MAX_DENSITY}, new double[]{0.75, 2.5}));
		return neuronCount;
	}

}
