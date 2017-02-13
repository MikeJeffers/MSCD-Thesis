package edu.mscd.thesis.util;

import java.util.concurrent.ForkJoinPool;

import edu.mscd.thesis.model.Tile;
import edu.mscd.thesis.model.World;

public class TileUpdaterService{
	private ForkJoinPool pool;
	private World w;

	public TileUpdaterService(World w){
		this.pool = new ForkJoinPool();
		this.w= w;
	}

	public void runUpdates() {
		Tile[] tiles = w.getTiles();
		
		tiles = pool.invoke(new RecursiveTileUpdater(tiles, 0, tiles.length));
		System.out.println("done?");
			
		
	}
	
	

}
