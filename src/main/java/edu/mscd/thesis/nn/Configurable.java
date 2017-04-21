package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.AiConfig;

/**
 * Interface that indicates a neural network that can be reinitialized programmatically with new Settings via ConfigData
 * @author Mike
 *
 */
public interface Configurable {
	
	public void configure(AiConfig configuration);

}
