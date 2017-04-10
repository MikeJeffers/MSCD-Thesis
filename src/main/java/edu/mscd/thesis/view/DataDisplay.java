package edu.mscd.thesis.view;

import java.util.Map;

import edu.mscd.thesis.controller.CityProperty;
import javafx.scene.chart.XYChart.Series;

public interface DataDisplay {
	
	public Map<CityProperty, Series<Number, Number>> getDataStreams();

}