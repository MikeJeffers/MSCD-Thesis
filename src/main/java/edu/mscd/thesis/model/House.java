package edu.mscd.thesis.model;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class House extends AbstractBuilding {
	private Collection<Person> occupants;

	public House(Pos2D site) {
		super(site);
		this.occupants = new HashSet<Person>();
		File file = new File("resources/house.png");
		Image img = new Image(file.toURI().toString());
		super.setImage(img);

	}

	@Override
	public Collection<Person> getOccupants() {
		// TODO Auto-generated method stub
		return occupants;
	}

}
