package edu.mscd.thesis.nn;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;

public interface AI {

	void setWorldState(Model state);

	UserData takeNextAction();

	void addCase(Model state, Model prev, UserData action);

}
