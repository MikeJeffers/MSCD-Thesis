package edu.mscd.thesis.view;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/**
 * View is observed by Observer
 * Events on View are recorded into <T>Data and sent to Observers registered to View
 * @author Mike
 *
 * @param <T> Type of data to notify Observers with
 */
public interface View<T> extends Observable<T>, DataDisplay{
	public void initView(Stage stage);

	public void renderView(Model<UserData, CityData> model, Double[] map);
	
	public void screenShot();

}
