package edu.mscd.thesis.controller;

import edu.mscd.thesis.view.GUI;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.World;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MouseObserver implements EventHandler<MouseEvent> {
	private GameLoop controller;

	public MouseObserver(GameLoop gameController) {
		controller = gameController;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
			World w = controller.getWorld();
			Affine xForm = controller.getGraphics().getTransform();
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
			w.setAllZonesAround(modelCoordinate, GUI.selection, GUI.radiusSelection, GUI.squareSelect);
			controller.step();
		}
	}
}
