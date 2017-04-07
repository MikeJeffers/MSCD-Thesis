package edu.mscd.thesis.view;

import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.model.Model;
import javafx.stage.Stage;

/**
 * View is observed by Observer
 * Events on View are recorded into <T>Data and sent to Observers registered to View
 * @author Mike
 *
 * @param <T> Type of data to notify Observers with
 */
public interface View<T> {
	public void initView(Stage stage);

	public void attachObserver(Observer<T> obs);

	public void detachObserver(Observer<T> obs);

	public void notifyObserver();

	public void renderView(Model model);
	
	public void screenShot();

}
