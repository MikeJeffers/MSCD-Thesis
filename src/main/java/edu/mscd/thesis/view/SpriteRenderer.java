package edu.mscd.thesis.view;

import javafx.scene.canvas.GraphicsContext;

public class SpriteRenderer implements Renderer<Sprite>{
	
	public SpriteRenderer(){
		
	}

	@Override
	public void draw(Sprite sprite, GraphicsContext g) {
		if (!sprite.getImage().isError() && !sprite.getImage().isBackgroundLoading()) {
			g.drawImage(sprite.getImage(), sprite.getPos().getX(), sprite.getPos().getY(), 1, 1);
		}
		
	}

}
