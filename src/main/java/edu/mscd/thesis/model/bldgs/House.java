package edu.mscd.thesis.model.bldgs;

import java.io.File;

import edu.mscd.thesis.model.Citizen;
import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.zones.Density;
import javafx.scene.image.Image;

public class House extends AbstractBuilding {
	Density density;

	public House(Pos2D site, Density density) {
		super(site);
		this.density = density;
		int level = density.getDensityLevel();
		File file = new File("resources/house"+level+".png");
		Image img = new Image(file.toURI().toString());
		super.setImage(img);
		super.setWealthLevel(level);
		super.setMaxOccupancy(level);
		for(int i=0; i<level; i++){
			Person p = new Citizen();
			p.setHome(this);
			super.addOccupant(p);
		}
	}

	@Override
	public Density getDensity() {
		return this.density;
	}

}
