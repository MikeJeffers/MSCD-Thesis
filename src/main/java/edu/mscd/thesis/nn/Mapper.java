package edu.mscd.thesis.nn;

public interface Mapper {
	
	/**
	 * For Learning nets that can create a policy map of values from which to select max value
	 * @return full double vector of full world map where values[i] are in range[0-1.0]
	 */
	public double[] getMapOfValues();

}
