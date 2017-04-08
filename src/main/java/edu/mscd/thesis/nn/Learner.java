package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;

/**
 * Interface for all subsystems that can be trained online, and given cases to
 * augment their training data, and retrain during live game
 * 
 * @author Mike
 *
 */
public interface Learner {

	/**
	 * Add a case to be learned by the learning system
	 * 
	 * @param prev
	 *            - Previous Model State, snapshot right before Action was
	 *            applied
	 * @param current
	 *            - Model after Action taken (can be any number of time-steps
	 *            afterward!)
	 * @param action
	 *            - UserData package of action attributes
	 * @param userRating
	 *            - A manual rating of the action taken to label case as "good"
	 *            >0.5 or "bad"<0.5
	 */
	public void addCase(Model<UserData, CityData> prev, Model<UserData, CityData> current, UserData action, double userRating);

}
