package edu.mscd.thesis.util;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Other misc functionality not game-related. Data-cleaning, validation,
 * safety-checking, or utilities.
 * 
 * @author Mike
 */
public class Util {
	/*
	 * TODO this should scale with available cores.. at least its relative to
	 * world size..
	 */
	public static final int MAX_SEQUENTIAL = (Rules.WORLD_X * Rules.WORLD_Y) / 8;
	// GUI constants
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	public static final int CHART_WIDTH=450;
	public static final int CHART_HEIGHT=300;
	public static final long MAX_FRAME_DURATION = 2000000000L;
	public static final int MAX_CHART_DATAPTS = 100;
	public static final double SCALE_FACTOR = Util.getScaleFactor(Rules.WORLD_X, Rules.WORLD_Y, WINDOW_WIDTH,
			WINDOW_HEIGHT);
	public static final boolean SCREENSHOT = false;
	public static final int ZONETYPES = ZoneType.values().length;
	public static final int TILE_ATTRIBUTES = 5;
	
	private static Random random = new Random();
	private static DateFormat df = new SimpleDateFormat("yyMMdd_HHmmss_SSS");
	private static final Date compileTime = new Date();
	

	/**
	 * Get all Tiles in tile array that are within ManhattanDistance of
	 * distance<=radius, including origin REQUIRES: tiles is single dimensional
	 * array that represents 2D matrix of width=cols; height=rows;
	 * 
	 * @param origin
	 *            - Tile from which to search
	 * @param tiles
	 *            - Single dimensional array that contains Origin, that
	 *            represents 2D matrix of width=cols; height=rows;
	 * @param radius
	 *            - Radius of manhattanDistance
	 * @param cols
	 *            - width of 2D matrix
	 * @param rows
	 *            - height of 2D matrix
	 * @return Tiles found within manhattanDistance of radius of origin, else
	 *         will be empty list if radius<0, or origin not in tiles[]
	 */
	public static List<Tile> getNeighborsManhattanDist(Tile origin, Tile[] tiles, int radius, int cols, int rows) {
		int index = Util.getIndexOf(origin, tiles);
		List<Tile> neighbors = new ArrayList<Tile>();
		if (index == -1) {
			return neighbors;
		}

		for (int j = -radius; j <= radius; j++) {
			for (int k = -radius; k <= radius; k++) {
				int indexOfNeighbor = index + (k * cols) + (j);
				if (indexOfNeighbor >= tiles.length || indexOfNeighbor < 0) {
					continue;
				}
				int expectedCol = (index % cols) + j;
				int expectedRow = (int) (index / cols) + k;
				int actualCol = indexOfNeighbor % cols;
				int actualRow = indexOfNeighbor / cols;
				if (actualRow == expectedRow && actualCol == expectedCol) {
					neighbors.add(tiles[indexOfNeighbor]);
				}
			}
		}
		return neighbors;
	}

