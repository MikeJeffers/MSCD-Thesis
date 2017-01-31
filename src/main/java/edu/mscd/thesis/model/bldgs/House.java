package edu.mscd.thesis.model.bldgs;

import java.io.File;
import edu.mscd.thesis.model.Pos2D;
import javafx.scene.image.Image;

public class House extends AbstractBuilding {

	public House(Pos2D site) {
		super(site);
		File file = new File("resources/house.png");
		Image img = new Image(file.toURI().toString());
		super.setImage(img);
		super.setWealthLevel(1);
		super.setMaxOccupancy(1);
	}

}
