package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WorldRenderer implements Renderer<World>{
	private Renderer<Tile> tileRender;
	
	public WorldRenderer(){
		tileRender = new TileRenderer();
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

}
