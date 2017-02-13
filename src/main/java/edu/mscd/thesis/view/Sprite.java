package edu.mscd.thesis.view;

import java.net.URL;

import edu.mscd.thesis.model.Pos2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public interface Sprite {
	
	Image getImage();
	boolean setImage(Image img);
	Pos2D getPos();
	Rectangle2D getRect();
	double getWidth();
	double getHeight();
	boolean setImage(URL url);

}
