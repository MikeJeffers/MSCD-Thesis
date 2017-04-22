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
import org.junit.Test;

import javafx.application.Application;

/**
 * MUST BE RUN LAST! Calls Platform.exit() which kills JavaFx processes
 * 
 * Tests full application start and shutdown
 * 
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

	/**
	 * Integration test - Full boot and close of application
	 * @throws Throwable 
	 */
	@Test(timeout=15000)
	public void testSomething() throws Throwable {
		ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> future = service.submit(() -> Application.launch(Launcher.class,new String[]{"--TEST=true"}));
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException ex) {
            assertTrue(true);
        } catch (ExecutionException ex) {
            throw ex.getCause();

        }
	}

}
