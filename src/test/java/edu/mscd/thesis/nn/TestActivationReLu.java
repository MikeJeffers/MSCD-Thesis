package edu.mscd.thesis.nn;

import org.junit.BeforeClass;

import edu.mscd.thesis.ai.activationfunctions.ActivationReLu;

public class TestActivationReLu extends TestActivation{
	
	@BeforeClass
	public static void runOnceBeforeClass() {
		act = new ActivationReLu();
	}


}
