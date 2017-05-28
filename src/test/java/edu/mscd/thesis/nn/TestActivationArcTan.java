package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

import edu.mscd.thesis.ai.activationfunctions.ActivationArcTan;

public class TestActivationArcTan extends TestActivation {

	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationArcTan();
	}

}
