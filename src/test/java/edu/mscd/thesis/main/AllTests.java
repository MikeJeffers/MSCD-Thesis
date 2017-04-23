package edu.mscd.thesis.main;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite entry point
 * ALWAYS CALL TestLaunch LAST - Kills javafx platform
 * @author Mike
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  edu.mscd.thesis.model.AllTests.class,
  edu.mscd.thesis.util.AllTests.class
})

public class AllTests {
  // the class remains empty,
  // used only as a holder for the above annotations
}