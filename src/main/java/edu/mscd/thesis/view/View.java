package edu.mscd.thesis.view;

import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.model.Model;
import javafx.stage.Stage;

public interface View {
	public void initView(Stage stage);
	public void attachObserver(Observer obs);
	public void detachObserver(Observer obs);
	public void notifyObserver();
	public void renderView(Model model);

}
