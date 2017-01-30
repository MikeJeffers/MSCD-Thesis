package edu.mscd.thesis.controller;

import edu.mscd.thesis.main.Main;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseObserver implements EventHandler<MouseEvent> {
	private GameLoop controller;

	public MouseObserver(GameLoop gameController) {
		controller = gameController;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
			World w  = controller.getWorld();
			double sf = w.getScale();
			double dx = Math.round((event.getSceneX()-(sf/2))/sf)*sf;
			double dy = Math.round((event.getSceneY()-(sf/2))/sf)*sf;
			Tile t = w.getTileAt(new Pos2D(dx, dy));
			t.setMouseOver(true);
			System.out.println(t);
			t.setZone(Main.selection);
			controller.step();
			
		}
	}
}
