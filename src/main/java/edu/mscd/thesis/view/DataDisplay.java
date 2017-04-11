package edu.mscd.thesis.view;

import java.util.Map;

import edu.mscd.thesis.controller.CityProperty;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.City;
import javafx.scene.chart.XYChart.Series;

public interface DataDisplay {
	
	public Map<CityProperty, Series<Number, Number>> getCityChartData();
	
	public void setRecentMove(UserData action);
	
	public Series<Number, Number> getPopulationChart();

}
