package edu.mscd.thesis.util;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import edu.mscd.thesis.model.Pos2D;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class Util {
	public static final int MAX = 255;
	public static final int GROWTH_THRESHOLD = 100;
	private static DateFormat df = new SimpleDateFormat("yyMMdd_HHmmss");

	public static boolean isValidPos2D(Pos2D p, double xMax, double yMax) {
		if (p == null) {
			return false;
		} else if (xMax < 1 || yMax < 1) {
			return false;
		} else if (p.getX() < 0 || p.getY() < 0) {
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
	
	
	public static void takeScreenshot(Stage stage){
		WritableImage img = stage.getScene().snapshot(null);
		Date date = Calendar.getInstance().getTime();
		String stamp = df.format(date);
		
		File file = new File("screenshots/screen"+stamp+".png");
		
		try {
			//file.createNewFile();
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
			ImageIO.write(renderedImage, "png", file);
		} catch (IOException e) {
			System.err.println("Failed to save screenshot");
			e.printStackTrace();
		}
	}

}
