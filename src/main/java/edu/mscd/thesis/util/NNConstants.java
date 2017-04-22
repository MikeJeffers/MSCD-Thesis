package edu.mscd.thesis.util;

public class NNConstants {
	
	public static final double INPUT_LAYER_INFLATION = 1.5;
	public static final int MAX_DENSITY = 4;
	public static final int MIN_DENSITY = 1;
	public static final int MAX_DEPTH = 4;
	public static final int MIN_DEPTH= 1;
	public static final int MIN_RADIUS = 0;
	public static final int MAX_RADIUS = 4;
	public static final int MIN_WAIT = 1;
	public static final int MAX_WAIT = 10;
	
	
	public static double getInputLayerSizeFactor(int inputSize, int factor){
		double modifier = inputSize*Util.mapValue(factor, new double[]{MIN_DENSITY,  MAX_DENSITY}, new double[]{0.5, 3.0});
		return modifier;
	}

}
