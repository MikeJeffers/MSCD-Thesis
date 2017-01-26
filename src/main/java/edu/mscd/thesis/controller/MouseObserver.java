package edu.mscd.thesis.controller;

import edu.mscd.thesis.main.Main;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseObserver implements EventHandler<MouseEvent> {
	private World world;

	public MouseObserver(World w) {
		world = w;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
			double sf = world.getScale();
			double dx = Math.round((event.getSceneX()-(sf/2))/sf)*sf;
			double dy = Math.round((event.getSceneY()-(sf/2))/sf)*sf;
			Tile t = world.getTileAt(new Pos2D(dx, dy));
			t.setMouseOver(true);
			System.out.println(t);
			t.setZone(Main.selection);
		}
	}
}
