package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WorldRenderer implements Renderer<World>{
	private Renderer<Tile> tileRender;
	private RenderMode renderMode;
	
	public WorldRenderer(RenderMode mode){
		tileRender = new TileRenderer(mode);
		this.renderMode = mode;
	}

	@Override
	public void draw(World world, GraphicsContext g) {
		g.setFill(Color.DARKGRAY);
		g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());
		Tile[] tiles = world.getTiles();
		for (int i = 0; i <tiles.length; i++) {
			tileRender.draw(tiles[i], g);
		}
	}

	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
		this.tileRender.changeMode(mode);
	}

}
