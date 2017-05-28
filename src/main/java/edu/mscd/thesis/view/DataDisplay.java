package edu.mscd.thesis.view;


import edu.mscd.thesis.model.ModelData;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.util.WeightVector;
import edu.mscd.thesis.view.viewdata.Action;


public interface DataDisplay {
	
	public WeightVector<CityProperty> getWeightVector();
	
	public void updateScore(double value, int turn);
	
	public void updateCityData(ModelData data, int turn);
	
	public void updateAIMove(Action action);
	
	public void setTileToolTip(String text);

}
