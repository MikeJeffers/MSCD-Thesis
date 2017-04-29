package edu.mscd.thesis.nn;

import org.encog.engine.network.activation.ActivationFunction;

/**
 * SoftPlus activation function implementation for Encog's ActivationFunction interface
 * @author Mike
 */
public class ActivationSoftPlus implements ActivationFunction {
	private static final long serialVersionUID = 1L;
	private final double[] params;

	public ActivationSoftPlus() {
		this.params = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			double data = x[i];
			x[i] = Math.log(1.0+Math.pow(Math.E, x[i]));
			if(Double.isNaN(x[i])){
				System.out.println("x["+i+"]="+x[i]+"  from = "+data);
			}
		}
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationSoftPlus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		double data = 1.0/(1.0+Math.pow(Math.E, -a));
		if(Double.isNaN(data)){
			System.out.println(a);
			System.out.println(Math.pow(Math.E, -a));
			System.out.println((1.0+Math.pow(Math.E, -a)));
			System.out.println(data);
		}
		return 1.0/(1.0+Math.pow(Math.E, -a));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String[] getParamNames() {
		final String[] result = {};
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getParams() {
		return this.params;
	}

	/**
	 * @return Return true, TANH has a derivative.
	 */
	@Override
	public final boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParam(final int index, final double value) {
		this.params[index] = value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFactoryCode() {
		return null;
	}

	public String getLabel() {
		return "SoftPlus";
	}
}
