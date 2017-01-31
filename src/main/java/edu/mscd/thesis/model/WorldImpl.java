package edu.mscd.thesis.model;

import java.util.Random;

import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneFactory;
import edu.mscd.thesis.model.zones.ZoneFactoryImpl;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
			Pos2D p = new Pos2D((i % cols), (i / cols));
			int typeSelection = r.nextInt(types.length);
			Tile t = new TileImpl(p, types[typeSelection], zFact);
			tiles[i] = t;
		}
	}

	@Override
	public void update() {
		//TODO
		//iterate through all zones: incr/decr value based on conditions
		
		for (int i = 0; i < tiles.length; i++) {
			Tile t = tiles[i];
			if(t.getZone().getZoneType()!=ZoneType.EMPTY){
				//getnearest of type, get dist, modify value of zone in tile as per ruleset
			}
			tiles[i].update();
		}

	}
	
	private Tile getNearestOfType(Tile t, ZoneType zt){
		double distance = 10000;
		Tile found = null;
		for(int i=0; i<tiles.length; i++){
			if(!tiles[i].equals(t)){
				if(tiles[i].getZone().getZoneType()==zt){
					double dist = tiles[i].getPos().distBetween(t.getPos());
					if(dist<distance){
						distance = dist;
						found = tiles[i];
					}
				}
					
			}
		}
		return found;
	}

	@Override
	public void draw(GraphicsContext g) {
		g.setFill(Color.DARKGRAY);
		g.fillRect(0, 0, g.getCanvas().getWidth(),  g.getCanvas().getHeight());
		for (int i = 0; i < tiles.length; i++) {
			tiles[i].draw(g);
		}
	}

	@Override
	public Tile getTileAt(Pos2D pos) {
		if (!Util.isValidPos2D(pos, cols, rows)) {
			return null;
		}
		int x = (int) (Math.floor(pos.getX()));
		int y = (int) (Math.floor(pos.getY()));
		return tiles[cols * y + x];
	}

	@Override
	public Zone getZoneAt(Pos2D pos) {
		return getTileAt(pos).getZone();
	}

	@Override
	public City getCity() {
		// TODO Auto-generated method stub
		return null;
	}

}
