package edu.mscd.thesis.controller;

import edu.mscd.thesis.main.Main;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
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
			World w  = controller.getWorld();
			Affine xForm = controller.getGraphics().getTransform();
	
			Point2D pt = new Point2D(event.getSceneX(), event.getSceneY());
			try {
				pt = xForm.inverseTransform(pt);
			} catch (NonInvertibleTransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			System.out.println("Click At:"+event.getSceneX()+"x, "+event.getSceneY()+"y");
			double dx = pt.getX();
			double dy = pt.getY();
			System.out.println("Click At:"+dx+"x, "+dy+"y");
			Pos2D modelCoordinate = new Pos2D(dx, dy);
			Tile t = w.getTileAt(modelCoordinate);
			if(t==null){
				System.err.println("No Tile found at: click pt="+pt.toString()+" model(xy):"+modelCoordinate);
				return;
			}
			t.setMouseOver(true);
			System.out.println(t);
			t.setZone(Main.selection);
			controller.step();
			
		}
	}
}
