package edu.mscd.thesis.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


public class TestUtil {


	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	@BeforeClass
	public static void runOnceBeforeClass() {

	}

	@AfterClass
	public static void runOnceAfterClass() {

	}

	@Before
	public void runBeforeTestMethod() {

	}

	@After
	public void runAfterTestMethod() {
		//
	}

	@Test
	public void testAppendVectors() {
		double[] A = new double[]{1};
		double[] B = new double[]{2};
		double[] C = Util.appendVectors(A, B);
		assertEquals(A.length+B.length, C.length);
		assertTrue(A[0]==C[0]);
		assertTrue(B[0]==C[1]);
		
		A = new double[]{1, 2, 3, 4};
		B = new double[]{5, 6, 7};
		C = Util.appendVectors(A, B);
		assertEquals(A.length+B.length, C.length);
		assertTrue(A[0]==C[0]);
		assertTrue(B[0]==C[A.length]);
		assertTrue(B[B.length-1]==C[C.length-1]);
		
	}

}
