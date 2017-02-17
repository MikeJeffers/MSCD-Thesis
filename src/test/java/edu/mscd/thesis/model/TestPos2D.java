package edu.mscd.thesis.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestPos2D {
	

	

	@BeforeClass
	public static void runOnceBeforeClass() {
		

	}

	@AfterClass
	public static void runOnceAfterClass() {
		//
	}

	@Before
	public void runBeforeTestMethod() {
		//
	}

	@After
	public void runAfterTestMethod() {
		//
	}

	@Test
	public void testPosInit() {
		//
		double x = 4.5;
		double y = 2.5;
		Pos2D p = new Pos2D(x, y);
		assertNotNull(p);
		assertTrue(x==p.getX());
		assertTrue(y==p.getY());
		
	}
	

	@Test
	public void testDist() {
		//
		double d = 10;
		Pos2D origin = new Pos2D(0,0);
		Pos2D xNeg = new Pos2D(-d,0);
		Pos2D xPos = new Pos2D(d,0);
		Pos2D yNeg = new Pos2D(0,-d);
		Pos2D yPos = new Pos2D(0,d);
		Pos2D xyPos = new Pos2D(d,d);
		Pos2D xyNeg = new Pos2D(-d,-d);
		assertTrue(origin.distBetween(origin)==0);
		assertTrue(xNeg.distBetween(origin)==origin.distBetween(xNeg));
		assertTrue(xPos.distBetween(origin)==origin.distBetween(xNeg));
		assertTrue(yPos.distBetween(origin)==origin.distBetween(yNeg));
		assertTrue(xyPos.distBetween(origin)==origin.distBetween(xyNeg));
		assertTrue(xNeg.distBetween(origin)==d);
		assertTrue(xPos.distBetween(origin)==d);
		
	}
}
