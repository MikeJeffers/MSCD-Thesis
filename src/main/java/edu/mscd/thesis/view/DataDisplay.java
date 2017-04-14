package edu.mscd.thesis.view;

import java.util.Map;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.city.City;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.util.WeightVector;
import javafx.scene.chart.XYChart.Series;

public interface DataDisplay {
	
	public Map<CityProperty, Series<Number, Number>> getCityChartData();
	
	public WeightVector<CityProperty> getWeightVector();
	
	public void updateScore(double value);
	
	public void updateAIMove(UserData action);

}
