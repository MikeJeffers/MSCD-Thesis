package edu.mscd.thesis.main;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import edu.mscd.thesis.util.JavaFXThreadingRule;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;

/**
 * MUST BE RUN LAST! Calls Platform.exit() which kills JavaFx processes
 * 
 * Tests full application start and shutdown
 * 
 * @author Mike
 *
 */
public class TestLaunch {
	//@Rule 
	//public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

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

	/**
	 * Integration test - Full boot and close of application
	 * @throws Throwable 
	 */
	@Test(timeout=15000)
	public void testSomething() throws Throwable {
		if(Platform.isSupported(ConditionalFeature.GRAPHICS)){
			Application.launch(Launcher.class, new String[]{"--TEST=true"});
		}
		
	}

}
