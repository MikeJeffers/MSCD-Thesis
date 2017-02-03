package edu.mscd.thesis.model.bldgs;

import java.io.File;


import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import javafx.scene.image.Image;

public class Factory extends PlaceOfWork{
	private Density density;

	public Factory(Pos2D pos, TileType tileType, ZoneType zoneType, Density density) {
		super(pos, tileType, zoneType);
		this.changeDensity(density);
	}
	
	@Override
	public Density getDensity() {
		return this.density;
	}


	@Override
	public void changeDensity(Density density) {
		this.density = density;
		int level = density.getDensityLevel();
		File file = new File("resources/factory"+level+".png");
		Image img = new Image(file.toURI().toString());
		super.setImage(img);
		super.setWealthLevel(level);
		super.setMaxOccupancy(level+1);
		
	}

}
