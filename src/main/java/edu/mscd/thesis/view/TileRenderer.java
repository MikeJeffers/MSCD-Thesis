package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.util.Rules;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class TileRenderer implements Renderer<Tile> {
	private Renderer<Zone> zoneRenderer;
	private RenderMode renderMode;

	public TileRenderer(RenderMode mode) {
		this.zoneRenderer = new ZoneRenderer(mode);
		this.renderMode = mode;
	}

	@Override
	public void draw(Tile tile, GraphicsContext g) {
		Pos2D tilePos = tile.getPos();

		if (renderMode == RenderMode.NORMAL) {
			g.setFill(tile.getType().getColor());
		} else if (renderMode == RenderMode.POLLUTION) {
			double pollution = tile.getPollution();

			double red = pollution / Rules.MAX;
			double blue = (Rules.MAX - pollution) / Rules.MAX;
			double green = ((Rules.MAX + pollution) / 2) / Rules.MAX;
			double intensity = (pollution) / Rules.MAX;
			Color pollutionColor = new Color(Util.boundValue(red, 0, 1), Util.boundValue(green, 0, 1),
					Util.boundValue(blue, 0, 1), Util.boundValue(intensity, 0, 1));
			g.setFill(pollutionColor);
		} else if (renderMode == RenderMode.LANDVALUE) {
			double landValue = tile.getCurrentLandValue();

			double green = landValue / Rules.MAX;
			Color landValueColor = new Color(0, Util.boundValue(green, 0, 1), 0, 1);
			g.setFill(landValueColor);
		} else if (renderMode == RenderMode.RESOURCE) {
			double materialValue = tile.materialValue();

			double red = materialValue / Rules.MAX;
			red = Util.boundValue(red, 0, 1);
			Color landValueColor = new Color(red, red/2.0, 0, 1);
			g.setFill(landValueColor);
		} else if (renderMode == RenderMode.DENSITY) {
			double level = 0;

			if (tile.getZone().getBuilding() != null) {
				level = tile.getZone().getBuilding().getDensity().getDensityLevel();
			}

			double max = Density.VERYHIGH.getDensityLevel();
			double red = level / max;
			double blue = (level / (max * 2));
			double green = 0.5;
			double alpha = 1;
			Color densityColor = new Color(red, green, blue, alpha);

			g.setFill(densityColor);
		} else if (renderMode == RenderMode.POLICY) {
			double value = tile.getOverlayValue();

			Color col = Color.MAGENTA;
			if (value == 1.0) {
				col = Color.CYAN;
			}
			col = col.deriveColor(1, value, value, value);
			g.setFill(col.brighter());
		}

		g.fillRect(tilePos.getX(), tilePos.getY(), 1, 1);
		Zone z = tile.getZone();
		if (z != null) {
			zoneRenderer.draw(z, g);
		}
		Selection s = tile.getSelection();
		if (s.isSelected()) {
			g.setLineWidth(0.095);
			g.setLineCap(StrokeLineCap.ROUND);
			g.setStroke(s.getType().getColor().brighter());
			g.strokeRect(tilePos.getX(), tilePos.getY(), 1, 1);
		}

	}

	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
		this.zoneRenderer.changeMode(mode);
	}

}
