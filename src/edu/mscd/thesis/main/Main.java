package edu.mscd.thesis.main;

import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.model.World;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class Main extends Application {
	private World world;

	public static void main(String[] args) {
		Application.launch(args);

	}
	
	private void initWorld(){
		//init model
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		Scene mainScene = new Scene(root);
		stage.setScene(mainScene);
		Canvas canvas = new Canvas(800, 600);
		root.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		
		GameLoop timer = new GameLoop(world, gc);
		timer.start();
		stage.show();
	}
}
