package edu.mscd.thesis.nn;

import org.encog.engine.network.activation.*;

public enum ActivationFunctions {
    //BIPOLAR(new ActivationBiPolar()), 
   // BIPOLAR_STEEPSIGMOID(new ActivationBipolarSteepenedSigmoid()), 
    //CLIPPED_LINEAR(new ActivationClippedLinear()), 
    //COMPETITVE(new ActivationCompetitive()), 
    ELLIOT(new ActivationElliott()), 
    //ELLIOT_SYM(new ActivationElliottSymmetric()), 
    //GAUSSIAN(new ActivationGaussian()), 
    //LINEAR(new ActivationLinear()), 
    LOG(new ActivationLOG()), 
    LOGISTIC(new ActivationLogistic()),
    RAMP(new ActivationRamp(1, 0, 1, 0)), 
    SIGMOID(new ActivationSigmoid()), 
    //SIN(new ActivationSIN()), 
    SOFTMAX(new ActivationSoftMax()), 
    SOFTPLUS(new ActivationSoftPlus()),
    SOFTSIGN(new ActivationSoftSign()),
    SIGMOID_STEEP(new ActivationSteepenedSigmoid()), 
    RELU(new ActivationReLu()),
    //STEP(new ActivationStep(0,0.5,1.0)), 
    TANH(new ActivationTANH()),
	ATAN(new ActivationArcTan());
	
	private ActivationFunction function;
	
	ActivationFunctions(ActivationFunction func){
		this.setFunction(func);
	}

	public ActivationFunction getFunction() {
		return function;
	}

	private void setFunction(ActivationFunction function) {
		this.function = function;
	}

}
