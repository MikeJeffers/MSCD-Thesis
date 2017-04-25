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

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.WorldImpl;


public class TestUtil {

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
	public void testValidPos2D(){
		int yMax = 10;
		int xMax = 15;
		for(int i=0; i<10;i++){
			Pos2D p = new Pos2D(i,i);
			assertTrue(Util.isValidPos2D(p, xMax, yMax));
		}
		assertFalse(Util.isValidPos2D(new Pos2D(-1, 0), xMax, yMax));
		assertFalse(Util.isValidPos2D(new Pos2D(xMax, 0), xMax, yMax));
		assertFalse(Util.isValidPos2D(new Pos2D(0, yMax), xMax, yMax));
		
	}
	
	@Test
	public void testIndexOf() {
		Integer[] A = new Integer[]{0, 1, 2, 3, 4, 5, 6};
		for(int i=0; i<A.length; i++){
			assertTrue(Util.getIndexOf(i, A)==i);
		}
		assertTrue(Util.getIndexOf(-1, A)==-1);
		assertTrue(Util.getIndexOf(-100, A)==-1);
		assertTrue(Util.getIndexOf(83, A)==-1);
		
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
	
	@Test(timeout=15000)
	public void testConcurrentModelCopy() {
		Model m = new WorldImpl(Rules.WORLD_X, Rules.WORLD_Y);
		Thread modelThread = new Thread(m);
		modelThread.start();
		Thread a = new Thread(new Runnable(){
			@Override
			public void run() {
				int trials = 0;
				while(trials<25){
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					assertNotNull(ModelStripper.reducedCopy(m));
					trials++;
				}	
			}
		});
		
		Thread b = new Thread(new Runnable(){
			@Override
			public void run() {
				int trials = 0;
				while(trials<25){
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					assertNotNull(ModelStripper.reducedCopy(m));
					trials++;
				}	
			}
		});
		a.start();
		b.start();
		
		try {
			b.join(5000);
			a.join(5000);
			modelThread.join(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testBounds(){
		assertTrue(2==Util.boundValue(5, 1, 2));
		assertTrue(1==Util.boundValue(-1235, 1, 2));
		assertTrue(1==Util.boundValue(-0.00001, 1, 2));
		assertTrue(0==Util.boundValue(-0.000001, 0, 255));
		assertTrue(255==Util.boundValue(13245235, 1, 255));
		
	}
	
	

}
