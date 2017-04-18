package edu.mscd.thesis.util;

public interface WeightVector<T> {
	
	public void setWeightFor(T key, double value);
	
	public double getWeightFor(T key);
	
	public double getSum();
	
	public int getNumWeights();
	
}
