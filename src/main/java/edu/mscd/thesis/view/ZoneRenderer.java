package edu.mscd.thesis.view;

import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.zones.Zone;
import javafx.scene.canvas.GraphicsContext;

public class ZoneRenderer implements Renderer<Zone> {
	private SpriteRenderer buildingRenderer;
	private RenderMode renderMode;
	public ZoneRenderer(RenderMode mode) {
		this.buildingRenderer = new SpriteRenderer(mode);
		this.renderMode = mode;
	}

	@Override
	public void draw(Zone zone, GraphicsContext g) {
		if(renderMode==RenderMode.NORMAL){
			g.setFill(zone.getZoneType().getColor());
			g.fillRect(zone.getPos().getX(), zone.getPos().getY(), 1, 1);
		}
		Building b = zone.getBuilding();
		if (b != null) {
			buildingRenderer.draw(b, g);
			System.out.println(b.getImage());
		}

	}

	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
		this.buildingRenderer.changeMode(mode);
	}

}
