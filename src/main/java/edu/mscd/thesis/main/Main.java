package edu.mscd.thesis.main;

import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.controller.MouseObserver;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.model.ZoneType;
import edu.mscd.thesis.util.Util;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	private World world;
	private static final int WORLD_X = 40;
	private static final int WORLD_Y = 30;
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	public static final double SCALE_FACTOR = Util.getScaleFactor(WORLD_X, WORLD_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
	public static ZoneType selection = ZoneType.EMPTY;

	public static void main(String[] args) {
		Application.launch(args);

	}

	@Override
	public void init() {
		initWorld();
	}

	private void initWorld() {
		this.world = new WorldImpl(WORLD_X, WORLD_Y, SCALE_FACTOR);

	}

	@Override
	public void start(Stage stage) throws Exception {
		
		Group root = new Group();
		FlowPane pane = new FlowPane();
		Canvas canvas = new Canvas(850, 600);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		GameLoop timer = new GameLoop(world, gc);
		timer.setStepMode(true);
		for(ZoneType zType: ZoneType.values()){
			Button button = new Button(zType.toString());
			button.setOnAction(e->setSelectedTypeTo(zType));
			pane.getChildren().add(button);
		}
		Button step = new Button("STEP");
		step.setOnAction(e->timer.step());
		pane.getChildren().add(step);
		pane.setLayoutX(SCREEN_WIDTH);
		
		Scene mainScene = new Scene(root);
		stage.setScene(mainScene);
		
		root.getChildren().add(canvas);
		root.getChildren().add(pane);
		
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseObserver(this.world));

		
		timer.start();
		stage.show();
	}
	
	private void setSelectedTypeTo(ZoneType zType){
		System.out.println("Selection changed to:"+zType.toString());
		Main.selection = zType;
	}
}
