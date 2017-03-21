package edu.mscd.thesis.model;

import java.util.List;

import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.zones.Zone;
import edu.mscd.thesis.model.zones.ZoneType;
import edu.mscd.thesis.util.Util;

public class WorldReduced implements World{
	private Tile[] tiles;
	private int cols, rows;
	private City city;

	public WorldReduced(World w) {
		this.city = new CityReduced(w.getCity());
		this.rows = w.height();
		this.cols = w.width();
		Tile[] originalTiles = w.getTiles();
		this.tiles = new Tile[originalTiles.length];
		for(int i=0; i<originalTiles.length; i++){
			tiles[i] = new TileReduced(originalTiles[i]);
		}
	}



	@Override
	public void update() {
		//DOES NOTHING
	}


	@Override
	public boolean setZoneAt(Pos2D pos, ZoneType zt) {
		Tile t = this.getTileAt(pos);
		if (t == null) {
			return false;
		}
		return t.setZone(zt);
	}

	@Override
	public boolean setAllZonesAround(Pos2D pos, ZoneType zt, int radius, boolean squareSelect) {
		Tile t = this.getTileAt(pos);
		if (t == null) {
			return false;
		}
		List<Tile> tilesInRange;
		if (squareSelect) {
			tilesInRange = Util.getNeighborsManhattanDist(t, tiles, radius, cols, rows);
		} else {
			tilesInRange = Util.getNeighborsCircularDist(t, tiles, radius);
		}
		for (Tile reZone : tilesInRange) {
			reZone.setZone(zt);
		}
		return true;
	}

	
	@Override
	public Tile[] getTiles(){
		return this.tiles;
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
		return this.city;
	}

	@Override
	public void userStateChange(UserData userData) {
		this.setAllZonesAround(userData.getClickLocation(), userData.getZoneSelection(), userData.getRadius(), userData.isSquare());
		
	}

	@Override
	public World getWorld() {
		for(int i=0; i<tiles.length; i++){
			tiles[i] = new TileReduced(tiles[i]);
		}
		return this;
	}

	@Override
	public int width() {
		return this.cols;
	}

	@Override
	public int height() {
		return this.rows;
	}


}
