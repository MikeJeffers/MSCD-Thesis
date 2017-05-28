package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

import edu.mscd.thesis.ai.activationfunctions.ActivationSoftSign;

public class TestActivationSoftSign extends TestActivation{
	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationSoftSign();
	}


}
