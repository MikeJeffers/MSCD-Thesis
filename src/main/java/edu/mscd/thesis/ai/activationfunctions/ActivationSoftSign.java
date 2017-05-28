package edu.mscd.thesis.ai.activationfunctions;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.BoundMath;

/**
 * SoftSign activation function implementation for Encog's ActivationFunction interface
 * @author Mike
 */
public class ActivationSoftSign implements ActivationFunction {
	private static final long serialVersionUID = 1L;
	private final double[] params;

	public ActivationSoftSign() {
		this.params = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			x[i] = x[i]/((1+Math.abs(x[i])));
		}
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationSoftSign();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return 1.0/BoundMath.pow(1.0+Math.abs(b), 2);
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
		return "SoftSign";
	}
}
