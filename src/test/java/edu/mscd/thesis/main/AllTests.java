package edu.mscd.thesis.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite entry point ALWAYS CALL TestLaunch First to init Javafx environment
 * 
 * @author Mike
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	TestLaunch.class, 
	edu.mscd.thesis.model.TestModelSuite.class,
	edu.mscd.thesis.util.TestUtilSuite.class,
	edu.mscd.thesis.nn.TestNNSuite.class

})

public class AllTests {
	// the class remains empty,
	// used only as a holder for the above annotations
}