package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

import edu.mscd.thesis.ai.activationfunctions.ActivationSoftPlus;

public class TestActivationSoftPlus extends TestActivation{
	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationSoftPlus();
	}


}
