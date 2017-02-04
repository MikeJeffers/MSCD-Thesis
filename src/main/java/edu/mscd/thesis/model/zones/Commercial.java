package edu.mscd.thesis.model.zones;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.bldgs.House;
import edu.mscd.thesis.model.bldgs.Shop;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Commercial extends AbstractZone {

	public Commercial(Pos2D pos, Tile tile) {
		super(pos, tile);
		super.setBuilding(new Shop(pos, tile.getType(), getZoneType(), Density.NONE));
	}

	@Override
	public ZoneType getZoneType() {
		return ZoneType.COMMERICAL;
	}

	@Override
	public void draw(GraphicsContext g) {
		g.setFill(new Color(0, 0, 1.0, 0.5));
		super.draw(g);
	}


}
