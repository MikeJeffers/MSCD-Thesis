package edu.mscd.thesis.model;

import java.net.URL;
import java.util.Objects;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class AbstractBuilding implements Building{
	private Pos2D pos;
	private Image image;
	private Rectangle2D rect;
	private double width;
	private double height;
	
	public AbstractBuilding(Pos2D pos){
		this.pos = pos;
	}
	
	@Override
	public void render(GraphicsContext g){
		if(!this.image.isError() && !this.image.isBackgroundLoading()){
			g.drawImage(this.image, pos.getX(), pos.getY(), 1, 1);
		}
		
	}
	

	@Override
	public boolean setImage(URL url) {
		System.out.println(url.toString());
		try{
			this.image = new Image(url.getPath());
			if(this.image.isError()){
				System.err.println(this.image.getException().getMessage());
			}
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
	
	void setPos(Pos2D pos){
		this.pos = pos;
	}

	
	@Override
	public boolean equals(Object o){
		if(o==null){
			return false;
		}
		if(o instanceof Building){
			Building b = (Building) o;
			if(b.getPos()!=null){
				return b.getPos().equals(this.getPos());
			}else{
				return b.getPos()==null && this.getPos()==null;
			}
			
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(pos);
	}

	
	@Override
	public String toString(){
		return "Building:{at="+this.pos.toString()+" img="+this.image.toString()+"}";
	}

}
