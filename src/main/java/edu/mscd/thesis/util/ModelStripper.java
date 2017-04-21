package edu.mscd.thesis.util;


import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.WorldReduced;

public class ModelStripper {

	public static Model reducedCopy(Model m) {
		Model copy = new WorldReduced(m.getWorld());
		return copy;
	}


}
