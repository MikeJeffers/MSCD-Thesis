package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.model.Model;

/**
 * AI subsystem that generates map of Q-values per each Tile of Model's state
 * 
 * @author Mike
 *
 */
public interface Mapper {

	/**
	 * Perform Q(s[i], a)->q[i] for each tile in Model state, given Action
	 * 
	 * @param state
	 *            - Model state to score
	 * @param action
	 *            - Action to score state with
	 * @return Double array of equal length to tiles.length
	 */
	public double[] getMapOfValues(Model state, Action action);

}
