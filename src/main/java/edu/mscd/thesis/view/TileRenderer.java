package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.util.Rules;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class TileRenderer implements Renderer<Tile>{
	private Renderer<Zone> zoneRenderer;
	private RenderMode renderMode;
	public TileRenderer(RenderMode mode){
		this.zoneRenderer = new ZoneRenderer(mode);
		this.renderMode = mode;
	}

	@Override
	public void draw(Tile tile, GraphicsContext g) {

		
		if(renderMode==RenderMode.NORMAL){
			g.setFill(tile.getType().getColor());
		}else if(renderMode==RenderMode.POLLUTION){
			double red = tile.getPollution()/Rules.MAX;
			double blue = (255-tile.getPollution())/Rules.MAX;
			double green = ((255+tile.getPollution())/2)/Rules.MAX;
			double intensity = (tile.getPollution())/Rules.MAX;
			Color pollutionColor = new Color(red, green, blue, intensity);
			g.setFill(pollutionColor);
		}else if(renderMode==RenderMode.LANDVALUE){
			double red = 0;
			double blue = 0;
			double green = tile.getCurrentLandValue()/255;
			double intensity = 1;
			Color landValueColor = new Color(red, green, blue, intensity);
			g.setFill(landValueColor);
		}else if(renderMode==RenderMode.DENSITY){
			Color densityColor = new Color(1, 1, 1, 1);
			double level = 0;
			if(tile.getZone().getBuilding()!=null){
				level = tile.getZone().getBuilding().getDensity().getDensityLevel();
				double max = Density.VERYHIGH.getDensityLevel();
				double red = level/max;
				double blue = (level/(max*2));
				double green = 0.5;
				double alpha = 1;
				densityColor = new Color(red, green, blue, alpha);
			}
			g.setFill(densityColor);
		}else if(renderMode==RenderMode.POLICY){
			double value = tile.getOverlayValue();
			double red = value;
			double blue = 0;
			double green = value;
			if(value<0.5){
				red = 1.0;
				green = value*2.0;
				blue = 0.0;
			}else{
				red = 1.0-(value-0.5)*2.0;
				green = 1.0;
				blue = 0.0;
			}
			
			double intensity = 0.8;
			Color color = new Color(red, green, blue, intensity);
			g.setFill(color);
		}
		g.fillRect(tile.getPos().getX(), tile.getPos().getY(), 1, 1);
		Zone z = tile.getZone();
		if (z != null) {
			zoneRenderer.draw(z, g);
		}
		if(tile.getSelection().isSelected()){
			g.setLineWidth(0.095);
			//g.setStroke(tile.getSelection().getType().getColor().deriveColor(0.5, 1, 1, 0.5));
			g.setStroke(tile.getSelection().getType().getColor().brighter());
			g.strokeRect(tile.getPos().getX(), tile.getPos().getY(), 1, 1);
		}
			
		
	}


	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
		this.zoneRenderer.changeMode(mode);
	}

}
