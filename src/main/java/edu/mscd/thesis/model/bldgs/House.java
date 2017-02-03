package edu.mscd.thesis.model.bldgs;

import java.io.File;

import edu.mscd.thesis.model.Citizen;
import edu.mscd.thesis.model.Person;
import edu.mscd.thesis.model.Pos2D;
import edu.mscd.thesis.model.TileType;
import edu.mscd.thesis.model.zones.Density;
import edu.mscd.thesis.model.zones.ZoneType;
import javafx.scene.image.Image;

public class House extends Home {
	private Density density;


	public House(Pos2D pos, TileType tileType, ZoneType zoneType, Density density) {
		super(pos, tileType, zoneType);
		this.changeDensity(density);
		this.fillWithPeople();
	}

	@Override
	public Density getDensity() {
		return this.density;
	}


	@Override
	public double update(double growth){
		//TODO only add people if there is global Demand
		fillWithPeople();
		return super.update(growth);
	}

	@Override
	public void changeDensity(Density density) {
		this.density = density;
		int level = density.getDensityLevel();
		File file = new File("resources/house"+level+".png");
		Image img = new Image(file.toURI().toString());
		super.setImage(img);
		super.setWealthLevel(level);
		super.setMaxOccupancy(level);
		
	}
	
	private void fillWithPeople(){
		for(int i=0; i<super.getMaxOccupants(); i++){
			Person p = new Citizen();
			this.addOccupant(p);
		}
	}

}
