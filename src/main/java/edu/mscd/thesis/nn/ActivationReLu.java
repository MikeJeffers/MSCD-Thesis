package edu.mscd.thesis.nn;

import org.encog.engine.network.activation.ActivationFunction;

/**
 * SoftPlus activation function implementation for Encog's ActivationFunction interface
 * @author Mike
 */
public class ActivationReLu implements ActivationFunction {
	private static final long serialVersionUID = 1L;
	private final double[] params;

	public ActivationReLu() {
		this.params = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			x[i] = Math.max(0, x[i]);
		}
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationReLu();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		if(a>=0){
			return 1.0;
		}else{
			return 0.0;
		}
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
