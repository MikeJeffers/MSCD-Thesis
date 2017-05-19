package edu.mscd.thesis.nn;

import static org.junit.Assert.assertTrue;

import org.encog.engine.network.activation.ActivationFunction;
import org.junit.Test;

public abstract class TestActivation {
	protected static ActivationFunction act;
	
	
	@Test
	public void testDerivative(){
		for(double x=0; x<1.0; x+=0.05){
			double[] arr = new double[]{x};
			act.activationFunction(arr, 0, 1);
			double result = act.derivativeFunction(x, arr[0]);
			assertTrue(result<=1.0);
			assertTrue(result>=0.0);
		}
	}
	
	@Test
	public void testActivation() {
		double step = 0.05;
		int size = (int) (1.0/step);
		double[] inputs = new double[size];
		
		for(int i=0; i<size; i++){
			inputs[i] = i*step;
		}
		act.activationFunction(inputs, 0, size);
		for(int i=0; i<size; i++){
			assertTrue(inputs[i]<=2.0);
			assertTrue(inputs[i]>=0.0);
		}
	}

}
