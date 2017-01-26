package edu.mscd.thesis.model;

import java.util.Random;

import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;

public class WorldImpl implements World {
	private Tile[] tiles;
	private int cols, rows;
	private double scale;
	private double width;
	private double height;

	public WorldImpl(int sizeX, int sizeY, double scale) {
		int size = sizeX * sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.scale = scale;
		this.width = (sizeX*scale);
		this.height = (sizeY*scale);
		this.worldInit();
	}

	private void worldInit() {
		Random r = new Random();
		TileType[] types = TileType.values();
		ZoneFactory zFact = new ZoneFactoryImpl();
		for (int i = 0; i < tiles.length; i++) {
			Pos2D p = new Pos2D((i % cols) * scale, (i / cols) * scale);
			int typeSelection = r.nextInt(types.length);
			Tile t = new TileImpl(p, types[typeSelection], zFact);
			tiles[i] = t;
		}
	}

	@Override
	public void update() {
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].update();
		}

	}

	@Override
	public void draw(GraphicsContext g) {
		g.fillRect(0, 0, width, height);
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].draw(g, scale);
		}

	}

	@Override
	public Tile getTileAt(Pos2D pos) {
		if (!Util.isValidPos2D(pos, width, height)) {
			return null;
		}
		int x = (int) (Math.round(pos.getX() / scale));
		int y = (int) (Math.round(pos.getY() / scale));
		return tiles[cols * y + x];
	}

	@Override
	public Zone getZoneAt(Pos2D pos) {
		return getTileAt(pos).getZone();
	}
	
	

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public double getScale() {
		return this.scale;
	}

}
