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
		System.out.println("Event fired!");
		if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
			double sf = Main.SCALE_FACTOR;
			double dx = (event.getSceneX() - (sf / 2)) / sf;
			double dy = (event.getSceneY() - (sf / 2)) / sf;
			int x = (int) Math.round(dx);
			int y = (int) Math.round(dy);
			System.out.println("x" + x + " y" + y);
			Tile t = world.getTileAt(new Pos2D(x, y));
			System.out.println(t);
		}

	}

}
