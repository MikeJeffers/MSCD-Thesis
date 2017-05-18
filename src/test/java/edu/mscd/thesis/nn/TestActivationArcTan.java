package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

public class TestActivationArcTan extends TestActivation {

	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationArcTan();
	}

}
