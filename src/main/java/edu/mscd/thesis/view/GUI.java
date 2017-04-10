package edu.mscd.thesis.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.CityProperty;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class GUI implements View<UserData> {
	private Collection<Observer<UserData>> observers = new ArrayList<Observer<UserData>>();
	private Renderer<Model<UserData, CityData>> renderer = new ModelRenderer(RenderMode.NORMAL);
	private GraphicsContext gc;
	private Stage stage;

	// User selections on UI elements
	private static UserData selection = new UserData();

	// Chart data
	XYChart.Series<Number, Number> scores = new XYChart.Series<Number, Number>();

	Map<CityProperty, Series<Number, Number>> chartData = new HashMap<CityProperty, Series<Number, Number>>();

	@Override
	public void initView(Stage stage) {
		renderer.changeMode(RenderMode.NORMAL);

		Group root = new Group();

		Canvas canvas = new Canvas(Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
		addClickListenerTo(canvas);
		gc = canvas.getGraphicsContext2D();
		Affine transformMatrix = gc.getTransform();
		transformMatrix.appendScale(Util.SCALE_FACTOR, Util.SCALE_FACTOR);
		gc.setTransform(transformMatrix);

		FlowPane controlPane = new FlowPane();
		Pane zonePane = makeZonePane();
		Pane cameraControls = makeControlButtons(gc);
		Pane renderModeControls = makeRenderModeControls();
		Pane chartPane = makeChartPane();
		Pane scorePane = makeMetricsPane();

		controlPane.setLayoutX(Util.WINDOW_WIDTH);

		controlPane.getChildren().add(zonePane);
		controlPane.getChildren().add(cameraControls);
		controlPane.getChildren().add(renderModeControls);
		controlPane.getChildren().add(chartPane);
		controlPane.getChildren().add(scorePane);

		root.getChildren().add(canvas);
		root.getChildren().add(controlPane);

		Scene mainScene = new Scene(root, Color.WHITE);
		stage.setScene(mainScene);

		this.stage = stage;
		stage.show();
	}
	
	private Pane makeMetricsPane(){
		Pane pane = new GridPane();
		pane.setPadding(new Insets(15, 5, 25, 25));
		int row = 0;
		for(CityProperty prop: CityProperty.values()){
			Label propReadout = new Label(prop.getLabel());
			Label dataReadout = new Label();
			GridPane.setRowIndex(propReadout, row);
			GridPane.setRowIndex(dataReadout, row);
			GridPane.setColumnIndex(propReadout, 0);
			GridPane.setColumnIndex(dataReadout, 1);
			pane.getChildren().addAll(propReadout, dataReadout);
			row++;
			ObservableList<Data<Number,Number>> list = this.chartData.get(prop).getData();
			list.addListener(new ListChangeListener<Data<Number,Number>>(){

				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends Data<Number, Number>> c) {
					while (c.next()) {
						if(c.wasAdded()){
							Data<Number,Number> data = c.getList().get(c.getTo()-1);
							String toDisplay = Double.toString((data.getYValue().doubleValue()*100));
							if(toDisplay.length()>7){
								toDisplay = toDisplay.substring(0, 7);
							}
							
							
							dataReadout.setText(toDisplay);
						}
					}
					
				}
				
			});
		}
		
		
		

		return pane;
	}

	private void addClickListenerTo(Canvas canvas) {
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
	}

	private Pane makeChartPane() {
		Pane chart = new FlowPane();
		XYChart.Series<Number, Number> scores = new XYChart.Series<Number, Number>();
		for (CityProperty prop : CityProperty.values()) {
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			series.setName(prop.getLabel());
			this.chartData.put(prop, series);
		}

		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Turn#");
		yAxis.setLabel("Score");
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("Game Score over turn");

		lineChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					Axis<Number> x = lineChart.getXAxis();
					x.setAutoRanging(true);
				}
			}
		});
		lineChart.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				double scroll = event.getTextDeltaY();
				NumberAxis x = (NumberAxis) lineChart.getXAxis();
				x.setAutoRanging(false);
				if (event.isControlDown()) {
					// scale
					x.setLowerBound(x.getLowerBound() + scroll);
					x.setUpperBound(x.getUpperBound() - scroll);
				} else {
					// translate
					x.setUpperBound(x.getUpperBound() + scroll);
					x.setLowerBound(x.getLowerBound() + scroll);
				}
			}
		});
		scores.setName("Scores");

		for (Entry<CityProperty, Series<Number, Number>> pair : chartData.entrySet()) {
			lineChart.getData().add(pair.getValue());
		}
		chart.getChildren().add(lineChart);
		return chart;
	}

	private Pane makeRenderModeControls() {
		Pane renderModeControls = new FlowPane();
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
		return renderModeControls;

	}

	private Pane makeZonePane() {
		FlowPane zonePanel = new FlowPane();
		addZoneButtonsTo(zonePanel);
		zonePanel.getChildren().add(turnStepButton());
		zonePanel.getChildren().add(makePlayPauseButton());
		zonePanel.getChildren().add(brushShapeButton());
		zonePanel.getChildren().add(radiusSelect());
		return zonePanel;
	}

	private void addZoneButtonsTo(Pane panel) {
		for (ZoneType zType : ZoneType.values()) {
			Button button = new Button(zType.toString());
			button.setOnAction(e -> setSelectedTypeTo(zType));
			panel.getChildren().add(button);
		}
	}

	private Button turnStepButton() {
		Button step = new Button("STEP");
		step.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selection.setTakeStep(true);
				notifyObserver();
				if (Util.SCREENSHOT) {
					Util.takeScreenshot(stage);
				}
			}
		});
		return step;
	}

	private Button makePlayPauseButton() {
		Button playButton = new Button("PLAY");
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selection.setStepMode(!selection.isStepMode());
				notifyObserver();
				if (selection.isStepMode()) {
					playButton.setText(">");
				} else {
					playButton.setText("||");
				}
			}
		});
		return playButton;
	}

	private Button brushShapeButton() {
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
		return brushShape;
	}

	private Spinner<Integer> radiusSelect() {
		Spinner<Integer> radiusSelector = new Spinner<Integer>(0, 10, 1);
		radiusSelector.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				selection.setRadius(newValue);
			}
		});
		return radiusSelector;

	}

	private Pane makeControlButtons(GraphicsContext gc) {
		FlowPane pane = new FlowPane();
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

		ObservableList<Node> nodes = pane.getChildren();
		nodes.add(upButton);
		nodes.add(downButton);
		nodes.add(rightButton);
		nodes.add(leftbutton);
		nodes.add(zoomIn);
		nodes.add(zoomOut);
		nodes.add(resetView);
		return pane;
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
	public void renderView(Model<UserData, CityData> model) {
		this.renderer.draw(model, this.gc);

	}

	@Override
	public void screenShot() {
		if (Util.SCREENSHOT) {
			Util.takeScreenshot(this.stage);
		}
	}

	@Override
	public Series<Number, Number> getDisplayData() {
		return this.scores;
	}

	@Override
	public Map<CityProperty, Series<Number, Number>> getDataStreams() {
		return this.chartData;
	}

}