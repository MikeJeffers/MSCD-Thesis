package edu.mscd.thesis.view;

import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.zones.Zone;
import javafx.scene.canvas.GraphicsContext;

public class ZoneRenderer implements Renderer<Zone> {
	private SpriteRenderer buildingRenderer;

	public ZoneRenderer() {
		this.buildingRenderer = new SpriteRenderer();
	}

	@Override
	public void draw(Zone zone, GraphicsContext g) {
		g.setFill(zone.getZoneType().getColor());
		g.fillRect(zone.getPos().getX(), zone.getPos().getY(), 1, 1);
		Building b = zone.getBuilding();
		if (b != null) {
			buildingRenderer.draw(b, g);
		}

	}

}
