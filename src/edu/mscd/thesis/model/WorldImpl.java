package edu.mscd.thesis.model;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;

public class WorldImpl implements World{
	private Tile[] tiles;
	private int cols, rows;
	
	public WorldImpl(int sizeX, int sizeY){
		int size = sizeX*sizeY;
		tiles = new Tile[size];
		this.rows = sizeY;
		this.cols = sizeX;
		this.worldInit();
	}
	
	private void worldInit(){
		Random r = new Random();
		TileType[] types = TileType.values();
		ZoneFactory zFact = new ZoneFactoryImpl();
		for(int i=0; i<tiles.length;i++){
			Pos2D p = new Pos2D(i/cols, i%cols);
			int typeSelection = r.nextInt(types.length);
			Tile t = new TileImpl(p, types[typeSelection], zFact);
			t.setZone(ZoneType.EMPTY);
			tiles[i] = t;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(GraphicsContext g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Tile getTileAt(Pos2D pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Zone getZoneAt(Pos2D pos) {
		// TODO Auto-generated method stub
		return null;
	}

}
