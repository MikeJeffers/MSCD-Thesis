package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

public class TestActivationSoftPlus extends TestActivation{
	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationSoftPlus();
	}


}
