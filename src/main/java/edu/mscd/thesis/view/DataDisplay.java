package edu.mscd.thesis.view;


import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.ModelData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.util.WeightVector;


public interface DataDisplay {
	
	public WeightVector<CityProperty> getWeightVector();
	
	public void updateScore(double value, int turn);
	
	public void updateCityData(ModelData data, int turn);
	
	public void updateAIMove(Action action);
	
	public void setTileToolTip(String text);

}
