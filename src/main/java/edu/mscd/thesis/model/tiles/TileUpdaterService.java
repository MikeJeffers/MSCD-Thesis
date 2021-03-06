package edu.mscd.thesis.model.tiles;

import java.util.concurrent.ForkJoinPool;

import edu.mscd.thesis.model.World;

public class TileUpdaterService {
	private ForkJoinPool pool;
	private World w;

	public TileUpdaterService(World w) {
		this.pool = new ForkJoinPool();
		this.w = w;
	}

	public void runUpdates() {
		Tile[] tiles = w.getTiles();
		pool.invoke(new RecursiveTileEffector(w, 0, tiles.length));
		tiles = pool.invoke(new RecursiveTileUpdater(tiles, 0, tiles.length));
	}

}
