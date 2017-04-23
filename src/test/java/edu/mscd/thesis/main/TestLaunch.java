package edu.mscd.thesis.main;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

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
	public void testSomething(){
		
		if(Platform.isSupported(ConditionalFeature.GRAPHICS)){
			System.out.println("start app");
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					Application.launch(Launcher.class, new String[0]);
				}
				
			});
			t.start();
			try {
				t.join(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				Platform.exit();
			}
		}
		
	}
	


}
