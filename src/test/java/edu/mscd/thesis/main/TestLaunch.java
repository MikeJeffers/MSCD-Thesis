package edu.mscd.thesis.main;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Application;
import javafx.application.Platform;


/**
 * MUST BE RUN LAST! 
 * Calls Platform.exit() which kills JavaFx processes
 * 
 * Tests full application start and shutdown
 * @author Mike
 *
 */
public class TestLaunch {


	@BeforeClass
	public static void runOnceBeforeClass() {
		

	}

	@AfterClass
	public static void runOnceAfterClass() {
		//
	}

	@Before
	public void runBeforeTestMethod() {

		
	}

	@After
	public void runAfterTestMethod() {
		//
	}

	@Test
	public void testSomething() {

		Application.launch(Launcher.class,new String[]{"--TEST=true"});
		
	}

	
}
