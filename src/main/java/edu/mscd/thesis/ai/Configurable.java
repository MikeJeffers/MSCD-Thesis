package edu.mscd.thesis.ai;

import edu.mscd.thesis.view.viewdata.AiConfig;

/**
 * Interface that indicates a neural network that can be reinitialized
 * programmatically with new Settings via ConfigData
 * 
 * @author Mike
 *
 */
public interface Configurable {

	/**
	 * AiConfiguration settings package, not all attributes may apply to all
	 * types of networks
	 * 
	 * @param configuration
	 *            - AiConfiguration object
	 */
	public void configure(AiConfig configuration);

}