	/**
	 * Get List of all Tiles, including origin, inside of radius from origin
	 * tile Pos
	 * 
	 * @param origin
	 *            - Tile from which to search
	 * @param tiles
	 *            - Tile[] to search
	 * @param radius
	 *            - radius from origin from which to search and include in
	 *            returned collection
	 * @return List<Tile> where all members are <=radius in distance from origin
	 *         Tile returns empty list if tiles array does not contain origin,
	 *         or radius is <0
	 */
	public static List<Tile> getNeighborsCircularDist(Tile origin, Tile[] tiles, int radius) {
		int index = Util.getIndexOf(origin, tiles);
		List<Tile> neighbors = new ArrayList<Tile>();
		if (index == -1) {
			return neighbors;
		}
		Pos2D originPt = origin.getPos();
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i].getPos().distBetween(originPt) <= radius) {
				neighbors.add(tiles[i]);
			}
		}
		return neighbors;
	}

	/**
	 * Find the index of some object of type T in unsorted array of type T
	 * 
	 * @param obj
	 *            -T object to find by EQUALITY (requires equals method)
	 * @param arr
	 *            - Array of T objects
	 * @return if array is empty or object is not in array, return -1 else
	 *         returns index of obj in array
	 */
	public static <T> int getIndexOf(T obj, T[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(obj)) {
				return i;
			}
		}
		return -1;
	}

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

	public static int getRandomBetween(int minInclusive, int maxExclusive) {
		return minInclusive + Util.random.nextInt(maxExclusive);
	}

	public static double boundValue(double value, double min, double max) {
		return Math.min(max, Math.max(min, value));
	}
	
	
	public static double[] appendVectors(double[] a, double [] b){
		double[] appended = new double[a.length+b.length];
		for(int i=0; i<a.length; i++){
			appended[i]=a[i];
		}
		for(int i=0; i<b.length; i++){
			appended[i+a.length]=b[i];
		}
		return appended;
	}
	
	
	public static double[] mapValues(double[] values, double[] targetDomain){
		double[] scaled = new double[values.length];
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for(int i=0; i<values.length; i++){
			if(values[i]<min){
				min = values[i];
			}
			if(values[i]>max){
				max = values[i];
			}
		}
		double[] src = new double[]{min, max};
		for(int i=0; i<values.length; i++){
			scaled[i] = mapValue(values[i], src, targetDomain);
		}
		return scaled;
	}
	
	/**
	 * Map a given value from a known source range to a new target range, scaling the factor
	 * @param value - value to remap
	 * @param sourceDomain - double pair, where [0]=start, [1]=end of domain AND start<end
	 * @param targetDomain - double pair, where [0]=start, [1]=end of domain AND start<end
	 * @return mapped value
	 */
	public static double mapValue(double value, double[] sourceDomain, double[] targetDomain){
		double temp = value-sourceDomain[0];
		double rangeOfSource = sourceDomain[1]-sourceDomain[0];
		double normalizedValue = temp/rangeOfSource;
		double rangeOfTarget = targetDomain[1]-targetDomain[0];
		double scaledValue = normalizedValue*rangeOfTarget;
		double translatedValue = scaledValue+targetDomain[0];
		return translatedValue;
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
	
	
	public static boolean isActionValid(Action act){
		if(act==null){
			return false;
		}
		return isValidPos2D(act.getTarget(), Rules.WORLD_X, Rules.WORLD_Y);
	}
	
	public static boolean isWeightVectorValid(WeightVector<CityProperty> weights){
		if(weights==null){
			return false;
		}
		return weights.getNumWeights()==CityProperty.values().length;
	}
	
	
	public static double getNormalizedDifference(double a, double b){
		double[] norm = new double[]{0, 1};
		double[]src = new double[]{-1, 1};
		double diff = a-b;
		return mapValue(diff, src, norm);
	}
	
	
	public static void pruneChartData(Series<Number,Number> series){
		int numParts = MAX_CHART_DATAPTS/4;
		ObservableList<Data<Number,Number>> data = series.getData();
		if (data.size() > MAX_CHART_DATAPTS) {
			List<List<Data<Number, Number>>> partitions = new ArrayList<List< Data<Number, Number>>>();
			int minX = data.get(0).getXValue().intValue();
			int maxX = data.get(data.size()-1).getXValue().intValue();
			int xStepSize = (maxX-minX)/numParts;
			
			List<Data<Number,Number>> part = new ArrayList<Data<Number,Number>>();
			for(int i=minX; i<=maxX; i++){
				for(Data<Number,Number>pt: data){
					if(i==pt.getXValue().intValue()){
						part.add(pt);
						break;
					}
				}
				if(i%xStepSize==0 && !part.isEmpty()){
					partitions.add(part);
					part = new ArrayList<Data<Number,Number>>();
				}
			}

			List<Number> keepers = new ArrayList<Number>();
			for (List<Data<Number, Number>> p : partitions) {
				keepers.addAll(getXValueOfMinMax(p));
			}
			Iterator<Data<Number, Number>> it = data.iterator();

			while (it.hasNext()) {
				Data<Number, Number> pt = it.next();
				if (!keepers.contains(pt.getXValue())) {
					it.remove();
				}
			}
		}
		
	}
	
	private static List<Number> getXValueOfMinMax(List<Data<Number, Number>> part) {
		List<Number> pair = new ArrayList<Number>();
		Number maxIndex = 0;
		double maxValue = Double.MIN_VALUE;
		Number minIndex = 0;
		double minValue = Double.MAX_VALUE;
		for (Data<Number, Number> d : part) {
			double val = d.getYValue().doubleValue();
			if (val > maxValue) {
				maxValue = val;
				maxIndex = d.getXValue();
			}
			if (val < minValue) {
				minValue = val;
				minIndex = d.getXValue();
			}
		}
		pair.add(minIndex);
		pair.add(maxIndex);
		return pair;
	}
	

	public static void takeScreenshot(Stage stage) {
		WritableImage img = stage.getScene().snapshot(null);
		Date date = Calendar.getInstance().getTime();
		String stamp = df.format(date);
		String dirString = "screenshots/Take_"+df.format(compileTime);
		System.out.print("Taking Screen @"+stamp+"....");
		File dir = new File(dirString);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File file = new File(dirString+"/screen" + stamp + ".png");

		try {
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(img, null);
			ImageIO.write(renderedImage, "png", file);
			System.out.println("Success!");
		} catch (IOException e) {
			System.out.println("Fail!");
			System.err.println("Failed to save screenshot");
			e.printStackTrace();
		}
	}
	

}
