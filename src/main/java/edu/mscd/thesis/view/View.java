package edu.mscd.thesis.view;

import edu.mscd.thesis.controller.Observable;
import edu.mscd.thesis.controller.ViewData;
import edu.mscd.thesis.model.Model;
import javafx.stage.Stage;

/**
 * View is observed by Observer
 * Events on View are recorded into <T>Data and sent to Observers registered to View
 * @author Mike
 */
public interface View extends Observable<ViewData>, DataDisplay{
	public void initView(Stage stage);

	public void renderView(Model model);
	
	public void screenShot();

}
