package edu.mscd.thesis.ai.activationfunctions;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.BoundMath;
import org.encog.mathutil.BoundNumbers;

/**
 * ArcTan activation function implementation for Encog's ActivationFunction interface
 * @author Mike
 */
public class ActivationArcTan implements ActivationFunction {
	private static final long serialVersionUID = 1L;
	private final double[] params;

	public ActivationArcTan() {
		this.params = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void activationFunction(final double[] x, final int start,
			final int size) {
		for (int i = start; i < start + size; i++) {
			x[i] = Math.atan(BoundNumbers.bound((Math.PI/2.0)*x[i]));
		}
	}

	/**
	 * @return The object cloned;
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationArcTan();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double derivativeFunction(final double b, final double a) {
		return 1.0/(1.0+BoundMath.pow((Math.PI/2.0)*b, 2));
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
		return "ArcTan";
	}
}
