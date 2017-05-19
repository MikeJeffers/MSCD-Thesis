package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

public class TestActivationSoftSign extends TestActivation{
	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationSoftSign();
	}


}
