package edu.mscd.thesis.main;

import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.controller.MouseObserver;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.util.Util;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Main extends Application {
	private World world;
	private static final int WORLD_X = 40;
	private static final int WORLD_Y = 30;
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	public static final double SCALE_FACTOR = Util.getScaleFactor(WORLD_X, WORLD_Y, SCREEN_WIDTH, SCREEN_HEIGHT);

	public static void main(String[] args) {
		Application.launch(args);

	}

	@Override
	public void init() {
		initWorld();
	}

	private void initWorld() {
		this.world = new WorldImpl(WORLD_X, WORLD_Y);

	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		Scene mainScene = new Scene(root);
		stage.setScene(mainScene);
		Canvas canvas = new Canvas(800, 600);
		root.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseObserver(this.world));

		GameLoop timer = new GameLoop(world, gc, SCALE_FACTOR);
		timer.start();
		stage.show();
	}
}
