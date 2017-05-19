package edu.mscd.thesis.nn;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestActivationArcTan.class,
  TestActivationReLu.class,
  TestActivationSoftPlus.class,
  TestActivationSoftSign.class
  
  
})

public class TestNNSuite {
  // the class remains empty,
  // used only as a holder for the above annotations
}