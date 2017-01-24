package edu.mscd.thesis.model;

import java.util.Random;

import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;

public class WorldImpl implements World {
	private Tile[] tiles;
	private int cols, rows;

	public WorldImpl(int sizeX, int sizeY) {
		int size = sizeX * sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.worldInit();
	}

	private void worldInit() {
		Random r = new Random();
		TileType[] types = TileType.values();
		ZoneFactory zFact = new ZoneFactoryImpl();
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = new Pos2D(i / cols, i % cols);
			int typeSelection = r.nextInt(types.length);
			Tile t = new TileImpl(p, types[typeSelection], zFact);
			tiles[i] = t;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(GraphicsContext g, double scale) {
		g.fillRect(0, 0, scale * cols, scale * rows);
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].draw(g, scale);
		}

	}

	@Override
	public Tile getTileAt(Pos2D pos) {
		if (!Util.isValidPos2D(pos, cols, rows)) {
			return null;
		}
		int x = pos.getX();
		int y = pos.getY();
		return tiles[cols * y + x];
	}

	@Override
	public Zone getZoneAt(Pos2D pos) {
		return getTileAt(pos).getZone();
	}

}
