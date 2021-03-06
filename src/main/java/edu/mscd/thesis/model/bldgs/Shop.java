package edu.mscd.thesis.model.bldgs;

import java.net.URL;

import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.tiles.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import javafx.scene.image.Image;

public class Shop extends PlaceOfWork {

	public Shop(Pos2D pos, TileType tileType, ZoneType zoneType, Density density) {
		super(pos, tileType, zoneType, density);
		this.changeDensity(density);
	}

	@Override
	public void changeDensity(Density density) {
		int level = density.getDensityLevel();
		URL url = this.getClass().getClassLoader().getResource("shop" + level + ".png");
		if(url!=null){
			Image img = new Image(url.toString());
			super.setImage(img);
		}
		super.setWealthLevel(level);
		super.setMaxOccupancy(level + 1);
		super.changeDensity(density);
	}

	@Override
	public String getLabelText() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nShop");
		sb.append(super.getLabelText());
		return sb.toString();

	}

}
