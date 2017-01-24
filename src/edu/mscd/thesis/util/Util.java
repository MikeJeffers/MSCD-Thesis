package edu.mscd.thesis.util;

import edu.mscd.thesis.model.Pos2D;

public class Util {

	public static boolean isValidPos2D(Pos2D p, int xMax, int yMax) {
		if (p == null) {
			return false;
		} else if (xMax < 1 || yMax < 1) {
			return false;
		} else {
			return p.getX() < xMax && p.getY() < yMax;
		}

	}

	/**
	 * Returns the minimum uniform scale factor such that @param xToScale
	 * and @param yToScale will scale uniformly to be contained by the @param
	 * xBound and @param xBound
	 * 
	 * @param xToScale
	 *            - int
	 * @param yToScale
	 *            - int
	 * @param xBound
	 *            - int
	 * @param yBound
	 *            - int
	 * @return double Uniform Scale Factor for Containment in bounds
	 */
	public static double getScaleFactor(int xToScale, int yToScale, int xBound, int yBound) {
		double xScale = ((double) (xBound)) / xToScale;
		double yScale = ((double) (yBound)) / yToScale;
		return Math.min(xScale, yScale);
	}

}
