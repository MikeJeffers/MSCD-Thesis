package edu.mscd.thesis.model;

import java.util.Collection;

import edu.mscd.thesis.view.Sprite;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public abstract class AbstractBuilding implements Building, Sprite{
	protected Pos2D pos;
	private Image image;
	private Rectangle2D rect;
	private double width;
	private double height;
	

	@Override
	public boolean setImage(String filePath) {
		try{
			this.image = new Image(filePath);
			return true;
		}catch(Exception e){
			System.err.println(e);
			return false;
		}
	}

	@Override
	public boolean setImage(Image img) {
		this.image = img;
		return true;
	}


	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public Rectangle2D getRect() {
		return this.rect;
	}

	@Override
	public double getWidth() {
		return this.width;
	}

	@Override
	public double getHeight() {
		return this.height;
	}

	@Override
	public Pos2D getPos() {
		return this.pos;
	}



}
