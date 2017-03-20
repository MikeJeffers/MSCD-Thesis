package edu.mscd.thesis.nn;

import java.util.Arrays;

import com.syvys.jaRBM.RBM;
import com.syvys.jaRBM.RBMImpl;
import com.syvys.jaRBM.Layers.SoftmaxLayer;
import com.syvys.jaRBM.RBMLearn.CDStochasticRBMLearner;

public class RBMTest {

	public static void main(String[] args) {
		RBM rbm = new RBMImpl(new SoftmaxLayer(4), new SoftmaxLayer(7));
		System.out.println(rbm.toString());
		CDStochasticRBMLearner.Learn(rbm, new double[][] { { 2, 1, 3, 5 } }, 5);
		System.out.println(rbm.toString());

		double[] hidden = rbm.getHiddenActivitiesFromVisibleData(new double[] { 2, 2, 2, 2 });
		double[] output = rbm.getVisibleActivitiesFromHiddenData(hidden);
		System.out.println(Arrays.toString(output));

		System.exit(0);
	}

}
