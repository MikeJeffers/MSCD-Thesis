package edu.mscd.thesis.model;

import edu.mscd.thesis.model.zones.Zone;
import javafx.scene.canvas.GraphicsContext;

public interface World {
	public void update();

	public void draw(GraphicsContext g);

	Tile getTileAt(Pos2D pos);

	Zone getZoneAt(Pos2D pos);
	
	City getCity();
	
}
