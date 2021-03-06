package edu.mscd.thesis.view.viewdata;

import edu.mscd.thesis.util.Util;

public class NNConstants {
	
	public static final int MAX_DENSITY = 12;
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
	public static final int MIN_REG = -10;
	public static final int MAX_REG = -2;
	
	
	public static double getRegularization(int regFactor){
		double bounded = Util.boundValue(regFactor, MIN_REG, MAX_REG);
		return Math.pow(10.0, bounded);
	}
	
	public static int getNeuronCountByFactor(int inputSize, int factor){
		int neuronCount = (int)Math.round(inputSize*Util.mapValue(factor, new double[]{MIN_DENSITY,  MAX_DENSITY}, new double[]{0.25, 3.25}));
		return neuronCount;
	}

}
