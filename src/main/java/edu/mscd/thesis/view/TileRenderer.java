package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.zones.Zone;
import javafx.scene.canvas.GraphicsContext;

public class TileRenderer implements Renderer<Tile>{
	private Renderer<Zone> zoneRenderer;
	
	public TileRenderer(){
		this.zoneRenderer = new ZoneRenderer();
	}


	@Override
	public void draw(Tile tile, GraphicsContext g) {
		g.setFill(tile.getType().getColor());
		g.fillRect(tile.getPos().getX(), tile.getPos().getY(), 1, 1);
		Zone z = tile.getZone();
		if (z != null) {
			zoneRenderer.draw(z, g);
		}
		
	}

}
