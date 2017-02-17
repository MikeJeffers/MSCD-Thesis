package edu.mscd.thesis.view;

import javafx.scene.canvas.GraphicsContext;

public class SpriteRenderer implements Renderer<Sprite>{
	private RenderMode renderMode;
	
	public SpriteRenderer(RenderMode mode){
		this.renderMode = mode;
	}

	@Override
	public void draw(Sprite sprite, GraphicsContext g) {
		if(sprite.getImage()!=null){
			if (!sprite.getImage().isError() && !sprite.getImage().isBackgroundLoading()) {
				g.drawImage(sprite.getImage(), sprite.getPos().getX(), sprite.getPos().getY(), 1, 1);
			}
		}else{
			//TODO image null, draw place holder
		}

		
	}

	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
	}

}
