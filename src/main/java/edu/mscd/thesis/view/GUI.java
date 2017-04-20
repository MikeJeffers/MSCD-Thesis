package edu.mscd.thesis.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.mscd.thesis.controller.Action;
import edu.mscd.thesis.controller.AiAction;
import edu.mscd.thesis.controller.AiConfigImpl;
import edu.mscd.thesis.controller.AiMode;
import edu.mscd.thesis.controller.GameConfigImpl;
import edu.mscd.thesis.controller.Observer;
import edu.mscd.thesis.controller.UserAction;
import edu.mscd.thesis.controller.ViewData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.city.CityProperty;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.CityDataWeightVector;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import edu.mscd.thesis.util.WeightVector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class GUI implements View {
	private Collection<Observer<ViewData>> observers = new ArrayList<Observer<ViewData>>();
	private ModelRenderer renderer = new ModelRenderer(RenderMode.NORMAL);
	private GraphicsContext gc;
	private Stage stage;

	// User selections on UI elements
	private static UserAction userAct = new UserAction();
	private static Action aiAct = new AiAction();
	private static EventType<DataReceived> dataReceipt = new EventType<>("DataReceived");

	private static GameConfigImpl gameConfig = new GameConfigImpl();
	private static AiConfigImpl aiConfig = new AiConfigImpl();

	private Node aiMoveEventTarget;
	private Node scoreEventTarget;

	private double score;
	private Series<Number, Number> scoreData = new Series<Number, Number>();
	private Map<CityProperty, Series<Number, Number>> chartData = new HashMap<CityProperty, Series<Number, Number>>();

	private WeightVector<CityProperty> weightVectorForNN = new CityDataWeightVector();

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

		GridPane controlPane = new GridPane();
		Pane zonePane = makeZonePane();
		Pane cameraControls = makeControlButtons(gc);
		Pane renderModeControls = makeRenderModeControls();
		Pane chartPane = makeStackedChartPane();
		Pane metricsPane = makeMetricsPane();
		Pane scorePane = makeScorePane();
		Pane weightSliders = makeSliderPane();
		Pane moveReporter = makeAIMoveReport();
		Pane aiModePane = makeAiModePane();

		controlPane.setHgap(5);
		controlPane.setVgap(5);
		controlPane.setPadding(new Insets(5, 5, 5, 25));

		controlPane.setLayoutX(Util.WINDOW_WIDTH);
		controlPane.add(chartPane, 0, 0, 4, 4);
		controlPane.add(zonePane, 0, 5);
		controlPane.add(cameraControls, 0, 6);
		controlPane.add(renderModeControls, 0, 7);
		controlPane.add(metricsPane, 0, 8);
		controlPane.add(scorePane, 0, 9);
		controlPane.add(weightSliders, 0, 10);
		controlPane.add(moveReporter, 0, 11);
		controlPane.add(aiModePane, 1, 5);

		// setGridVisible(controlPane);

		root.getChildren().add(canvas);
		root.getChildren().add(controlPane);

		Scene mainScene = new Scene(root, Color.WHITE);
		stage.setScene(mainScene);

		this.stage = stage;
		stage.show();
	}

	private static void setGridVisible(Node n) {
		if (n instanceof GridPane) {
			GridPane grid = (GridPane) n;
			grid.setGridLinesVisible(true);
			for (Node child : grid.getChildren()) {
				setGridVisible(child);
			}
		}

	}

	private Pane makeAiModePane() {
		Pane aiModePane = new GridPane();
		Label aiModeLabel = new Label("AI Mode: ");
		ComboBox<AiMode> combo = new ComboBox<AiMode>();
		combo.getItems().setAll(AiMode.values());
		combo.setValue(AiMode.ON);
		combo.valueProperty().addListener(new ChangeListener<AiMode>() {
			@Override
			public void changed(ObservableValue<? extends AiMode> observable, AiMode oldValue, AiMode newValue) {
				gameConfig.setAiMode(newValue);
				notifyObserver((ViewData) gameConfig.copy());
			}
		});
		GridPane.setColumnIndex(aiModeLabel, 0);
		GridPane.setRowIndex(aiModeLabel, 0);
		GridPane.setColumnIndex(combo, 1);
		GridPane.setRowIndex(combo, 0);
		aiModePane.getChildren().add(aiModeLabel);
		aiModePane.getChildren().add(combo);
		return aiModePane;
	}

	private Pane makeAIMoveReport() {
		Pane pane = new GridPane();
		Label aiMoveLabel = new Label("AI Move:  ");
		aiMoveEventTarget = pane;
		Label aiMove = new Label("...AI is thinking...");
		GridPane.setColumnIndex(aiMove, 1);
		GridPane.setRowIndex(aiMove, 0);
		GridPane.setConstraints(aiMoveLabel, 0, 0, 1, 1, HPos.LEFT, VPos.TOP);

		aiMoveEventTarget.addEventHandler(dataReceipt, new EventHandler<DataReceived>() {
			@Override
			public void handle(DataReceived event) {
				aiMove.setText(aiAct.getLabelText());
			}

		});
		pane.getChildren().add(aiMoveLabel);
		pane.getChildren().add(aiMove);
		return pane;
	}

	private Pane makeScorePane() {
		GridPane pane = new GridPane();

		scoreEventTarget = pane;
		Label scoreText = new Label("Score: ");
		Label scoreValue = new Label(Double.toString(score));
		GridPane.setColumnIndex(scoreText, 0);
		GridPane.setRowIndex(scoreText, 0);
		GridPane.setColumnIndex(scoreValue, 1);
		GridPane.setRowIndex(scoreValue, 0);

		scoreEventTarget.addEventHandler(dataReceipt, new EventHandler<DataReceived>() {
			@Override
			public void handle(DataReceived event) {
				String toDisplay = Double.toString(score);
				if (toDisplay.length() > 7) {
					toDisplay = toDisplay.substring(0, 7);
				}
				scoreValue.setText(toDisplay);
			}

		});
		pane.getChildren().addAll(scoreText, scoreValue);
		return pane;
	}

	private Pane makeSliderPane() {
		GridPane pane = new GridPane();

		Label weightLabel = new Label("Game Score Weights: ");
		GridPane.setRowIndex(weightLabel, 0);
		GridPane.setColumnIndex(weightLabel, 0);
		pane.getChildren().add(weightLabel);
		int row = 1;
		for (CityProperty prop : CityProperty.values()) {
			Label propReadout = new Label(prop.getLabel());
			Label dataReadout = new Label("0.5");
			Slider slider = new Slider(0.0, 1.0, 0.5);
			this.weightVectorForNN.setWeightFor(prop, 0.5);
			slider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					weightVectorForNN.setWeightFor(prop, newValue.doubleValue());
					String toDisplay = Double.toString(newValue.doubleValue());
					if (toDisplay.length() > 7) {
						toDisplay = toDisplay.substring(0, 7);
					}
					dataReadout.setText(toDisplay);
				}
			});
			GridPane.setRowIndex(propReadout, row);
			GridPane.setRowIndex(dataReadout, row);
			GridPane.setRowIndex(slider, row);
			GridPane.setColumnIndex(propReadout, 0);
			GridPane.setColumnIndex(slider, 1);
			GridPane.setColumnIndex(dataReadout, 2);
			pane.getChildren().addAll(propReadout, dataReadout, slider);
			row++;

		}
		return pane;
	}

	private Pane makeMetricsPane() {
		GridPane pane = new GridPane();

		Label metricsLabel = new Label("Data Readout: ");
		GridPane.setRowIndex(metricsLabel, 0);
		GridPane.setColumnIndex(metricsLabel, 0);
		pane.getChildren().add(metricsLabel);
		int row = 1;
		for (CityProperty prop : CityProperty.values()) {
			Label propReadout = new Label(prop.getLabel());
			Label dataReadout = new Label();
			double displayMultipler = prop.getMultiplier();
			GridPane.setRowIndex(propReadout, row);
			GridPane.setRowIndex(dataReadout, row);
			GridPane.setColumnIndex(propReadout, 0);
			GridPane.setColumnIndex(dataReadout, 1);
			pane.getChildren().addAll(propReadout, dataReadout);
			row++;
			ObservableList<Data<Number, Number>> list = this.chartData.get(prop).getData();
			list.addListener(new ListChangeListener<Data<Number, Number>>() {

				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends Data<Number, Number>> c) {
					while (c.next()) {
						if (c.wasAdded()) {
							Data<Number, Number> data = c.getList().get(c.getTo() - 1);
							String toDisplay = Double.toString((data.getYValue().doubleValue() * displayMultipler));
							if (toDisplay.length() > 7) {
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
					userAct.setTarget(modelCoordinate);
					userAct.setMove(true);
					notifyObserver((ViewData) userAct.copy());
				}
				if (Util.SCREENSHOT) {
					Util.takeScreenshot(stage);
				}
			}
		});
	}

	private Pane makeStackedChartPane() {
		Pane stackCharts = new StackPane();

		for (CityProperty prop : CityProperty.values()) {
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			series.setName(prop.getLabel());
			this.chartData.put(prop, series);
			NumberAxis xAxis = new NumberAxis();
			NumberAxis yAxis = new NumberAxis();
			xAxis.setLabel("Turn#");
			yAxis.setLabel(prop.getLabel());
			LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
			lineChart.setTitle(prop.getLabel() + " over turn");

			addChartBehaviourListeners(lineChart);

			lineChart.getData().add(chartData.get(prop));

			lineChart.setMaxSize(Util.WINDOW_WIDTH / 3, Util.WINDOW_HEIGHT / 3);
			lineChart.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
			stackCharts.getChildren().add(lineChart);
		}
		// Add scorechart
		scoreData.setName("Score");
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Turn#");
		yAxis.setLabel("Score");
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setTitle("Score by turn");
		addChartBehaviourListeners(lineChart);
		lineChart.getData().add(scoreData);
		lineChart.setMaxSize(Util.WINDOW_WIDTH / 3, Util.WINDOW_HEIGHT / 3);
		lineChart.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		stackCharts.getChildren().add(lineChart);

		stackCharts.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (!event.isControlDown()) {
					ObservableList<Node> children = stackCharts.getChildren();
					if (children.size() > 1) {
						Node topNode = children.get(children.size() - 1);
						topNode.toBack();
					}
				}

			}
		});
		return stackCharts;
	}

	private void addChartBehaviourListeners(LineChart<Number, Number> lineChart) {
		lineChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isControlDown()) {
					if (event.getClickCount() == 2) {
						lineChart.getXAxis().setAutoRanging(true);
						lineChart.getYAxis().setAutoRanging(true);
					}
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
	}

	private Pane makeRenderModeControls() {
		GridPane renderModeControls = new GridPane();

		Label renderModeLabel = new Label("Data Overlays: ");
		GridPane.setColumnIndex(renderModeLabel, 0);
		GridPane.setRowIndex(renderModeLabel, 0);
		renderModeControls.getChildren().add(renderModeLabel);
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
		GridPane.setColumnIndex(combo, 1);
		GridPane.setRowIndex(combo, 0);
		renderModeControls.getChildren().add(combo);
		return renderModeControls;

	}

	private Pane makeZonePane() {
		GridPane zonePanel = new GridPane();
		Label zoneLabel = new Label("Zones: ");
		Label brushLabel = new Label("Brush: ");
		Label radiusLabel = new Label("Radius: ");
		Label turnLabel = new Label("Turn control: ");
		Pane zonePane = makeZoneButtonPane();
		GridPane combine = new GridPane();
		Button step = turnStepButton();
		Button playButton = makePlayPauseButton();
		combine.add(step, 0, 0);
		combine.add(playButton, 1, 0);
		Pane brushPane = brushShapeButton();
		Pane radiusPane = radiusSelect();

		GridPane.setConstraints(zoneLabel, 0, 0, 1, 1, HPos.LEFT, VPos.BASELINE);
		GridPane.setConstraints(zonePane, 1, 0, 1, 1, HPos.RIGHT, VPos.BASELINE);
		GridPane.setConstraints(turnLabel, 0, 1, 1, 1, HPos.LEFT, VPos.BASELINE);
		GridPane.setConstraints(combine, 1, 1, 1, 1, HPos.RIGHT, VPos.BASELINE);
		GridPane.setConstraints(brushLabel, 0, 2, 1, 1, HPos.LEFT, VPos.BASELINE);
		GridPane.setConstraints(brushPane, 1, 2, 1, 1, HPos.RIGHT, VPos.BASELINE);
		GridPane.setConstraints(radiusLabel, 0, 3, 1, 1, HPos.LEFT, VPos.BASELINE);
		GridPane.setConstraints(radiusPane, 1, 3, 1, 1, HPos.RIGHT, VPos.BASELINE);

		zonePanel.getChildren().addAll(zoneLabel, zonePane, turnLabel, combine, brushLabel, brushPane, radiusLabel,
				radiusPane);
		return zonePanel;
	}

	private Pane makeZoneButtonPane() {
		Pane zonePane = new GridPane();
		int col = 0;
		Button[] theButtons = new Button[ZoneType.values().length];
		for (ZoneType zType : ZoneType.values()) {
			Button button = new Button(zType.toString());
			theButtons[zType.ordinal()] = button;
			button.setTooltip(new Tooltip("Set Zone Selection to: " + zType.name()));
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					userAct.setZoneType(zType);
					userAct.setMove(false);
					notifyObserver((ViewData) userAct.copy());
					button.setDisable(true);
					for (int i = 0; i < theButtons.length; i++) {
						if (theButtons[i] != null && !theButtons[i].equals(button)) {
							theButtons[i].setDisable(false);
						}
					}
				}
			});
			GridPane.setColumnIndex(button, col);
			GridPane.setRowIndex(button, 0);
			zonePane.getChildren().add(button);
			col++;
		}
		return zonePane;
	}

	private Button turnStepButton() {
		Button step = new Button("STEP");
		step.setTooltip(new Tooltip("Press to manually step-through turns while paused"));
		step.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (gameConfig.isPaused()) {
					gameConfig.setStep(true);
					notifyObserver((ViewData) gameConfig.copy());
					if (Util.SCREENSHOT) {
						Util.takeScreenshot(stage);
					}
					gameConfig.setStep(false);
				}
			}
		});
		return step;
	}

	private Button makePlayPauseButton() {
		Button playButton = new Button("PLAY");
		playButton.setTooltip(new Tooltip("Press to play/pause the game"));
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gameConfig.setPaused(!gameConfig.isPaused());
				if (gameConfig.isPaused()) {
					playButton.setText("PLAY");
				} else {
					playButton.setText("PAUSE");
				}
				notifyObserver((ViewData) gameConfig.copy());
			}
		});
		return playButton;
	}

	private Pane brushShapeButton() {
		GridPane brushPane = new GridPane();
		Button brushShape = new Button("Circle");
		brushShape.setTooltip(new Tooltip("Click to change Brush Shape!"));
		brushShape.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				userAct.setSquare(!userAct.isSquare());
				if (userAct.isSquare()) {
					brushShape.setText("Square");
				} else {
					brushShape.setText("Circle");
				}
			}
		});
		brushPane.add(brushShape, 0, 0);
		return brushPane;
	}

	private Pane radiusSelect() {
		GridPane radiusSelectPane = new GridPane();
		Spinner<Integer> radiusSelector = new Spinner<Integer>(0, 10, 1);
		radiusSelector.setTooltip(new Tooltip("Sets size of Brush"));
		radiusSelector.setMaxSize(100, 25);
		radiusSelector.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				userAct.setRadius(newValue);
			}
		});
		radiusSelectPane.add(radiusSelector, 0, 0);
		return radiusSelectPane;

	}

	private Pane makeControlButtons(GraphicsContext gc) {
		GridPane pane = new GridPane();
		Button upButton = new Button(" UP ");
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

		GridPane.setHalignment(zoomOut, HPos.LEFT);
		GridPane.setHalignment(zoomIn, HPos.RIGHT);
		GridPane.setHalignment(upButton, HPos.CENTER);
		upButton.setPrefWidth(54);
		Label label = new Label("Camera Controls:");
		GridPane.setConstraints(label, 0, 0, 3, 1, HPos.LEFT, VPos.BASELINE);
		pane.add(label, 0, 0);
		pane.add(upButton, 1, 1);
		pane.add(downButton, 1, 2);
		pane.add(rightButton, 2, 2);
		pane.add(leftbutton, 0, 2);
		pane.add(zoomIn, 0, 1);
		pane.add(zoomOut, 2, 1);
		pane.add(resetView, 3, 2);

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
		userAct.setMove(false);
		notifyObserver((ViewData) userAct.copy());
	}

	@Override
	public void attachObserver(Observer<ViewData> obs) {
		this.observers.add(obs);

	}

	@Override
	public void detachObserver(Observer<ViewData> obs) {
		this.observers.remove(obs);

	}

	@Override
	public void notifyObserver(ViewData data) {
		for (Observer<ViewData> o : observers) {
			o.notifyNewData(data);
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

	@Override
	public Map<CityProperty, Series<Number, Number>> getCityChartData() {
		return this.chartData;
	}

	@Override
	public void updateAIMove(Action action) {
		aiAct = action.copy();
		this.aiMoveEventTarget.fireEvent(new DataReceived(dataReceipt));
	}

	@Override
	public WeightVector<CityProperty> getWeightVector() {
		return this.weightVectorForNN;
	}

	@Override
	public void updateScore(double value, int turnCount) {
		this.score = value;
		this.scoreData.getData().add(new Data<Number, Number>(turnCount, value));
		this.scoreEventTarget.fireEvent(new DataReceived(dataReceipt));

	}

}