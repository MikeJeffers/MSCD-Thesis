package edu.mscd.thesis.main;

import java.awt.image.RenderedImage;
import java.util.Calendar;
import java.util.Date;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import edu.mscd.thesis.controller.GameLoop;
import edu.mscd.thesis.controller.MouseObserver;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.World;
import edu.mscd.thesis.model.WorldImpl;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Util;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

public class Main extends Application {
	private World world;
	private GameLoop controller;
	private static final int WORLD_X = 20;
	private static final int WORLD_Y = 15;
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	public static final double SCALE_FACTOR = Util.getScaleFactor(WORLD_X, WORLD_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
	public static ZoneType selection = ZoneType.EMPTY;
	public static Image houseImage;

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

		Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		controller = new GameLoop(world, gc);
		controller.setStepMode(true);
		FlowPane controlPane = new FlowPane();
		FlowPane zonePanel = new FlowPane();
		for (ZoneType zType : ZoneType.values()) {
			Button button = new Button(zType.toString());
			button.setOnAction(e -> setSelectedTypeTo(zType));
			zonePanel.getChildren().add(button);
		}
		Button step = new Button("STEP");
		step.setOnAction(e -> controller.step());
		zonePanel.getChildren().add(step);

		FlowPane cameraControls = new FlowPane();
		makeControlButtons(cameraControls, gc);

		controlPane.setLayoutX(SCREEN_WIDTH);

		controlPane.getChildren().add(zonePanel);
		controlPane.getChildren().add(cameraControls);

		root.getChildren().add(canvas);
		root.getChildren().add(controlPane);

		Affine transformMatrix = gc.getTransform();
		transformMatrix.appendScale(SCALE_FACTOR, SCALE_FACTOR);
		gc.setTransform(transformMatrix);

		Scene mainScene = new Scene(root, Color.WHITE);
		stage.setScene(mainScene);

		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new MouseObserver(controller));
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Util.takeScreenshot(stage);
			}
		});

		controller.start();
		controller.step();
		stage.show();
	}

	private void makeControlButtons(Pane panel, GraphicsContext gc) {
		Button upButton = new Button("UP");
		Button downButton = new Button("DOWN");
		Button rightButton = new Button("RIGHT");
		Button leftbutton = new Button("LEFT");
		upButton.setOnAction(e -> translateView(gc, new Pos2D(0, -1)));
		downButton.setOnAction(e -> translateView(gc, new Pos2D(0, 1)));
		rightButton.setOnAction(e -> translateView(gc, new Pos2D(1, 0)));
		leftbutton.setOnAction(e -> translateView(gc, new Pos2D(-1, 0)));

		Button zoomIn = new Button("+");
		Button zoomOut = new Button("-");

		zoomIn.setOnAction(e -> scaleView(gc, 1.1));
		zoomOut.setOnAction(e -> scaleView(gc, 0.9));

		Button resetView = new Button("Reset View");
		resetView.setOnAction(e -> resetMatrix(gc));

		ObservableList<Node> nodes = panel.getChildren();
		nodes.add(upButton);
		nodes.add(downButton);
		nodes.add(rightButton);
		nodes.add(leftbutton);
		nodes.add(zoomIn);
		nodes.add(zoomOut);
		nodes.add(resetView);

	}

	private void translateView(GraphicsContext gc, Pos2D dir) {
		redraw(gc);
		Affine matrix = gc.getTransform();
		matrix.appendTranslation(dir.getX(), dir.getY());
		gc.setTransform(matrix);
		controller.step();
		redraw(gc);
	}

	private void scaleView(GraphicsContext gc, double scale) {
		redraw(gc);
		Affine matrix = gc.getTransform();
		matrix.prependScale(scale, scale);
		gc.setTransform(matrix);
		controller.step();
		redraw(gc);
	}

	private void resetMatrix(GraphicsContext gc) {
		redraw(gc);
		Affine identityMatrix = new Affine();
		identityMatrix.appendScale(SCALE_FACTOR, SCALE_FACTOR);
		gc.setTransform(identityMatrix);
		controller.step();
		redraw(gc);
	}

	private void redraw(GraphicsContext gc) {
		gc.setFill(Color.DARKGRAY);
		gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
	}

	private void setSelectedTypeTo(ZoneType zType) {
		System.out.println("Selection changed to:" + zType.toString());
		Main.selection = zType;
	}

	
}
