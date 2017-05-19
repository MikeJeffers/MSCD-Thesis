package edu.mscd.thesis.view;

import edu.mscd.thesis.model.bldgs.Building;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.util.Rules;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ZoneRenderer implements Renderer<Zone> {
	private SpriteRenderer buildingRenderer;
	private RenderMode renderMode;

	public ZoneRenderer(RenderMode mode) {
		this.buildingRenderer = new SpriteRenderer(mode);
		this.renderMode = mode;
	}

	@Override
	public void draw(Zone zone, GraphicsContext g) {
		if (renderMode == RenderMode.NORMAL) {
			g.setFill(zone.getZoneType().getColor());
			g.fillRect(zone.getPos().getX(), zone.getPos().getY(), 1, 1);
		} else if (renderMode == RenderMode.GROWTH) {
			double v = zone.getValue() / Rules.MAX;
			double red=1.0-v;
			double green=v;
			double blue = 0;
			if(v>0.5){
				red = 1.0-((v-0.5)*2.0);
				green = 1.0;
			}else{
				red=1.0;
				green = 2.0*v;
			}
			Color landValueColor = new Color(red, green, blue, 1);
			g.setFill(landValueColor);
			g.fillRect(zone.getPos().getX(), zone.getPos().getY(), 1, 1);
		}
		Building b = zone.getBuilding();
		if (b != null) {
			buildingRenderer.draw(b, g);
		}
	}

	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
		this.buildingRenderer.changeMode(mode);
	}

}
