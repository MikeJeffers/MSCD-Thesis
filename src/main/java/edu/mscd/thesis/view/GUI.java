package edu.mscd.thesis.view;

import java.util.ArrayList;
import java.util.Collection;

import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class GUI implements View<UserData> {
	private Collection<Observer<UserData>> observers = new ArrayList<Observer<UserData>>();
	private Renderer<Model> renderer = new ModelRenderer(RenderMode.NORMAL);
	private GraphicsContext gc;
	private Stage stage;
	
	// User selections on UI elements
	private static UserData selection = new UserData();

	@Override
	public void initView(Stage stage) {
		renderer.changeMode(RenderMode.NORMAL);

		Group root = new Group();

		Canvas canvas = new Canvas(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
		gc = canvas.getGraphicsContext2D();

		FlowPane controlPane = new FlowPane();
		FlowPane zonePanel = new FlowPane();
		for (ZoneType zType : ZoneType.values()) {
			Button button = new Button(zType.toString());
			button.setOnAction(e -> setSelectedTypeTo(zType));
			zonePanel.getChildren().add(button);
		}
		Button step = new Button("STEP");
		step.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selection.setTakeStep(true);
				notifyObserver();
				if(Util.SCREENSHOT){
					Util.takeScreenshot(stage);
				}	
			}
		});
		zonePanel.getChildren().add(step);
		Button brushShape = new Button("Circle");
		brushShape.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selection.setSquare(!selection.isSquare());
				if (selection.isSquare()) {
					brushShape.setText("Square");
				} else {
					brushShape.setText("Circle");
				}

			}
		});
		zonePanel.getChildren().add(brushShape);

		Spinner<Integer> radiusSelector = new Spinner<Integer>(0, 10, 1);
		radiusSelector.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				selection.setRadius(newValue);
			}
		});
		zonePanel.getChildren().add(radiusSelector);

		FlowPane cameraControls = new FlowPane();
		makeControlButtons(cameraControls, gc);

		FlowPane renderModeControls = new FlowPane();
		ComboBox<RenderMode> combo = new ComboBox<RenderMode>();
		combo.getItems().setAll(RenderMode.values());
		combo.setValue(RenderMode.NORMAL);
		combo.valueProperty().addListener(new ChangeListener<RenderMode>() {
			@Override
			public void changed(ObservableValue<? extends RenderMode> observable, RenderMode oldValue,
					RenderMode newValue) {
				renderer.changeMode(newValue);
				redraw(gc);
			}
		});
		renderModeControls.getChildren().add(combo);

		controlPane.setLayoutX(Util.WINDOW_WIDTH);

		controlPane.getChildren().add(zonePanel);
		controlPane.getChildren().add(cameraControls);
		controlPane.getChildren().add(renderModeControls);

		root.getChildren().add(canvas);
		root.getChildren().add(controlPane);

		Affine transformMatrix = gc.getTransform();
		transformMatrix.appendScale(Util.SCALE_FACTOR, Util.SCALE_FACTOR);
		gc.setTransform(transformMatrix);

		Scene mainScene = new Scene(root, Color.WHITE);
		stage.setScene(mainScene);
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Affine xForm = gc.getTransform();
				Point2D pt = new Point2D(event.getSceneX(), event.getSceneY());
				try {
					pt = xForm.inverseTransform(pt);
				} catch (NonInvertibleTransformException e) {
					e.printStackTrace();
					return;
				}
				double dx = pt.getX();
				double dy = pt.getY();
				Pos2D modelCoordinate = new Pos2D(dx, dy);
				if (Util.isValidPos2D(modelCoordinate, Rules.WORLD_X, Rules.WORLD_Y)) {
					selection.setTakeStep(true);
					selection.setClickLocation(modelCoordinate);
					notifyObserver();
				}
				if (Util.SCREENSHOT) {
					Util.takeScreenshot(stage);
				}
			}
		});
		this.stage = stage;
		stage.show();
	}

	private void makeControlButtons(Pane panel, GraphicsContext gc) {
		Button upButton = new Button("UP");
		Button downButton = new Button("DOWN");
		Button rightButton = new Button("RIGHT");
		Button leftbutton = new Button("LEFT");
		upButton.setOnAction(e -> translateView(gc, new Pos2D(0, 1)));
		downButton.setOnAction(e -> translateView(gc, new Pos2D(0, -1)));
		rightButton.setOnAction(e -> translateView(gc, new Pos2D(-1, 0)));
		leftbutton.setOnAction(e -> translateView(gc, new Pos2D(1, 0)));

		Button zoomIn = new Button("+");
		Button zoomOut = new Button("-");

		zoomIn.setOnAction(e -> scaleView(gc, 1.2));
		zoomOut.setOnAction(e -> scaleView(gc, 0.8));

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
		redraw(gc);
	}

	private void scaleView(GraphicsContext gc, double scale) {
		redraw(gc);
		Affine matrix = gc.getTransform();
		matrix.prependScale(scale, scale);
		gc.setTransform(matrix);
		redraw(gc);
	}

	private void resetMatrix(GraphicsContext gc) {
		redraw(gc);
		Affine identityMatrix = new Affine();
		identityMatrix.appendScale(Util.SCALE_FACTOR, Util.SCALE_FACTOR);
		gc.setTransform(identityMatrix);
		redraw(gc);
	}

	private void redraw(GraphicsContext gc) {
		gc.setFill(Color.DARKGRAY);
		gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		selection.setDrawFlag(true);
		selection.setTakeStep(false);
		notifyObserver();

	}

	private void setSelectedTypeTo(ZoneType zType) {
		System.out.println("Selection changed to:" + zType.toString());
		selection.setZoneSelection(zType);
	}

	@Override
	public void attachObserver(Observer<UserData> obs) {
		this.observers.add(obs);

	}

	@Override
	public void detachObserver(Observer<UserData> obs) {
		this.observers.remove(obs);

	}

	@Override
	public void notifyObserver() {
		for (Observer<UserData> o : observers) {
			o.notifyNewData(selection);
		}

	}

	@Override
	public void renderView(Model model) {
		this.renderer.draw(model, this.gc);

	}

	@Override
	public void screenShot() {
		if (Util.SCREENSHOT) {
			Util.takeScreenshot(this.stage);
		}
	}

}