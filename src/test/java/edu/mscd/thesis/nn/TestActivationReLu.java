package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

public class TestActivationReLu extends TestActivation{
	
	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationReLu();
	}


}
